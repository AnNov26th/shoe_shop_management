package com.pbl_3project.enums;
public enum Gender {
    MALE("Nam"),
    FEMALE("Nữ"),
    UNISEX("Unisex");
    private final String description;
    Gender(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
