package com.pbl_3project.bus;
import java.util.ArrayList;
import java.util.List;
import com.pbl_3project.dto.CartItem;
public class CartBUS {
    private List<CartItem> cartItems;
    public CartBUS() {
        this.cartItems = new ArrayList<>();
    }
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    public void addItem(CartItem newItem) throws Exception {
        for (CartItem item : cartItems) {
            if (item.getSku().equals(newItem.getSku())) {
                int newQty = item.getQuantity() + newItem.getQuantity();
                if (newQty > item.getStock()) {
                    throw new Exception("Không đủ số lượng tồn kho! (Tối đa: " + item.getStock() + ")");
                }
                item.setQuantity(newQty);
                return; 
            }
        }
        if (newItem.getQuantity() > newItem.getStock()) {
            throw new Exception("Vượt quá số lượng tồn kho trong hệ thống!");
        }
        cartItems.add(newItem);
    }
    public void updateQuantity(int index, int newQty) throws Exception {
        CartItem item = cartItems.get(index);
        if (newQty > item.getStock()) {
            throw new Exception("Vượt quá số lượng tồn kho! (Tối đa: " + item.getStock() + ")");
        }
        item.setQuantity(newQty);
    }
    public void removeItem(int index) {
        if (index >= 0 && index < cartItems.size()) {
            cartItems.remove(index);
        }
    }
    public void clearCart() {
        cartItems.clear();
    }
    public double calculateTotalAmount() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }
}
