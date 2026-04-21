package com.pbl_3project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.pbl_3project.dto.CartItem;
import com.pbl_3project.util.DatabaseConnection;

public class OrderDAO {

    // Hàm lưu hóa đơn với Transaction (Đảm bảo an toàn dữ liệu 100%)
    public boolean createOrder(String customerInfo, double totalAmount, List<CartItem> cartItems) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtDetail = null;
        PreparedStatement pstmtUpdateStock = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            // 🛑 TẮT AUTO-COMMIT ĐỂ BẮT ĐẦU TRANSACTION
            conn.setAutoCommit(false);

            // ==========================================
            // 1. TẠO HÓA ĐƠN MỚI
            // ==========================================
            // Tạm thời lưu số điện thoại khách vào cột customer_phone (Hoặc bro có thể sửa
            // thành cột khác tùy DB)
            String sqlOrder = "INSERT INTO [Order] (customer_phone, total_amount, created_at) VALUES (?, ?, GETDATE())";

            // Statement.RETURN_GENERATED_KEYS giúp lấy lại ID của hóa đơn vừa tự động tăng
            pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            pstmtOrder.setString(1, customerInfo);
            pstmtOrder.setDouble(2, totalAmount);
            pstmtOrder.executeUpdate();

            // Lấy Order ID
            rs = pstmtOrder.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            } else {
                throw new SQLException("Không thể tạo mã Hóa đơn mới!");
            }

            // ==========================================
            // 2. LƯU CHI TIẾT & TRỪ TỒN KHO
            // ==========================================
            String sqlDetail = "INSERT INTO Order_Detail (order_id, sku_code, quantity, price) VALUES (?, ?, ?, ?)";
            pstmtDetail = conn.prepareStatement(sqlDetail);

            String sqlUpdateStock = "UPDATE Product_Variant SET stock_quantity = stock_quantity - ? WHERE sku_code = ?";
            pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock);

            for (CartItem item : cartItems) {
                // Chuẩn bị lệnh thêm vào Order_Detail
                pstmtDetail.setInt(1, orderId);
                pstmtDetail.setString(2, item.getSku());
                pstmtDetail.setInt(3, item.getQuantity());
                pstmtDetail.setDouble(4, item.getPrice());
                pstmtDetail.addBatch(); // Gom lệnh lại chạy 1 lần cho nhanh

                // Chuẩn bị lệnh trừ tồn kho
                pstmtUpdateStock.setInt(1, item.getQuantity());
                pstmtUpdateStock.setString(2, item.getSku());
                pstmtUpdateStock.addBatch();
            }

            // Thực thi hàng loạt (Batch Execution)
            pstmtDetail.executeBatch();
            pstmtUpdateStock.executeBatch();

            // 🟢 NẾU TẤT CẢ ĐỀU KHÔNG CÓ LỖI -> XÁC NHẬN LƯU VÀO DB
            conn.commit();
            return true;

        } catch (SQLException e) {
            // 🔴 NẾU CÓ LỖI (Hết hàng, rớt mạng...) -> HOÀN TÁC TOÀN BỘ (ROLLBACK)
            if (conn != null) {
                conn.rollback();
            }
            e.printStackTrace();
            throw new SQLException("Thanh toán thất bại, đã hủy giao dịch: " + e.getMessage());
        } finally {
            // Đóng toàn bộ kết nối để giải phóng RAM
            if (rs != null)
                rs.close();
            if (pstmtOrder != null)
                pstmtOrder.close();
            if (pstmtDetail != null)
                pstmtDetail.close();
            if (pstmtUpdateStock != null)
                pstmtUpdateStock.close();
            if (conn != null) {
                conn.setAutoCommit(true); // Bật lại trạng thái bình thường
                DatabaseConnection.closeConnection(conn);
            }
        }
    }

    public boolean createOrderOnline(int customerId, String phone, double totalAmount, List<CartItem> cartItems)
            throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtDetail = null;
        PreparedStatement pstmtUpdateStock = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Giao dịch an toàn

            // 1. TẠO HÓA ĐƠN MỚI với trạng thái "Chưa thanh toán"
            String sqlOrder = "INSERT INTO [Order] (customer_id, customer_phone, total_amount, status, created_at) " +
                    "VALUES (?, ?, ?, N'Chưa thanh toán', GETDATE())";

            pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            pstmtOrder.setInt(1, customerId);
            pstmtOrder.setString(2, phone);
            pstmtOrder.setDouble(3, totalAmount);
            pstmtOrder.executeUpdate();

            // Lấy ID hóa đơn vừa tạo
            rs = pstmtOrder.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            } else {
                throw new SQLException("Lỗi hệ thống: Không thể tạo ID Hóa đơn!");
            }

            // 2. LƯU CHI TIẾT SẢN PHẨM & TRỪ KHO
            String sqlDetail = "INSERT INTO Order_Detail (order_id, sku_code, quantity, price) VALUES (?, ?, ?, ?)";
            pstmtDetail = conn.prepareStatement(sqlDetail);

            String sqlUpdateStock = "UPDATE Product_Variant SET stock_quantity = stock_quantity - ? WHERE sku_code = ?";
            pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock);

            for (CartItem item : cartItems) {
                // Thêm vào chi tiết hóa đơn
                pstmtDetail.setInt(1, orderId);
                pstmtDetail.setString(2, item.getSku());
                pstmtDetail.setInt(3, item.getQuantity());
                pstmtDetail.setDouble(4, item.getPrice());
                pstmtDetail.addBatch();

                // Trừ tồn kho (Vì lúc bỏ vào giỏ Online, hệ thống không khóa kho)
                pstmtUpdateStock.setInt(1, item.getQuantity());
                pstmtUpdateStock.setString(2, item.getSku());
                pstmtUpdateStock.addBatch();
            }

            pstmtDetail.executeBatch();
            pstmtUpdateStock.executeBatch();

            // Nếu trót lọt tất cả -> Lưu vào Database
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null)
                conn.rollback(); // Có lỗi mạng/hết hàng -> Hoàn tác mọi thứ
            throw new SQLException("Đặt hàng thất bại: " + e.getMessage());
        } finally {
            if (rs != null)
                rs.close();
            if (pstmtOrder != null)
                pstmtOrder.close();
            if (pstmtDetail != null)
                pstmtDetail.close();
            if (pstmtUpdateStock != null)
                pstmtUpdateStock.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }
}