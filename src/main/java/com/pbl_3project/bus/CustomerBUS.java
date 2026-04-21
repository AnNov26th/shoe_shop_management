package com.pbl_3project.bus;

import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;

import com.pbl_3project.dao.UserDAO;

public class CustomerBUS {
    private UserDAO userDAO;

    public CustomerBUS() {
        userDAO = new UserDAO();
    }

    public DefaultTableModel getCustomerTableModel(String keyword) throws SQLException {
        return userDAO.searchCustomers(keyword);
    }

    // Dùng chung hàm xóa mềm (Khóa tài khoản khách hàng nếu có gian lận)
    public boolean xoaKhachHang(int id) throws SQLException {
        return userDAO.deleteUser(id);
    }
}