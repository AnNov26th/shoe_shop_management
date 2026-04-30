package com.pbl_3project.enums;

public enum PromotionType {
    FLASH_SALE("Flash sale"),
    BUY_1_GET_1("Mua 1 tặng 1"),
    CATEGORY_DISCOUNT("Giảm giá danh mục");

    private final String description;

    PromotionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
