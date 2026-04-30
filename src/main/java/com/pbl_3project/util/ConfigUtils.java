package com.pbl_3project.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConfigUtils {
    private static int RESERVE_DURATION_MINUTES = 5;
    private static boolean cacheLoaded = false;
    static {
        loadConfigFromDB();
    }

    public static void loadConfigFromDB() {
        if (cacheLoaded)
            return;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT TOP 1 reserve_minutes FROM System_Config";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                RESERVE_DURATION_MINUTES = rs.getInt("reserve_minutes");
            }
            cacheLoaded = true;
        } catch (Exception e) {
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

    public static int getReserveDurationMinutes() {
        return RESERVE_DURATION_MINUTES;
    }

    public static void setReserveDurationMinutes(int minutes) {
        RESERVE_DURATION_MINUTES = minutes;
    }

    public static long getReserveDurationMillis() {
        return (long) RESERVE_DURATION_MINUTES * 60 * 1000;
    }

    public static String getSQLExpireDateTime() {
        return "DATEADD(minute, " + RESERVE_DURATION_MINUTES + ", GETDATE())";
    }
}
