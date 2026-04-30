package com.pbl_3project.bus;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import com.pbl_3project.dao.UserDAO;
public class EmployeeBUS {
    private UserDAO userDAO;
    public EmployeeBUS() {
        userDAO = new UserDAO();
    }
    public DefaultTableModel getEmployeeTableModel(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userDAO.getAllUsers(); 
        }
        return userDAO.searchUsers(keyword); 
    }
    public String xuLyThemNhanVien(String name, String email, String password, String phone, int roleId) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            return "EMPTY";
        }
        if (!email.contains("@")) {
            return "INVALID_EMAIL";
        }
        if (phone.length() < 10 || !phone.matches("\\d+")) {
            return "INVALID_PHONE";
        }
        try {
            boolean isSuccess = userDAO.addUser(name, email, password, phone, roleId);
            return isSuccess ? "SUCCESS" : "FAIL";
        } catch (SQLException e) {
            e.printStackTrace();
            return "DB_ERROR";
        }
    }
    public boolean xoaNhanVien(int id) throws SQLException {
        return userDAO.deleteUser(id);
    }
}
