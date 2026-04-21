package com.pbl_3project.bus;

import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;

import com.pbl_3project.dao.UserDAO;

public class EmployeeBUS {
    private UserDAO userDAO;

    public EmployeeBUS() {
        userDAO = new UserDAO();
    }

    // 1. ĐÃ SỬA: Thêm tham số keyword để tìm kiếm
    public DefaultTableModel getEmployeeTableModel(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userDAO.getAllUsers(); // Nếu ô tìm kiếm trống thì load tất cả
        }
        return userDAO.searchUsers(keyword); // Gọi hàm tìm kiếm mới tạo ở DAO
    }

    // 2. GIỮ NGUYÊN HÀM XỬ LÝ THÊM NHÂN VIÊN CỦA BRO
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