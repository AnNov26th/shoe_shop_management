package com.pbl_3project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import com.pbl_3project.util.DatabaseConnection;

public class ProductDAO {
    public DefaultTableModel getAllProductVariants() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] columnNames = { "Mã SKU", "Tên Sản Phẩm", "Thương Hiệu", "Size", "Màu Sắc", "Tồn Kho", "Giá Bán" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT pv.sku_code, p.name AS product_name, b.name AS brand_name, " +
                    "pv.size, pv.color, pv.stock_quantity, p.base_price " +
                    "FROM Product p " +
                    "JOIN Brand b ON p.brand_id = b.id " +
                    "JOIN Product_Variant pv ON p.id = pv.product_id " +
                    "ORDER BY p.name, pv.size";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        rs.getString("sku_code"),
                        rs.getString("product_name"),
                        rs.getString("brand_name"),
                        rs.getString("size"),
                        rs.getString("color"),
                        rs.getInt("stock_quantity"),
                        String.format("%,.0f VNĐ", rs.getDouble("base_price"))
                };
                tableModel.addRow(row);
            }
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return tableModel;
    }

    public DefaultTableModel getProductsForPOS() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] columnNames = { "Mã SKU", "Tên SP", "Size", "Màu", "Giá Bán", "Tồn" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT pv.sku_code, p.name AS product_name, pv.size, pv.color, p.base_price, pv.stock_quantity "
                    +
                    "FROM Product p " +
                    "JOIN Product_Variant pv ON p.id = pv.product_id " +
                    "WHERE pv.stock_quantity > 0 " +
                    "ORDER BY p.name, pv.size";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        rs.getString("sku_code"),
                        rs.getString("product_name"),
                        rs.getString("size"),
                        rs.getString("color"),
                        String.format("%,.0f VNĐ", rs.getDouble("base_price")),
                        rs.getInt("stock_quantity")
                };
                tableModel.addRow(row);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return tableModel;
    }

    public DefaultTableModel searchProductsForPOS(String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] columnNames = { "Mã SKU", "Tên SP", "Size", "Màu", "Giá Bán", "Tồn" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT pv.sku_code, p.name AS product_name, pv.size, pv.color, p.base_price, pv.stock_quantity "
                    +
                    "FROM Product p " +
                    "JOIN Product_Variant pv ON p.id = pv.product_id " +
                    "WHERE pv.stock_quantity > 0 AND (p.name LIKE ? OR pv.sku_code LIKE ?) " +
                    "ORDER BY p.name, pv.size";
            pstmt = conn.prepareStatement(sql);
            String searchStr = "%" + keyword + "%";
            pstmt.setString(1, searchStr);
            pstmt.setString(2, searchStr);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        rs.getString("sku_code"),
                        rs.getString("product_name"),
                        rs.getString("size"),
                        rs.getString("color"),
                        String.format("%,.0f VNĐ", rs.getDouble("base_price")),
                        rs.getInt("stock_quantity")
                };
                tableModel.addRow(row);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return tableModel;
    }

    public DefaultTableModel getProductsForShop(String genderFilter) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] columnNames = { "Mã SKU", "Tên SP", "Size", "Màu", "Giá Bán", "Tồn" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT pv.sku_code, p.name AS product_name, pv.size, pv.color, p.base_price, pv.stock_quantity, p.gender "
                    +
                    "FROM Product p " +
                    "JOIN Product_Variant pv ON p.id = pv.product_id " +
                    "WHERE pv.stock_quantity > 0 ";
            if (genderFilter != null && !genderFilter.equals("Tất cả")) {
                sql += " AND p.gender = ? ";
            }
            sql += " ORDER BY p.name, pv.size";
            pstmt = conn.prepareStatement(sql);
            if (genderFilter != null && !genderFilter.equals("Tất cả")) {
                pstmt.setString(1, genderFilter);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("sku_code"),
                        rs.getString("product_name"),
                        rs.getString("size"),
                        rs.getString("color"),
                        String.format("%,.0f VNĐ", rs.getDouble("base_price")),
                        rs.getInt("stock_quantity")
                });
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return tableModel;
    }

    public DefaultTableModel getBaseProducts(String genderFilter) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] columnNames = { "ID", "Tên SP", "Giá Cơ Bản", "Màu Đại Diện", "Tổng Tồn Kho", "Hình Ảnh" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT p.id, p.name, p.base_price, MAX(pv.color) as sample_color, SUM(pv.stock_quantity) as total_stock, MAX(pi.image_url) as image_url "
                    + "FROM Product p "
                    + "JOIN Product_Variant pv ON p.id = pv.product_id "
                    + "LEFT JOIN Product_Image pi ON p.id = pi.product_id AND pi.is_primary = 1 "
                    + "WHERE pv.stock_quantity > 0 ";
            if (genderFilter != null && !genderFilter.equals("Tất cả")) {
                sql += " AND p.gender = ? ";
            }
            sql += " GROUP BY p.id, p.name, p.base_price, p.gender ORDER BY p.name";
            pstmt = conn.prepareStatement(sql);
            if (genderFilter != null && !genderFilter.equals("Tất cả")) {
                pstmt.setString(1, genderFilter);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("id"), rs.getString("name"), rs.getDouble("base_price"),
                        rs.getString("sample_color"), rs.getInt("total_stock"), rs.getString("image_url")
                });
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return tableModel;
    }

    public DefaultTableModel getVariantsByProductId(int productId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] columnNames = { "Mã SKU", "Size", "Màu sắc", "Tồn kho" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT sku_code, size, color, stock_quantity " +
                    "FROM Product_Variant WHERE product_id = ? AND stock_quantity > 0 ORDER BY size";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, productId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("sku_code"), rs.getString("size"),
                        rs.getString("color"), rs.getInt("stock_quantity")
                });
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return tableModel;
    }

    public DefaultTableModel searchProductsAdmin(String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] columnNames = { "ID", "Tên SP", "Giá Cơ Bản", "Màu Đại Diện", "Tổng Tồn Kho", "Giới tính",
                "Hình Ảnh" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT p.id, p.name, p.base_price, MAX(pv.color) as sample_color, " +
                    "ISNULL(SUM(pv.stock_quantity), 0) as total_stock, p.gender, MAX(pi.image_url) as image_url " +
                    "FROM Product p " +
                    "LEFT JOIN Product_Variant pv ON p.id = pv.product_id " +
                    "LEFT JOIN Product_Image pi ON p.id = pi.product_id AND pi.is_primary = 1 " +
                    "WHERE p.status != 'Inactive' ";
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql += " AND p.name LIKE ? ";
            }
            sql += " GROUP BY p.id, p.name, p.base_price, p.gender ORDER BY p.id DESC";
            pstmt = conn.prepareStatement(sql);
            if (keyword != null && !keyword.trim().isEmpty()) {
                pstmt.setString(1, "%" + keyword.trim() + "%");
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("id"), rs.getString("name"), rs.getDouble("base_price"),
                        rs.getString("sample_color") != null ? rs.getString("sample_color") : "",
                        rs.getInt("total_stock"), rs.getString("gender"),
                        rs.getString("image_url")
                });
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return tableModel;
    }

    public boolean addProduct(int brandId, int categoryId, String name, double basePrice, String gender,
            String description) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            String slug = name.toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
            String sql = "INSERT INTO Product (brand_id, category_id, name, slug, base_price, gender, description, status) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 'Selling')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, brandId);
            pstmt.setInt(2, categoryId);
            pstmt.setString(3, name);
            pstmt.setString(4, slug);
            pstmt.setDouble(5, basePrice);
            pstmt.setString(6, gender);
            pstmt.setString(7, description);
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean deleteProduct(int productId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE Product SET status = 'Inactive' WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }

    public DefaultTableModel lookupInventory(String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] columnNames = { "ID", "Tên SP", "Hãng", "Giá Bán", "Màu Đại Diện", "Tổng Tồn Kho" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT p.id, p.name, b.name as brand_name, p.base_price, MAX(pv.color) as sample_color, " +
                    "SUM(pv.stock_quantity) as total_stock " +
                    "FROM Product p " +
                    "JOIN Brand b ON p.brand_id = b.id " +
                    "JOIN Product_Variant pv ON p.id = pv.product_id " +
                    "WHERE p.status = 'Selling' ";
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql += " AND (p.name LIKE ? OR b.name LIKE ?) ";
            }
            sql += " GROUP BY p.id, p.name, b.name, p.base_price ORDER BY total_stock DESC";
            pstmt = conn.prepareStatement(sql);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String p = "%" + keyword.trim() + "%";
                pstmt.setString(1, p);
                pstmt.setString(2, p);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"), rs.getString("name"), rs.getString("brand_name"),
                        rs.getDouble("base_price"), rs.getString("sample_color"), rs.getInt("total_stock")
                });
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return model;
    }

    public void setupReviewTable() throws SQLException {
        Connection conn = null;
        java.sql.Statement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Product_Review' AND xtype='U') " +
                    "BEGIN " +
                    "CREATE TABLE Product_Review (" +
                    "id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "product_id INT, " +
                    "user_id INT, " +
                    "rating INT, " +
                    "comment NVARCHAR(MAX), " +
                    "image_url VARCHAR(255), " +
                    "created_at DATETIME DEFAULT GETDATE(), " +
                    "FOREIGN KEY (product_id) REFERENCES Product(id), " +
                    "FOREIGN KEY (user_id) REFERENCES [User](id)" +
                    ") " +
                    "END";
            stmt.execute(sql);
        } finally {
            if (stmt != null)
                stmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean addReview(int productId, int userId, int rating, String comment, String imageUrl)
            throws SQLException {
        setupReviewTable();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Product_Review (product_id, user_id, rating, comment, image_url) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, productId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, rating);
            pstmt.setNString(4, comment);
            pstmt.setString(5, imageUrl);
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }

    public DefaultTableModel getReviewsByProductId(int productId) throws SQLException {
        setupReviewTable();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] columnNames = { "User Name", "Rating", "Comment", "Image URL", "Date" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT u.full_name, r.rating, r.comment, r.image_url, r.created_at " +
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
                        rs.getString("comment"),
                        rs.getString("image_url"),
                        rs.getDate("created_at")
                });
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return model;
    }
}
