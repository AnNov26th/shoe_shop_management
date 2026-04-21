package com.pbl_3project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.pbl_3project.util.DatabaseConnection;

public class CartDAO {

    // 1. Hàm tìm/tạo Giỏ hàng dùng chung
    public int getOrCreateCart(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int cartId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            String sqlCheck = "SELECT id FROM Cart WHERE user_id = ?";
            pstmt = conn.prepareStatement(sqlCheck);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                cartId = rs.getInt("id");
            } else {
                String sqlInsert = "INSERT INTO Cart (user_id) VALUES (?)";
                pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();

                ResultSet rsKeys = pstmt.getGeneratedKeys();
                if (rsKeys.next()) {
                    cartId = rsKeys.getInt(1);
                }
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return cartId;
    }

    // =========================================================================
    // LUỒNG 1: KHÁCH HÀNG ONLINE (CHỈ BỎ VÀO GIỎ - KHÔNG TRỪ KHO)
    // =========================================================================
    public boolean addToCartOnline(int userId, String skuCode, int quantity) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            int cartId = getOrCreateCart(userId);

            // Lấy variant_id từ sku_code
            String sqlGetVariant = "SELECT id FROM Product_Variant WHERE sku_code = ?";
            pstmt = conn.prepareStatement(sqlGetVariant);
            pstmt.setString(1, skuCode);
            ResultSet rs = pstmt.executeQuery();
            int variantId = -1;
            if (rs.next())
                variantId = rs.getInt("id");
            if (variantId == -1)
                throw new SQLException("Sản phẩm không tồn tại!");
            pstmt.close();

            // Kiểm tra xem món này đã có trong giỏ chưa
            String sqlCheckItem = "SELECT quantity FROM Cart_Item WHERE cart_id = ? AND variant_id = ?";
            pstmt = conn.prepareStatement(sqlCheckItem);
            pstmt.setInt(1, cartId);
            pstmt.setInt(2, variantId);
            ResultSet rsItem = pstmt.executeQuery();

