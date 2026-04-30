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
    public Map<String, Object> getQuickStats() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(*) FROM [User]");
            if (rs.next())
                stats.put("total_users", rs.getInt(1));
            rs = stmt.executeQuery("SELECT COUNT(*) FROM Product");
            if (rs.next())
                stats.put("total_products", rs.getInt(1));
            rs = stmt.executeQuery("SELECT ISNULL(SUM(stock_quantity), 0) FROM Product_Variant");
            if (rs.next())
                stats.put("total_stock", rs.getInt(1));
            rs = stmt.executeQuery("SELECT ISNULL(SUM(final_amount), 0) FROM [Order] WHERE status != N'Đã hủy'");
            if (rs.next())
                stats.put("total_revenue", rs.getDouble(1));
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return stats;
    }
    public DefaultTableModel getBrandStatistics() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] columns = { "Thương Hiệu", "Số Mẫu Giày", "Tổng Tồn Kho" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        try {
            conn = DatabaseConnection.getConnection();
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
    public DefaultTableModel getMonthlyRevenueStatistics() throws SQLException {
        String[] cols = { "Tháng/Năm", "Số Đơn Hàng", "Doanh Thu (VNĐ)" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        String sql = "SELECT FORMAT(created_at, 'MM/yyyy') as month_year, COUNT(id) as order_count, SUM(final_amount) as revenue " +
                     "FROM [Order] WHERE status != N'Đã hủy' " +
                     "GROUP BY FORMAT(created_at, 'MM/yyyy') " +
                     "ORDER BY MIN(created_at) DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("month_year"),
                    rs.getInt("order_count") + " Đơn",
                    String.format("%,.0f", rs.getDouble("revenue"))
                });
            }
        }
        return model;
    }
    public DefaultTableModel getEmployeeRevenueStatistics() throws SQLException {
        String[] cols = { "Nhân Viên / Nguồn", "Số Đơn Phục Vụ", "Doanh Thu Đóng Góp (VNĐ)" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        String sql = "SELECT CASE WHEN o.employee_id IS NULL THEN N'Đơn Khách Online' ELSE u.full_name END as seller, " +
                     "COUNT(o.id) as order_count, SUM(o.final_amount) as revenue " +
                     "FROM [Order] o " +
                     "LEFT JOIN [User] u ON o.employee_id = u.id " +
                     "WHERE o.status != N'Đã hủy' " +
                     "GROUP BY o.employee_id, u.full_name " +
                     "ORDER BY revenue DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("seller"),
                    rs.getInt("order_count") + " Đơn",
                    String.format("%,.0f", rs.getDouble("revenue"))
                });
            }
        }
        return model;
    }
}
