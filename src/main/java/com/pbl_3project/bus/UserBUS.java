package com.pbl_3project.bus;
import java.sql.SQLException;
import com.pbl_3project.dao.UserDAO;
public class UserBUS {
    private UserDAO userDAO;
    public UserBUS() {
        userDAO = new UserDAO(); 
    }
    public String xulyDangNhap(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return "EMPTY_FIELD";
        }
        if (!email.contains("@")) {
            return "INVALID_EMAIL";
        }
        try {
            int result = userDAO.checkLogin(email, password);
            if (result == -1) {
                return "BANNED"; 
            } else if (result > 0) {
                return "SUCCESS_" + result; 
            } else {
                return "WRONG_INFO"; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "DB_ERROR"; 
        }
    }
}