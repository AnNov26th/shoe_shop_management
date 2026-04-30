package com.pbl_3project.gui;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import com.pbl_3project.bus.CartBUS;
import com.pbl_3project.dto.CartItem;
import com.pbl_3project.util.IconUtils;
import com.pbl_3project.util.TimeDisplayPanel;
public class CustomerForm extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private CartBUS cartBUS;
    private int customerId; 
    private static final Color BG_CONTENT = new Color(248, 250, 252);
    private static final Color NAV_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color NAV_TEXT = new Color(15, 23, 42);
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
        topNavbar.setBackground(NAV_BG);
        topNavbar.setPreferredSize(new Dimension(getWidth(), 70));
        topNavbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(10, 20, 10, 20)));
        JButton btnLogo = new JButton("  T&T SHOES") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
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
        searchPanel.setBackground(NAV_BG);
        JTextField txtSearch = new JTextField(25);
        txtSearch.setPreferredSize(new Dimension(300, 40));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(0, 15, 0, 15)));
        JButton btnSearch = new JButton("Tìm kiếm") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(15, 23, 42));
                } else {
                    g2.setColor(new Color(51, 65, 85));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setContentAreaFilled(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setPreferredSize(new Dimension(100, 40));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        topNavbar.add(searchPanel, BorderLayout.CENTER);
        JPanel rightMenu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightMenu.setBackground(NAV_BG);
        JButton btnCart = createNavButton("Giỏ hàng (0)", true);
        JButton btnOrders = createNavButton("Đơn hàng", false);
        JButton btnProfile = createNavButton("Tài khoản", false);
        JButton btnLogout = createNavButton("Đăng xuất", false);
        rightMenu.add(btnCart);
        rightMenu.add(makeDivider());
        rightMenu.add(btnOrders);
        rightMenu.add(makeDivider());
        rightMenu.add(btnProfile);
        rightMenu.add(makeDivider());
        rightMenu.add(btnLogout);
        topNavbar.add(rightMenu, BorderLayout.EAST);
        topHeaderWrapper.add(topNavbar);
        add(topHeaderWrapper, BorderLayout.NORTH);
        Runnable updateCartBadge = () -> {
            int totalItems = 0;
            for (CartItem item : cartBUS.getCartItems()) {
                totalItems += item.getQuantity();
            }
            btnCart.setText("Giỏ hàng (" + totalItems + ")");
            try {
                btnCart.setIcon(IconUtils.loadSmallIcon(IconUtils.IconType.TROLLEY));
            } catch (Exception ignored) {
            }
        };
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(BG_CONTENT);
        CustomerShopPanel shopPanel = new CustomerShopPanel(this.customerId, cartBUS, updateCartBadge);
        CustomerCartPanel cartPanel = new CustomerCartPanel(this.customerId, cartBUS, updateCartBadge);
        CustomerProfilePanel profilePanel = new CustomerProfilePanel(this.customerId);
        CustomerOrderPanel orderPanel = new CustomerOrderPanel(this.customerId);
        mainContentPanel.add(shopPanel, "Shop");
        mainContentPanel.add(profilePanel, "Profile");
        mainContentPanel.add(orderPanel, "Orders");
        mainContentPanel.add(cartPanel, "Cart");
        add(mainContentPanel, BorderLayout.CENTER);
        updateCartBadge.run(); 
        btnLogo.addActionListener(e -> cardLayout.show(mainContentPanel, "Shop"));
        btnProfile.addActionListener(e -> cardLayout.show(mainContentPanel, "Profile"));
        btnOrders.addActionListener(e -> {
            orderPanel.refresh(); 
            cardLayout.show(mainContentPanel, "Orders");
        });
        btnCart.addActionListener(e -> {
            cartPanel.refreshCartGUI();
            cardLayout.show(mainContentPanel, "Cart");
        });
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginForm().setVisible(true);
            }
        });
    }
    private JButton createNavButton(String text, boolean isAccent) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isAccent) {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(56, 189, 248),
                            getWidth(), 0, new Color(99, 210, 255));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(241, 245, 249));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(isAccent ? new Color(10, 18, 40) : NAV_TEXT);
        try {
            if (text.contains("Tài khoản")) {
                btn.setIcon(IconUtils.loadSmallIcon(IconUtils.IconType.USER));
            } else if (text.contains("Đơn hàng")) {
                btn.setIcon(IconUtils.loadSmallIcon(IconUtils.IconType.TAG));
            } else if (text.contains("Đăng xuất")) {
                btn.setIcon(IconUtils.loadSmallIcon(IconUtils.IconType.LOGOUT));
            }
        } catch (Exception ignored) {
        }
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        return btn;
    }
    private JLabel makeDivider() {
        JLabel div = new JLabel("|");
        div.setForeground(new Color(203, 213, 225));
        div.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return div;
    }
    private JPanel createDummyPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_CONTENT);
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl.setForeground(NAV_TEXT);
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
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
