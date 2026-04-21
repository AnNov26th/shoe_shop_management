package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import com.pbl_3project.bus.CartBUS;
import com.pbl_3project.dao.CartDAO;
import com.pbl_3project.dao.ProductDAO;
import com.pbl_3project.dto.CartItem;

public class ProductDetailDialog extends JDialog {
    private int productId;
    private String productName;
    private double basePrice;
    private JTable tableVariants;
    private ProductDAO productDAO = new ProductDAO();
    private CartDAO cartDAO = new CartDAO();
    private int currentUserId;

    private CartBUS cartBUS;
    private Runnable onSuccess;

    // Constructor cũ (Để không làm lỗi các file khác)
    public ProductDetailDialog(JFrame parent, int productId, String productName, double basePrice, String role,
            Integer userId) {
        this(parent, productId, productName, basePrice, role, userId, null, null);
    }

    // Constructor mới đầy đủ tham số
    public ProductDetailDialog(JFrame parent, int productId, String productName, double basePrice, String role,
            Integer userId, CartBUS cartBUS, Runnable onSuccess) {
        super(parent, "Chi Tiết Sản Phẩm: " + productName, true);
        this.productId = productId;
        this.productName = productName;
        this.basePrice = basePrice;
        this.currentUserId = (userId != null) ? userId : -1;
        this.cartBUS = cartBUS;
        this.onSuccess = onSuccess;

        setSize(700, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        tableVariants = new JTable();
        tableVariants.setRowHeight(30);
        tableVariants.setFont(new Font("Arial", Font.PLAIN, 14));
        tableVariants.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        loadVariants();
        JScrollPane scrollPane = new JScrollPane(tableVariants);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách Phân loại (Chỉ hiện các đôi còn hàng)"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        actionPanel.setBackground(Color.WHITE);

        JLabel lblQty = new JLabel("Số lượng:");
        lblQty.setFont(new Font("Arial", Font.BOLD, 14));
        JSpinner spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        spinQty.setFont(new Font("Arial", Font.BOLD, 14));

        JButton btnAction = new JButton();
        btnAction.setFont(new Font("Arial", Font.BOLD, 14));
        btnAction.setPreferredSize(new Dimension(200, 40));
        btnAction.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if ("ADMIN".equals(role)) {
            lblQty.setVisible(false);
            spinQty.setVisible(false);
            btnAction.setText("Đóng Cửa Sổ");
            btnAction.setBackground(new Color(200, 200, 200));
            btnAction.addActionListener(e -> dispose());
        } else {
            actionPanel.add(lblQty);
            actionPanel.add(spinQty);

            if ("STAFF".equals(role)) {
                btnAction.setText("Giữ Chỗ POS (5 Phút)");
                btnAction.setBackground(new Color(46, 204, 113));
                btnAction.setForeground(Color.BLACK);
                btnAction.addActionListener(e -> handleStaffAction(spinQty));
            } else if ("CUSTOMER".equals(role)) {
                btnAction.setText("Thêm Vào Giỏ Online");
                btnAction.setBackground(new Color(59, 190, 210));
                btnAction.setForeground(Color.BLACK);
                btnAction.addActionListener(e -> handleCustomerAction(spinQty));
            }
        }

        actionPanel.add(btnAction);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void handleCustomerAction(JSpinner spinQty) {
        String sku = getSelectedSku();
        if (sku == null)
            return;

        if (currentUserId == -1) {
            JOptionPane.showMessageDialog(this, "Không nhận diện được Khách hàng! Vui lòng đăng nhập lại.", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int qty = (int) spinQty.getValue();
            // 1. LƯU XUỐNG DATABASE TRƯỚC
            if (cartDAO.addToCartOnline(currentUserId, sku, qty)) {

                // 2. NẾU THÀNH CÔNG, CẬP NHẬT NGAY LÊN GIAO DIỆN
                if (cartBUS != null) {
                    int row = tableVariants.getSelectedRow();
                    String size = tableVariants.getValueAt(row, 1).toString();
                    String color = tableVariants.getValueAt(row, 2).toString();
                    int stock = Integer.parseInt(tableVariants.getValueAt(row, 3).toString());

                    CartItem newItem = new CartItem(sku, productName, size, color, basePrice, qty, stock);
                    cartBUS.addItem(newItem);
                }

                JOptionPane.showMessageDialog(this, "Đã thêm vào giỏ hàng thành công!");
                if (onSuccess != null)
                    onSuccess.run();
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleStaffAction(JSpinner spinQty) {
        String sku = getSelectedSku();
        if (sku == null)
            return;
        try {
            if (cartDAO.holdItemForPOS(currentUserId, sku, (int) spinQty.getValue())) {
                JOptionPane.showMessageDialog(this, "Đã thêm vào giỏ POS & Khóa kho 5 phút!");
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadVariants() {
        try {
            tableVariants.setModel(productDAO.getVariantsByProductId(productId));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải phân loại: " + e.getMessage());
        }
    }

    private String getSelectedSku() {
        int row = tableVariants.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Size/Màu từ bảng!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return tableVariants.getValueAt(row, 0).toString();
    }
}