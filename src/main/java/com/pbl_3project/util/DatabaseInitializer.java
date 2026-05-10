package com.pbl_3project.util;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Customer_Voucher' and xtype='U') " +
                         "CREATE TABLE Customer_Voucher (" +
                         "id INT IDENTITY PRIMARY KEY, " +
                         "customer_id INT, " +
                         "promo_code VARCHAR(50), " +
                         "is_used BIT DEFAULT 0)";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
