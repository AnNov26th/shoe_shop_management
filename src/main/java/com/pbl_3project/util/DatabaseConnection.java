package com.pbl_3project.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    private static final String SERVER_NAME = "localhost";
    private static final String INSTANCE_NAME = "SQLEXPRESS";
    private static final String DATABASE_NAME = "ShopGiayDB";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123456";
    private static final String DB_URL = "jdbc:sqlserver://" + SERVER_NAME
            + ";instanceName=" + INSTANCE_NAME
            + ";databaseName=" + DATABASE_NAME
            + ";encrypt=true;trustServerCertificate=true;";
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
