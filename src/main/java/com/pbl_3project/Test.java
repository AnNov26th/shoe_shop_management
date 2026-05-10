package com.pbl_3project;

import java.sql.*;
import com.pbl_3project.util.DatabaseConnection;

public class Test {
    public static void main(String[] args) throws Exception {
        try (Connection c = DatabaseConnection.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT DISTINCT status FROM [Order]")) {
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        }
    }
}
