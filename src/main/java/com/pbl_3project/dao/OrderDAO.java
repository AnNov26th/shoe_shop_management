package com.pbl_3project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.pbl_3project.dto.CartItem;
import com.pbl_3project.util.DatabaseConnection;

public class OrderDAO {

    public boolean createOrder(String customerInfo, double totalAmount, List<CartItem> cartItems, String status) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtDetail = null;
        PreparedStatement pstmtUpdateStock = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // Sinh mã đơn tự động
            String orderCode = "ORD" + System.currentTimeMillis();

            // ĐÃ FIX: Có đủ 6 dấu chấm hỏi cho 6 cột
            String sqlOrder = "INSERT INTO [Order] (order_code, customer_id, customer_phone, subtotal, total_amount, final_amount, created_at, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, GETDATE(), ?)";

            pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);

            // Xử lý logic truyền ID hoặc Số điện thoại từ POS
            Integer customerId = null;
            String customerPhone = null;
            try {
                customerId = Integer.parseInt(customerInfo);
            } catch (NumberFormatException e) {
                customerPhone = customerInfo;
            }

            // Gán giá trị theo đúng thứ tự 1-6
            pstmtOrder.setString(1, orderCode);
            if (customerId != null) {
                pstmtOrder.setInt(2, customerId);
            } else {
                pstmtOrder.setNull(2, java.sql.Types.INTEGER);
            }
            pstmtOrder.setString(3, customerPhone);
            pstmtOrder.setDouble(4, totalAmount); // subtotal
            pstmtOrder.setDouble(5, totalAmount); // total_amount
            pstmtOrder.setDouble(6, totalAmount); // final_amount
            pstmtOrder.setString(7, status);

            int affectedRows = pstmtOrder.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("Lỗi: Không thể tạo hóa đơn.");

            // Lấy ID đơn hàng mới tạo
            rs = pstmtOrder.getGeneratedKeys();
            int orderId = -1;
            if (rs.next())
                orderId = rs.getInt(1);

            // Xử lý Chi tiết đơn hàng và Trừ kho
            String sqlDetail = "INSERT INTO [Order_Detail] (order_id, variant_id, quantity, unit_price, subtotal) VALUES (?, (SELECT id FROM [Product_Variant] WHERE sku_code = ?), ?, ?, ?)";
            String sqlUpdateStock = "UPDATE [Product_Variant] SET stock_quantity = stock_quantity - ? WHERE sku_code = ?";

            pstmtDetail = conn.prepareStatement(sqlDetail);
            pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock);

            for (CartItem item : cartItems) {
                // Lô chi tiết
                pstmtDetail.setInt(1, orderId);
                pstmtDetail.setString(2, item.getSku());
                pstmtDetail.setInt(3, item.getQuantity());
                pstmtDetail.setDouble(4, item.getPrice()); // unit_price
                pstmtDetail.setDouble(5, item.getPrice() * item.getQuantity()); // subtotal
                pstmtDetail.addBatch();

                // Lô trừ kho
                pstmtUpdateStock.setInt(1, item.getQuantity());
                pstmtUpdateStock.setString(2, item.getSku());
                pstmtUpdateStock.addBatch();
            }

            pstmtDetail.executeBatch();
            pstmtUpdateStock.executeBatch();

            // Lưu dữ liệu
            conn.commit();
            return true;

        } catch (SQLException ex) {
            if (conn != null)
                conn.rollback(); // Hủy bỏ nếu có lỗi
            throw ex;
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

    public boolean createOrderOnline(int customerId, String customerPhone, double subtotal, double discountAmount, double finalAmount, Integer promotionId, List<CartItem> cartItems, String status)
            throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtDetail = null;
        PreparedStatement pstmtUpdateStock = null;
        PreparedStatement pstmtUpdatePromo = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // Sinh mã đơn tự động
            String orderCode = "ORD" + System.currentTimeMillis();

            String sqlOrder = "INSERT INTO [Order] (order_code, customer_id, customer_phone, subtotal, discount_amount, final_amount, created_at, status, promotion_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?, GETDATE(), ?, ?)";

            pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);

            pstmtOrder.setString(1, orderCode);
            pstmtOrder.setInt(2, customerId);
            pstmtOrder.setString(3, customerPhone);
            pstmtOrder.setDouble(4, subtotal);
            pstmtOrder.setDouble(5, discountAmount);
            pstmtOrder.setDouble(6, finalAmount);
            pstmtOrder.setString(7, status);
            if (promotionId != null && promotionId > 0) {
                pstmtOrder.setInt(8, promotionId);
            } else {
                pstmtOrder.setNull(8, java.sql.Types.INTEGER);
            }

            int affectedRows = pstmtOrder.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("Lỗi: Không thể tạo hóa đơn.");

            // Lấy ID đơn hàng mới tạo
            rs = pstmtOrder.getGeneratedKeys();
            int orderId = -1;
            if (rs.next())
                orderId = rs.getInt(1);

            // Xử lý Chi tiết đơn hàng và Trừ kho
            String sqlDetail = "INSERT INTO [Order_Detail] (order_id, variant_id, quantity, unit_price, subtotal) VALUES (?, (SELECT id FROM [Product_Variant] WHERE sku_code = ?), ?, ?, ?)";
            String sqlUpdateStock = "UPDATE [Product_Variant] SET stock_quantity = stock_quantity - ? WHERE sku_code = ?";

            pstmtDetail = conn.prepareStatement(sqlDetail);
            pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock);

            for (CartItem item : cartItems) {
                // Lô chi tiết
                pstmtDetail.setInt(1, orderId);
                pstmtDetail.setString(2, item.getSku());
                pstmtDetail.setInt(3, item.getQuantity());
                pstmtDetail.setDouble(4, item.getPrice()); // unit_price
                pstmtDetail.setDouble(5, item.getPrice() * item.getQuantity()); // subtotal
                pstmtDetail.addBatch();

                // Lô trừ kho
                pstmtUpdateStock.setInt(1, item.getQuantity());
                pstmtUpdateStock.setString(2, item.getSku());
                pstmtUpdateStock.addBatch();
            }

            pstmtDetail.executeBatch();
            pstmtUpdateStock.executeBatch();

            // 3. Cập nhật lượt dùng mã (nếu có)
            if (promotionId != null && promotionId > 0) {
                String sqlPromo = "UPDATE [Promotion] SET current_usage = ISNULL(current_usage, 0) + 1 WHERE id = ?";
                pstmtUpdatePromo = conn.prepareStatement(sqlPromo);
                pstmtUpdatePromo.setInt(1, promotionId);
                pstmtUpdatePromo.executeUpdate();
            }

            // Lưu dữ liệu
            conn.commit();
            return true;

        } catch (SQLException ex) {
            if (conn != null)
                conn.rollback(); // Hủy bỏ nếu có lỗi
            throw ex;
        } finally {
            if (rs != null)
                rs.close();
            if (pstmtOrder != null)
                pstmtOrder.close();
            if (pstmtDetail != null)
                pstmtDetail.close();
            if (pstmtUpdateStock != null)
                pstmtUpdateStock.close();
            if (pstmtUpdatePromo != null)
                pstmtUpdatePromo.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }

    /**
     * Lấy đơn hàng của 1 khách hàng cụ thể (Đã thêm cột ID ẩn)
     */
    public DefaultTableModel getAllOrdersByCustomer(int customerId) throws SQLException {
        String[] cols = { "ID", "Mã Đơn", "Số điện thoại", "Tổng Thanh Toán", "Ngày Đặt", "Trạng Thái" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        String sql = "SELECT id, order_code, customer_phone, total_amount, created_at, status FROM [Order] WHERE customer_id = ? ORDER BY created_at DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("order_code"),
                        rs.getString("customer_phone") == null ? "Không có" : rs.getString("customer_phone"),
                        String.format("%,.0f VNĐ", rs.getDouble("total_amount")),
                        rs.getTimestamp("created_at"),
                        rs.getString("status")
                });
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            if (conn != null)
                DatabaseConnection.closeConnection(conn);
        }
        return model;
    }

    /**
     * Lấy tất cả đơn hàng (cho phía Admin quản lý)
     */
    public DefaultTableModel getAllOrders(String keyword) throws SQLException {
        String[] cols = { "ID", "Mã Đơn", "Khách Hàng", "SĐT", "Tổng Tiền", "Ngày Đặt", "Trạng Thái" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        String sql = "SELECT o.id, o.order_code, cp.full_name, o.customer_phone, o.total_amount, o.created_at, o.status "
                +
                "FROM [Order] o " +
                "LEFT JOIN [Customer_Profile] cp ON o.customer_id = cp.user_id " +
                "WHERE o.order_code LIKE ? OR o.customer_phone LIKE ? " +
                "ORDER BY o.created_at DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("order_code"),
                        rs.getString("full_name") == null ? "Khách vãng lai" : rs.getString("full_name"),
                        rs.getString("customer_phone"),
                        String.format("%,.0f VNĐ", rs.getDouble("total_amount")),
                        rs.getTimestamp("created_at"),
                        rs.getString("status")
                });
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            if (conn != null)
                DatabaseConnection.closeConnection(conn);
        }
        return model;
    }

    public DefaultTableModel getOrderDetails(int orderId) throws SQLException {
        String[] cols = { "Sản phẩm", "Mã SKU", "Size", "Màu", "Số lượng", "Đơn giá", "Thành tiền" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        String sql = "SELECT p.name as product_name, pv.sku_code, pv.size, pv.color, od.quantity, od.unit_price " +
                "FROM [Order_Detail] od " +
                "JOIN [Product_Variant] pv ON od.variant_id = pv.id " +
                "JOIN [Product] p ON pv.product_id = p.id " +
                "WHERE od.order_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                double price = rs.getDouble("unit_price");
                int qty = rs.getInt("quantity");
                model.addRow(new Object[] {
                        rs.getString("product_name"),
                        rs.getString("sku_code"),
                        rs.getString("size"),
                        rs.getString("color"),
                        qty,
                        String.format("%,.0f", price),
                        String.format("%,.0f", price * qty)
                });
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            if (conn != null)
                DatabaseConnection.closeConnection(conn);
        }
        return model;
    }

    public boolean updateOrderStatus(int orderId, String newStatus) throws SQLException {
        String sql = "UPDATE [Order] SET status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, orderId);
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null)
                pstmt.close();
            if (conn != null)
                DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Hủy đơn hàng và HOÀN LẠI TỒN KHO
     */
    public boolean cancelOrder(int orderId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtStatus = null;
        PreparedStatement pstmtGetItems = null;
        PreparedStatement pstmtUpdateStock = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Cập nhật trạng thái sang 'Đã hủy'
            String sqlStatus = "UPDATE [Order] SET status = N'Đã hủy' WHERE id = ? AND status != N'Đã hủy'";
            pstmtStatus = conn.prepareStatement(sqlStatus);
            pstmtStatus.setInt(1, orderId);
            int updated = pstmtStatus.executeUpdate();

            if (updated > 0) {
                // 2. Lấy danh sách sản phẩm để hoàn kho
                String sqlGetItems = "SELECT variant_id, quantity FROM [Order_Detail] WHERE order_id = ?";
                pstmtGetItems = conn.prepareStatement(sqlGetItems);
                pstmtGetItems.setInt(1, orderId);
                rs = pstmtGetItems.executeQuery();

                String sqlUpdateStock = "UPDATE [Product_Variant] SET stock_quantity = stock_quantity + ? WHERE id = ?";
                pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock);

                while (rs.next()) {
                    pstmtUpdateStock.setInt(1, rs.getInt("quantity"));
                    pstmtUpdateStock.setInt(2, rs.getInt("variant_id"));
                    pstmtUpdateStock.addBatch();
                }
                pstmtUpdateStock.executeBatch();

                conn.commit();
                return true;
            }
            return false;
        } catch (SQLException ex) {
            if (conn != null) conn.rollback();
            throw ex;
        } finally {
            if (rs != null) rs.close();
            if (pstmtStatus != null) pstmtStatus.close();
            if (pstmtGetItems != null) pstmtGetItems.close();
            if (pstmtUpdateStock != null) pstmtUpdateStock.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }

    /**
     * Xử lý thanh toán
     */
    public boolean processPayment(int orderId, String method, double amount) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtPay = null;
        PreparedStatement pstmtUpdateOrder = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Ghi nhận vào bảng Payment
            String sqlPay = "INSERT INTO [Payment] (order_id, method, amount, status, paid_at) VALUES (?, ?, ?, N'Hoàn thành', GETDATE())";
            pstmtPay = conn.prepareStatement(sqlPay);
            pstmtPay.setInt(1, orderId);
            pstmtPay.setString(2, method);
            pstmtPay.setDouble(3, amount);
            pstmtPay.executeUpdate();

            // 2. Cập nhật trạng thái đơn hàng
            String sqlUpdateOrder = "UPDATE [Order] SET status = N'Đã thanh toán' WHERE id = ?";
            pstmtUpdateOrder = conn.prepareStatement(sqlUpdateOrder);
            pstmtUpdateOrder.setInt(1, orderId);
            pstmtUpdateOrder.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) conn.rollback();
            throw ex;
        } finally {
            if (pstmtPay != null) pstmtPay.close();
            if (pstmtUpdateOrder != null) pstmtUpdateOrder.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }

    public boolean confirmReceipt(int orderId) throws SQLException {
        return updateOrderStatus(orderId, "Hoàn thành");
    }

    public boolean requestReturn(int orderId) throws SQLException {
        return updateOrderStatus(orderId, "Yêu cầu Đổi/Trả");
    }
}