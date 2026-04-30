package com.pbl_3project.enums;

public enum OrderStatus {
    PENDING("Đã đặt"),
    CONFIRMED("Đã xác nhận"),
    PACKING("Đang đóng gói"),
    SHIPPING("Đang giao"),
    COMPLETED("Thành công"),
    CANCELLED("Đã hủy");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
