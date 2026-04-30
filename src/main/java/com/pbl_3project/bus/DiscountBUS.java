package com.pbl_3project.bus;

import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import com.pbl_3project.dao.DiscountDAO;

public class DiscountBUS {
    private DiscountDAO discountDAO = new DiscountDAO();

    public DefaultTableModel getAllPromotions() throws SQLException {
        return discountDAO.getAllPromotions();
    }

    public boolean addPromotion(String code, String type, double value, double minVal, double maxDiscount, int limit,
            String start, String end) throws SQLException {
        if (code == null || code.trim().isEmpty())
            throw new SQLException("Mã không được để trống!");
        if (value <= 0)
            throw new SQLException("Giá trị giảm phải > 0!");
        return discountDAO.addPromotion(code, type, value, minVal, maxDiscount, limit, start, end);
    }

    public boolean deletePromotion(int id) throws SQLException {
        return discountDAO.deletePromotion(id);
    }

    public Object[] validateCoupon(String code, double orderTotal) throws SQLException {
        Object[] promo = discountDAO.checkPromotion(code);
        if (promo == null) {
            throw new SQLException("Mã giảm giá không hợp lệ hoặc đã hết hạn!");
        }
        double minVal = (double) promo[3];
        if (orderTotal < minVal) {
            throw new SQLException("Đơn hàng tối thiểu " + String.format("%,.0f", minVal) + " VNĐ để dùng mã này!");
        }
        return promo;
    }

    public void useCoupon(int promoId) throws SQLException {
        discountDAO.incrementUsage(promoId);
    }
}
