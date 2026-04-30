package com.pbl_3project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import com.pbl_3project.util.DatabaseConnection;

public class DiscountDAO {
    public DefaultTableModel getAllPromotions() throws SQLException {
        String[] cols = { "ID", "Mã", "Loại", "Giá trị", "Đơn tối thiểu", "Giảm tối đa", "Giới hạn", "Đã dùng",
                "Bắt đầu", "Kết thúc" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        String sql = "SELECT * FROM [Promotion] ORDER BY end_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("type"),
                        rs.getDouble("discount_value"),
                        rs.getDouble("min_order_value"),
                        rs.getDouble("max_discount_amount"),
                        rs.getInt("usage_limit"),
                        rs.getInt("current_usage"),
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date")
                });
            }
        }
        return model;
    }

    public boolean addPromotion(String code, String type, double value, double minVal, double maxDiscount, int limit,
            String start, String end) throws SQLException {
        String sql = "INSERT INTO [Promotion] (code, type, discount_value, min_order_value, max_discount_amount, usage_limit, current_usage, start_date, end_date) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, 0, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, type);
            pstmt.setDouble(3, value);
            pstmt.setDouble(4, minVal);
            pstmt.setDouble(5, maxDiscount);
            pstmt.setInt(6, limit);
            pstmt.setString(7, start);
            pstmt.setString(8, end);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deletePromotion(int id) throws SQLException {
        String sql = "DELETE FROM [Promotion] WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public Object[] checkPromotion(String code) throws SQLException {
        String sql = "SELECT * FROM [Promotion] WHERE code = ? AND GETDATE() BETWEEN start_date AND end_date AND (usage_limit IS NULL OR current_usage < usage_limit)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[] {
                            rs.getInt("id"),
                            rs.getString("type"),
                            rs.getDouble("discount_value"),
                            rs.getDouble("min_order_value"),
                            rs.getDouble("max_discount_amount")
                    };
                }
            }
        }
        return null;
    }

    public void incrementUsage(int promoId) throws SQLException {
        String sql = "UPDATE [Promotion] SET current_usage = ISNULL(current_usage, 0) + 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, promoId);
            pstmt.executeUpdate();
        }
    }
}
