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

        // Tên cột khớp 100% với giao diện bên POSPanel
        String[] columnNames = { "Mã SKU", "Tên SP", "Size", "Màu", "Giá Bán", "Tồn" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Khóa không cho nhân viên sửa trực tiếp trên bảng
            }
        };

        try {
            conn = DatabaseConnection.getConnection();

            // Lệnh SQL kết hợp 2 bảng, VÀ CHỈ LẤY SẢN PHẨM CÓ TỒN KHO > 0
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
                        String.format("%,.0f VNĐ", rs.getDouble("base_price")), // Định dạng số tiền có dấu phẩy
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

    // Lọc sản phẩm riêng cho màn hình POS theo từ khóa (Mã SKU hoặc Tên)
    public DefaultTableModel searchProductsForPOS(String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String[] columnNames = { "Mã SKU", "Tên SP", "Size", "Màu", "Giá Bán", "Tồn" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Vẫn khóa không cho sửa trực tiếp trên bảng
            }
        };

        try {
            conn = DatabaseConnection.getConnection();

            // Tìm các sản phẩm CÒN HÀNG và KHỚP với từ khóa (Tên hoặc SKU)
            String sql = "SELECT pv.sku_code, p.name AS product_name, pv.size, pv.color, p.base_price, pv.stock_quantity "
                    +
                    "FROM Product p " +
                    "JOIN Product_Variant pv ON p.id = pv.product_id " +
                    "WHERE pv.stock_quantity > 0 AND (p.name LIKE ? OR pv.sku_code LIKE ?) " +
                    "ORDER BY p.name, pv.size";

            pstmt = conn.prepareStatement(sql);
            String searchStr = "%" + keyword + "%"; // Thêm % 2 đầu để tìm chuỗi con
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

    // Lấy danh sách sản phẩm cho màn hình Khách hàng (Có bộ lọc giới tính)
    public DefaultTableModel getProductsForShop(String genderFilter) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String[] columnNames = { "Mã SKU", "Tên SP", "Size", "Màu", "Giá Bán", "Tồn" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        try {
            conn = DatabaseConnection.getConnection();

            // Câu SQL lấy thêm cột gender để lọc
            String sql = "SELECT pv.sku_code, p.name AS product_name, pv.size, pv.color, p.base_price, pv.stock_quantity, p.gender "
                    +
                    "FROM Product p " +
                    "JOIN Product_Variant pv ON p.id = pv.product_id " +
                    "WHERE pv.stock_quantity > 0 ";

            // Nếu có bộ lọc (không phải là "Tất cả") thì thêm điều kiện WHERE
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

        String[] columnNames = { "ID", "Tên SP", "Giá Cơ Bản", "Màu Đại Diện", "Tổng Tồn Kho" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        try {
            conn = DatabaseConnection.getConnection();
            // Dùng GROUP BY để gom nhóm. MAX(pv.color) để lấy 1 màu làm ảnh đại diện
            String sql = "SELECT p.id, p.name, p.base_price, MAX(pv.color) as sample_color, SUM(pv.stock_quantity) as total_stock "
                    +
                    "FROM Product p " +
                    "JOIN Product_Variant pv ON p.id = pv.product_id " +
                    "WHERE pv.stock_quantity > 0 ";

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
                        rs.getString("sample_color"), rs.getInt("total_stock")
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

    // 2. Lấy danh sách Chi tiết (Size/Màu) của 1 Mẫu Giày cụ thể
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
            // Đóng connection...
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

        String[] columnNames = { "ID", "Tên SP", "Giá Cơ Bản", "Màu Đại Diện", "Tổng Tồn Kho", "Giới tính" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        try {
            conn = DatabaseConnection.getConnection();
            // Dùng LEFT JOIN để lấy cả sản phẩm chưa có Variant nào
            String sql = "SELECT p.id, p.name, p.base_price, MAX(pv.color) as sample_color, " +
                    "ISNULL(SUM(pv.stock_quantity), 0) as total_stock, p.gender " +
                    "FROM Product p " +
                    "LEFT JOIN Product_Variant pv ON p.id = pv.product_id " +
                    "WHERE p.status != 'Inactive' "; // Không lấy hàng đã xóa

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
                        rs.getInt("total_stock"), rs.getString("gender")
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

    // 2. Thêm Sản phẩm mới (Mẫu giày cơ bản)
    public boolean addProduct(int brandId, int categoryId, String name, double basePrice, String gender,
            String description) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Tự động tạo Slug đơn giản từ Tên (Ví dụ: "Nike Air" -> "nike-air")
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

    // 3. Xóa mềm (Vô hiệu hóa) Sản phẩm
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
}