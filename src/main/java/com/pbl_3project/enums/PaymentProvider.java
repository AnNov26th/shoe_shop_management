package com.pbl_3project.enums;
public enum PaymentProvider {
    MOMO("MoMo"),
    VNPAY("VNPay"),
    STRIPE("Stripe"),
    NONE("Không");
    private final String description;
    PaymentProvider(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
