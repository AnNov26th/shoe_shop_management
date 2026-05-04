package com.pbl_3project.bus;

import java.sql.SQLException;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import com.pbl_3project.dao.OrderDAO;
import com.pbl_3project.dto.CartItem;

public class OrderBUS {
    private OrderDAO orderDAO = new OrderDAO();

    public boolean checkout(Integer customerId, String customerPhone, Integer staffId, String paymentMethod, double totalAmount, List<CartItem> cartItems, String status)
            throws SQLException {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new SQLException("Giỏ hàng đang trống, không thể thanh toán!");
        }
        return orderDAO.createOrder(customerId, customerPhone, staffId, paymentMethod, totalAmount, cartItems, status) != null;
    }

    public DefaultTableModel getOrdersByCustomer(int customerId) throws SQLException {
        return orderDAO.getAllOrdersByCustomer(customerId);
    }

    public DefaultTableModel getAllOrders(String keyword) throws SQLException {
        return orderDAO.getAllOrders(keyword);
    }

    public DefaultTableModel getAllOrders(String keyword, String fromDate, String toDate) throws SQLException {
        return orderDAO.getAllOrders(keyword, fromDate, toDate);
    }

    public DefaultTableModel getOrderDetails(int orderId) throws SQLException {
        return orderDAO.getOrderDetails(orderId);
    }

    public boolean updateOrderStatus(int orderId, String newStatus) throws SQLException {
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new SQLException("Trạng thái không hợp lệ!");
        }
        return orderDAO.updateOrderStatus(orderId, newStatus);
    }

    public boolean cancelOrder(int orderId) throws SQLException {
        return orderDAO.cancelOrder(orderId);
    }

    public boolean payOrder(int orderId, String method, double amount) throws SQLException {
        if (method == null || method.trim().isEmpty()) {
            throw new SQLException("Vui lòng chọn phương thức thanh toán!");
        }
        return orderDAO.processPayment(orderId, method, amount);
    }

    public boolean confirmReceipt(int orderId) throws SQLException {
        return orderDAO.confirmReceipt(orderId);
    }

    public boolean requestReturn(int orderId, String reason, String type, String details) throws SQLException {
        return orderDAO.requestReturn(orderId, reason, type, details);
    }

    public DefaultTableModel getReturnRequests() throws SQLException {
        return orderDAO.getReturnRequests();
    }

    public boolean handleReturnRequest(int orderId, boolean accept) throws SQLException {
        return orderDAO.handleReturnRequest(orderId, accept);
    }

    public boolean updateExchangeInfo(int orderId, String info) throws SQLException {
        return orderDAO.updateExchangeInfo(orderId, info);
    }

    public boolean isEligibleForReturn(int orderId) throws SQLException {
        return orderDAO.isEligibleForReturn(orderId);
    }
}
