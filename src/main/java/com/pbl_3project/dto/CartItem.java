package com.pbl_3project.dto;
import java.time.LocalDateTime;
public class CartItem {
    private String sku;
    private String name;
    private String size;
    private double price;
    private int quantity;
    private int stock;
    private String color;
    private LocalDateTime expiresAt; 
    public CartItem(String sku, String name, String size, String color, double price, int quantity, int stock) {
        this.sku = sku;
        this.name = name;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
        this.expiresAt = null; 
    }
    public CartItem(String sku, String name, String size, String color, double price, int quantity, int stock,
            LocalDateTime expiresAt) {
        this(sku, name, size, color, price, quantity, stock);
        this.expiresAt = expiresAt;
    }
    public String getSku() {
        return sku;
    }
    public String getName() {
        return name;
    }
    public String getSize() {
        return size;
    }
    public double getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }
    public int getStock() {
        return stock;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getColor() {
        return color;
    }
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    public boolean isExpired() {
        if (expiresAt == null)
            return false;
        return LocalDateTime.now().isAfter(expiresAt);
    }
    public long getSecondsUntilExpire() {
        if (expiresAt == null)
            return Long.MAX_VALUE;
        long seconds = java.time.temporal.ChronoUnit.SECONDS.between(LocalDateTime.now(), expiresAt);
        return Math.max(seconds, 0);
    }
    public double getTotalPrice() {
        return price * quantity;
    }
}
