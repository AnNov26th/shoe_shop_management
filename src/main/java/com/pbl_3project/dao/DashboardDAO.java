package com.pbl_3project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import com.pbl_3project.util.DatabaseConnection;

public class DashboardDAO {

    // 1. Hàm lấy 3 con số tổng quan (Trả về dạng Map: Key - Value)
    public Map<String, Integer> getQuickStats() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();

            // Tổng số nhân sự & khách hàng
            rs = stmt.executeQuery("SELECT COUNT(*) FROM [User]");
            if (rs.next())
                stats.put("total_users", rs.getInt(1));

            // Tổng số mẫu giày (Base Products)
            rs = stmt.executeQuery("SELECT COUNT(*) FROM Product");
            if (rs.next())
                stats.put("total_products", rs.getInt(1));

            // Tổng số lượng giày tồn kho
            rs = stmt.executeQuery("SELECT ISNULL(SUM(stock_quantity), 0) FROM Product_Variant");
            if (rs.next())
                stats.put("total_stock", rs.getInt(1));

        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return stats;
    }

    // 2. Hàm gom nhóm thống kê số lượng giày theo từng Thương Hiệu
    public DefaultTableModel getBrandStatistics() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String[] columns = { "Thương Hiệu", "Số Mẫu Giày", "Tổng Tồn Kho" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try {
            conn = DatabaseConnection.getConnection();
            // Lệnh SQL kết hợp 3 bảng và gom nhóm theo tên Thương hiệu
            String sql = "SELECT b.name AS brand_name, COUNT(DISTINCT p.id) AS total_models, " +
                    "ISNULL(SUM(pv.stock_quantity), 0) AS total_stock " +
                    "FROM Brand b " +
                    "LEFT JOIN Product p ON b.id = p.brand_id " +
                    "LEFT JOIN Product_Variant pv ON p.id = pv.product_id " +
                    "GROUP BY b.name " +
                    "ORDER BY total_stock DESC";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("brand_name"),
                        rs.getInt("total_models") + " Mẫu",
                        rs.getInt("total_stock") + " Đôi"
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