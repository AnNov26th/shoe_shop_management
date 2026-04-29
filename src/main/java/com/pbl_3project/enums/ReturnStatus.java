package com.pbl_3project.enums;
public enum ReturnStatus {
    PENDING("Chờ xử lý"),
    APPROVED("Đã phê duyệt"),
    REJECTED("Đã từ chối");
    private final String description;
    ReturnStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
