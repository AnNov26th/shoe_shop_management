package com.pbl_3project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.pbl_3project.util.DatabaseConnection;

public class ReviewDAO {

    public boolean addProductReview(int userId, int productId, int orderId, int rating, String comment) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Product_Review (user_id, product_id, order_id, rating, comment, created_at) VALUES (?, ?, ?, ?, ?, GETDATE())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, orderId);
            pstmt.setInt(4, rating);
            pstmt.setString(5, comment);
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean addShippingReview(int userId, int orderId, int rating, String comment) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Shipping_Review (user_id, order_id, rating, comment, created_at) VALUES (?, ?, ?, ?, GETDATE())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, orderId);
            pstmt.setInt(3, rating);
            pstmt.setString(4, comment);
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }
}
