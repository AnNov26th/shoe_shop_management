package com.pbl_3project.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.pbl_3project.util.DatabaseConnection;

public class ReviewDAO {
    public void setupTables() throws SQLException {
        Connection conn = null;
        java.sql.Statement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            

            String sqlProductReview = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Product_Review' AND xtype='U') " +
                                      "BEGIN " +
                                      "CREATE TABLE Product_Review (" +
                                      "id INT IDENTITY(1,1) PRIMARY KEY, " +
                                      "user_id INT, " +
                                      "product_id INT, " +
                                      "order_id INT, " +
                                      "rating INT, " +
                                      "variant_info NVARCHAR(200), " +
                                      "comment NVARCHAR(MAX), " +
                                      "image_url VARCHAR(255), " +
                                      "created_at DATETIME DEFAULT GETDATE(), " +
                                      "FOREIGN KEY (user_id) REFERENCES [User](id), " +
                                      "FOREIGN KEY (product_id) REFERENCES Product(id)" +
                                      ") " +
                                      "END";
            stmt.execute(sqlProductReview);

            String sqlAddVariantInfo = "IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Product_Review') AND name = 'variant_info') " +
                                       "ALTER TABLE Product_Review ADD variant_info NVARCHAR(200)";
            stmt.execute(sqlAddVariantInfo);


            String sqlShippingReview = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Shipping_Review' AND xtype='U') " +
                                       "BEGIN " +
                                       "CREATE TABLE Shipping_Review (" +
                                       "id INT IDENTITY(1,1) PRIMARY KEY, " +
                                       "user_id INT, " +
                                       "order_id INT, " +
                                       "rating INT, " +
                                       "comment NVARCHAR(MAX), " +
                                       "created_at DATETIME DEFAULT GETDATE(), " +
                                       "FOREIGN KEY (user_id) REFERENCES [User](id)" +
                                       ") " +
                                       "END";
            stmt.execute(sqlShippingReview);

        } finally {
            if (stmt != null) stmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean addProductReview(int userId, int productId, int orderId, int rating, String variantInfo, String comment, String imageUrl) throws SQLException {
        setupTables();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Product_Review (user_id, product_id, order_id, rating, variant_info, comment, image_url, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, orderId);
            pstmt.setInt(4, rating);
            pstmt.setNString(5, variantInfo);
            pstmt.setNString(6, comment);
            pstmt.setString(7, imageUrl);
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean addShippingReview(int userId, int orderId, int rating, String comment) throws SQLException {
        setupTables();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Shipping_Review (user_id, order_id, rating, comment, created_at) VALUES (?, ?, ?, ?, GETDATE())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, orderId);
            pstmt.setInt(3, rating);
            pstmt.setNString(4, comment);
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }

    public javax.swing.table.DefaultTableModel getReviewsByProductId(int productId) throws SQLException {
        setupTables();
        Connection conn = null;
        PreparedStatement pstmt = null;
        java.sql.ResultSet rs = null;
        String[] columnNames = { "User Name", "Rating", "Variant", "Comment", "Image URL", "Date" };
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columnNames, 0);
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT u.full_name, r.rating, r.variant_info, r.comment, r.image_url, r.created_at " +
                         "FROM Product_Review r " +
                         "JOIN [User] u ON r.user_id = u.id " +
                         "WHERE r.product_id = ? " +
                         "ORDER BY r.created_at DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, productId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("full_name"),
                        rs.getInt("rating"),
                        rs.getString("variant_info"),
                        rs.getString("comment"),
                        rs.getString("image_url"),
                        rs.getDate("created_at")
                });
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return model;
    }

    public boolean isOrderReviewed(int orderId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        java.sql.ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM Product_Review WHERE order_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }
}
