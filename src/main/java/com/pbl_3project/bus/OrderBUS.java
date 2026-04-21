package com.pbl_3project.bus;

import java.sql.SQLException;
import java.util.List;

import com.pbl_3project.dao.OrderDAO;
import com.pbl_3project.dto.CartItem;

public class OrderBUS {
    private OrderDAO orderDAO = new OrderDAO();

    public boolean checkout(String customerInfo, double totalAmount, List<CartItem> cartItems) throws SQLException {
        // Kiểm tra logic cơ bản trước khi đụng vào DB
        if (cartItems == null || cartItems.isEmpty()) {
            throw new SQLException("Giỏ hàng đang trống, không thể thanh toán!");
        }
        if (customerInfo == null || customerInfo.trim().isEmpty()) {
            throw new SQLException("Vui lòng nhập thông tin khách hàng (Số điện thoại)!");
        }

        // Gọi DAO để thực hiện thanh toán
        return orderDAO.createOrder(customerInfo, totalAmount, cartItems);
    }
}