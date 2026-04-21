package com.pbl_3project.enums;

public enum StockAdjustmentType {
    DAMAGED("Hàng bị hư hỏng"),
    LOST("Mất hàng"),
    MANUAL_ADJUST("Điều chỉnh thủ công");

    private final String description;

    StockAdjustmentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
