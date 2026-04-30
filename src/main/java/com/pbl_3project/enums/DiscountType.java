package com.pbl_3project.enums;

public enum DiscountType {
    PERCENT("Phần trăm"),
    FIXED("Số tiền cố định");

    private final String description;

    DiscountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
