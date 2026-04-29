package com.pbl_3project.enums;
public enum Role {
    ADMIN("Admin", "Quản trị viên"),
    EMPLOYEE("User", "Nhân viên"),
    CUSTOMER("Customer", "Khách hàng");
    private final String code;
    private final String description;
    Role(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }
    public static Role fromCode(String code) {
        for (Role role : Role.values()) {
            if (role.code.equalsIgnoreCase(code)) {
                return role;
            }
        }
        return null;
    }
}
