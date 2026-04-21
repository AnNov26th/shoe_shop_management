package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.pbl_3project.bus.CartBUS;
import com.pbl_3project.dto.CartItem;
import com.pbl_3project.util.IconUtils;
import com.pbl_3project.util.TimeDisplayPanel;

public class CustomerForm extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private CartBUS cartBUS;
    private int customerId; // LƯU ID CỦA KHÁCH HÀNG THẬT

    // ĐÃ SỬA: Nhận ID thật từ lúc đăng nhập
    public CustomerForm(int customerId) {
        this.customerId = customerId;
        this.cartBUS = new CartBUS();

        setTitle("Cửa hàng Giày dép T&T - Trải nghiệm mua sắm");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        JPanel topHeaderWrapper = new JPanel();
        topHeaderWrapper.setLayout(new BoxLayout(topHeaderWrapper, BoxLayout.Y_AXIS));
        topHeaderWrapper.add(new TimeDisplayPanel());

        JPanel topNavbar = new JPanel(new BorderLayout());
        topNavbar.setBackground(Color.WHITE);
        topNavbar.setPreferredSize(new Dimension(getWidth(), 70));
        topNavbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(10, 20, 10, 20)));

        JButton btnLogo = new JButton("  T&T SHOES");
        btnLogo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnLogo.setForeground(new Color(56, 189, 248));
        try {
            btnLogo.setIcon(IconUtils.loadLargeIcon(IconUtils.IconType.SHOE));
        } catch (Exception ignored) {
        }
        btnLogo.setContentAreaFilled(false);
        btnLogo.setBorderPainted(false);
        btnLogo.setFocusPainted(false);
        btnLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topNavbar.add(btnLogo, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        JTextField txtSearch = new JTextField(25);
        txtSearch.setPreferredSize(new Dimension(300, 40));
        searchPanel.add(txtSearch);

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(51, 65, 85));
        btnSearch.setForeground(Color.WHITE);
        searchPanel.add(btnSearch);
        topNavbar.add(searchPanel, BorderLayout.CENTER);

        JPanel rightMenu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightMenu.setBackground(Color.WHITE);

        JButton btnCart = new JButton("Giỏ hàng (0)");
        btnCart.setForeground(new Color(10, 18, 40));
        btnCart.setBackground(new Color(56, 189, 248));
        btnCart.setOpaque(true);
        JButton btnOrders = new JButton("Đơn hàng");
        JButton btnProfile = new JButton("Tài khoản");
        JButton btnLogout = new JButton("Đăng xuất");

        rightMenu.add(btnCart);
        rightMenu.add(btnOrders);
        rightMenu.add(btnProfile);
        rightMenu.add(btnLogout);
        topNavbar.add(rightMenu, BorderLayout.EAST);
        topHeaderWrapper.add(topNavbar);
        add(topHeaderWrapper, BorderLayout.NORTH);

        Runnable updateCartBadge = () -> {
            int totalItems = 0;
            for (CartItem item : cartBUS.getCartItems())
                totalItems += item.getQuantity();
            btnCart.setText("Giỏ hàng (" + totalItems + ")");
        };

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(248, 250, 252));

        // CHUYỀN ID CHO CÁC FORM CON
        CustomerShopPanel shopPanel = new CustomerShopPanel(this.customerId, cartBUS, updateCartBadge);
        CustomerCartPanel cartPanel = new CustomerCartPanel(customerId, cartBUS, updateCartBadge); // Dùng CartPanel cũ
                                                                                                   // có sãn
        // autoId cũng k sao, nhưng thêm
        // giỏ phải chuẩn

        mainContentPanel.add(shopPanel, "Shop");
        mainContentPanel.add(new JPanel(), "Profile");
        mainContentPanel.add(new JPanel(), "Orders");
        mainContentPanel.add(cartPanel, "Cart");

        add(mainContentPanel, BorderLayout.CENTER);
        updateCartBadge.run();

        btnLogo.addActionListener(e -> cardLayout.show(mainContentPanel, "Shop"));
        btnCart.addActionListener(e -> {
            cartPanel.refreshCartGUI();
            cardLayout.show(mainContentPanel, "Cart");
        });
        btnLogout.addActionListener(e -> {
            this.dispose();
            /* new LoginForm().setVisible(true); */ });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        // TỰ ĐỘNG MÓC 1 KHÁCH HÀNG ĐỂ TRÁNH CRASH DATABASE KHI TEST
        int validCustomerId = -1;
        try (java.sql.Connection conn = com.pbl_3project.util.DatabaseConnection.getConnection();
                java.sql.Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT TOP 1 id FROM [User] WHERE role_id = 4")) {
            if (rs.next())
                validCustomerId = rs.getInt("id");
        } catch (Exception e) {
        }

        if (validCustomerId == -1) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy dữ liệu Khách hàng! Vui lòng tạo 1 tài khoản trước.");
            return;
        }

        final int finalId = validCustomerId;
        SwingUtilities.invokeLater(() -> new CustomerForm(finalId).setVisible(true));
    }
}