            if (rsItem.next()) {
                // Cập nhật số lượng (expires_at vẫn để NULL)
                String sqlUpdateItem = "UPDATE Cart_Item SET quantity = quantity + ? WHERE cart_id = ? AND variant_id = ?";
                PreparedStatement pstmtUpd = conn.prepareStatement(sqlUpdateItem);
                pstmtUpd.setInt(1, quantity);
                pstmtUpd.setInt(2, cartId);
                pstmtUpd.setInt(3, variantId);
                pstmtUpd.executeUpdate();
                pstmtUpd.close();
            } else {
                // Thêm mới vào giỏ (expires_at = NULL để Máy hút bụi bỏ qua)
                String sqlInsertItem = "INSERT INTO Cart_Item (cart_id, variant_id, quantity, expires_at) VALUES (?, ?, ?, NULL)";
                PreparedStatement pstmtIns = conn.prepareStatement(sqlInsertItem);
                pstmtIns.setInt(1, cartId);
                pstmtIns.setInt(2, variantId);
                pstmtIns.setInt(3, quantity);
                pstmtIns.executeUpdate();
                pstmtIns.close();
            }
            return true;
        } finally {
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }

    // =========================================================================
    // LUỒNG 2: NHÂN VIÊN BÁN QUẦY (POS) - GIỮ CHỖ 5 PHÚT & TRỪ KHO NGAY
    // =========================================================================
    public boolean holdItemForPOS(int staffId, String skuCode, int quantity) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtCheckStock = null;
        PreparedStatement pstmtUpdateStock = null;
        PreparedStatement pstmtCartItem = null;
        ResultSet rsStock = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int cartId = getOrCreateCart(staffId);

            // Kiểm tra tồn kho
            String sqlCheck = "SELECT id, stock_quantity FROM Product_Variant WHERE sku_code = ?";
            pstmtCheckStock = conn.prepareStatement(sqlCheck);
            pstmtCheckStock.setString(1, skuCode);
            rsStock = pstmtCheckStock.executeQuery();

            int variantId = -1;
            int currentStock = 0;
            if (rsStock.next()) {
                variantId = rsStock.getInt("id");
                currentStock = rsStock.getInt("stock_quantity");
            }

            if (currentStock < quantity) {
                throw new SQLException("Kho không đủ hàng! Chỉ còn " + currentStock + " đôi.");
            }

            // TRỪ KHO NGAY LẬP TỨC
            String sqlDeduct = "UPDATE Product_Variant SET stock_quantity = stock_quantity - ? WHERE id = ?";
            pstmtUpdateStock = conn.prepareStatement(sqlDeduct);
            pstmtUpdateStock.setInt(1, quantity);
            pstmtUpdateStock.setInt(2, variantId);
            pstmtUpdateStock.executeUpdate();

            // THÊM VÀO GIỎ CỦA NHÂN VIÊN VÀ HẸN GIỜ 5 PHÚT
            String sqlCheckItem = "SELECT quantity FROM Cart_Item WHERE cart_id = ? AND variant_id = ?";
            pstmtCartItem = conn.prepareStatement(sqlCheckItem);
            pstmtCartItem.setInt(1, cartId);
            pstmtCartItem.setInt(2, variantId);
            ResultSet rsItem = pstmtCartItem.executeQuery();

            if (rsItem.next()) {
                // Gia hạn thêm 5 phút
                String sqlUpdateItem = "UPDATE Cart_Item SET quantity = quantity + ?, expires_at = DATEADD(minute, 5, GETDATE()) WHERE cart_id = ? AND variant_id = ?";
                PreparedStatement pstmtUpd = conn.prepareStatement(sqlUpdateItem);
                pstmtUpd.setInt(1, quantity);
                pstmtUpd.setInt(2, cartId);
                pstmtUpd.setInt(3, variantId);
                pstmtUpd.executeUpdate();
                pstmtUpd.close();
            } else {
                // Thêm mới với hạn 5 phút
                String sqlInsertItem = "INSERT INTO Cart_Item (cart_id, variant_id, quantity, expires_at) VALUES (?, ?, ?, DATEADD(minute, 5, GETDATE()))";
                PreparedStatement pstmtIns = conn.prepareStatement(sqlInsertItem);
                pstmtIns.setInt(1, cartId);
                pstmtIns.setInt(2, variantId);
                pstmtIns.setInt(3, quantity);
                pstmtIns.executeUpdate();
                pstmtIns.close();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (rsStock != null)
                rsStock.close();
            if (pstmtCheckStock != null)
                pstmtCheckStock.close();
            if (pstmtUpdateStock != null)
                pstmtUpdateStock.close();
            if (pstmtCartItem != null)
                pstmtCartItem.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }

    // =========================================================================
    // HÀM XÓA KHỎI GIỎ - (Tự động trả kho nếu đó là đồ POS đang giữ)
    // =========================================================================
    public boolean removeFromCart(int userId, String skuCode) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlGetQty = "SELECT ci.quantity, ci.variant_id, ci.expires_at " +
                    "FROM Cart_Item ci JOIN Cart c ON ci.cart_id = c.id " +
                    "JOIN Product_Variant pv ON ci.variant_id = pv.id " +
                    "WHERE c.user_id = ? AND pv.sku_code = ?";
            pstmt = conn.prepareStatement(sqlGetQty);
            pstmt.setInt(1, userId);
            pstmt.setString(2, skuCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int qtyToReturn = rs.getInt("quantity");
                int variantId = rs.getInt("variant_id");

                // Nếu expires_at KHÔNG NULL (tức là hàng POS đang giữ), thì trả lại kho
                if (rs.getObject("expires_at") != null) {
                    String sqlReturnStock = "UPDATE Product_Variant SET stock_quantity = stock_quantity + ? WHERE id = ?";
                    PreparedStatement pReturn = conn.prepareStatement(sqlReturnStock);
                    pReturn.setInt(1, qtyToReturn);
                    pReturn.setInt(2, variantId);
                    pReturn.executeUpdate();
                    pReturn.close();
                }

                // Xóa khỏi giỏ
                String sqlDelete = "DELETE FROM Cart_Item WHERE cart_id = (SELECT id FROM Cart WHERE user_id = ?) AND variant_id = ?";
                PreparedStatement pDel = conn.prepareStatement(sqlDelete);
                pDel.setInt(1, userId);
                pDel.setInt(2, variantId);
                pDel.executeUpdate();
                pDel.close();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (pstmt != null)
                pstmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }

    // Hàm dọn sạch giỏ hàng của Khách sau khi đặt hàng thành công
    public boolean clearCartByUserId(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM Cart_Item WHERE cart_id = (SELECT id FROM Cart WHERE user_id = ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }
}