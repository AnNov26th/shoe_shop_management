package com.pbl_3project.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * ConfigUtils - Quản lý các cấu hình từ database
 */
public class ConfigUtils {

    // Cache các config để không phải query liên tục
    private static int RESERVE_DURATION_MINUTES = 5; // Mặc định 5 phút
    private static boolean cacheLoaded = false;

    static {
        loadConfigFromDB();
    }

    /**
     * Load các config từ database (nếu có bảng Config)
     * Nếu chưa có, dùng giá trị mặc định
     */
    public static void loadConfigFromDB() {
        if (cacheLoaded)
            return;

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();

            // Kiểm tra xem có cột reserve_minutes trong bảng System_Config không
            String sql = "SELECT TOP 1 reserve_minutes FROM System_Config";
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                RESERVE_DURATION_MINUTES = rs.getInt("reserve_minutes");
            }

            cacheLoaded = true;
        } catch (Exception e) {
            // Nếu không có config, dùng mặc định
            System.out.println("⚠️ Không thể load config từ DB, dùng giá trị mặc định: 5 phút");
            RESERVE_DURATION_MINUTES = 5;
            cacheLoaded = true;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    DatabaseConnection.closeConnection(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Lấy khoảng thời gian giữ chỗ (tính bằng phút)
     */
    public static int getReserveDurationMinutes() {
        return RESERVE_DURATION_MINUTES;
    }

    /**
     * Set khoảng thời gian giữ chỗ (dùng cho test)
     */
    public static void setReserveDurationMinutes(int minutes) {
        RESERVE_DURATION_MINUTES = minutes;
    }

    /**
     * Lấy khoảng thời gian giữ chỗ tính bằng milliseconds
     */
    public static long getReserveDurationMillis() {
        return (long) RESERVE_DURATION_MINUTES * 60 * 1000;
    }

    /**
     * Lấy SQL datetime string cho DATEADD (minutes)
     * Ví dụ: DATEADD(minute, 5, GETDATE())
     */
    public static String getSQLExpireDateTime() {
        return "DATEADD(minute, " + RESERVE_DURATION_MINUTES + ", GETDATE())";
    }
}
