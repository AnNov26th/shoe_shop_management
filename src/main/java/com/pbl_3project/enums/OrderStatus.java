package com.pbl_3project.enums;

public enum OrderStatus {
    PENDING("Đã đặt"),
    CONFIRMED("Đã xác nhận"),
    PACKING("Đang đóng gói"),
    SHIPPING("Đang giao"),
    DELIVERED("Đã giao"),
    RECEIVED("Đã nhận"),
    PAID_COD("Thanh toán"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
