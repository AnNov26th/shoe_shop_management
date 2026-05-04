package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import com.pbl_3project.util.TimeDisplayPanel;
import com.pbl_3project.gui.HeaderPanel;

public class AdminForm extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JButton selectedMenuButton;
    private final Color MENU_INACTIVE = Color.WHITE;
    private final Color MENU_ACTIVE = new Color(34, 177, 76);
    private final Color MENU_HOVER = new Color(240, 245, 249);
    private final Color TEXT_INACTIVE = new Color(50, 50, 50);
    private final Color TEXT_ACTIVE = Color.WHITE;
    private final Color APP_BACKGROUND = new Color(240, 244, 247);
    private int adminId;
    private int roleId;

    public AdminForm(int adminId, int roleId) {
        this.adminId = adminId;
        this.roleId = roleId;
        setTitle("Hệ thống Quản lý Cửa hàng Giày dép - [ADMIN]");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        getContentPane().setBackground(APP_BACKGROUND);
        initComponents();
    }

    private void initComponents() {
        HeaderPanel headerPanel = new HeaderPanel(this.adminId, this.roleId);
        add(headerPanel, BorderLayout.NORTH);
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(260, getHeight()));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
        JLabel lblAdmin = new JLabel("Shoe Shop T&T", SwingConstants.CENTER);
        lblAdmin.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblAdmin.setForeground(new Color(34, 177, 76));
        lblAdmin.setBorder(new EmptyBorder(25, 10, 25, 10));
        lblAdmin.setBackground(Color.WHITE);
        lblAdmin.setOpaque(true);
        sidebarPanel.add(lblAdmin, BorderLayout.NORTH);
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JButton btnThongKe = createMenuButton("Báo cáo & Thống kê");
        JButton btnSanPham = createMenuButton("Quản lý Sản phẩm");
        JButton btnDonHang = createMenuButton("Quản lý Đơn hàng Online");
        JButton btnHoaDon = createMenuButton("Lịch sử Hóa đơn");
        JButton btnKhuyenMai = createMenuButton("Quản lý Khuyến mãi");
        JButton btnNhanVien = createMenuButton("Quản lý Nhân sự");
        JButton btnDuyetYeuCau = createMenuButton("Duyệt Yêu cầu NV");
        JButton btnKhachHang = createMenuButton("Quản lý Khách hàng");
        JButton btnLogout = createMenuButton("Đăng xuất");
        btnLogout.setForeground(Color.RED);
        menuPanel.add(btnThongKe);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnSanPham);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnDonHang);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnHoaDon);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnKhuyenMai);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnNhanVien);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnDuyetYeuCau);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnKhachHang);
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(btnLogout);
        sidebarPanel.add(menuPanel, BorderLayout.CENTER);
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainContentPanel.add(new ProductManagementPanel(), "SanPham");
        mainContentPanel.add(new DashboardPanel(), "ThongKe");
        mainContentPanel.add(new EmployeeManagementPanel(), "NhanVien");
        mainContentPanel.add(new EmployeeRequestPanel(), "DuyetYeuCau");
        mainContentPanel.add(new CustomerManagementPanel(), "KhachHang");
        mainContentPanel.add(new OrderManagementPanel(true, false), "DonHang");
        mainContentPanel.add(new OrderManagementPanel(true, true), "HoaDon");
        mainContentPanel.add(new PromotionManagementPanel(), "KhuyenMai");
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(APP_BACKGROUND);
        contentWrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentWrapper.add(mainContentPanel, BorderLayout.CENTER);
        add(sidebarPanel, BorderLayout.WEST);
        add(contentWrapper, BorderLayout.CENTER);
        btnThongKe.addActionListener(e -> {
            selectMenuButton(btnThongKe);
            cardLayout.show(mainContentPanel, "ThongKe");
        });
        btnSanPham.addActionListener(e -> {
            selectMenuButton(btnSanPham);
            cardLayout.show(mainContentPanel, "SanPham");
        });
        btnDonHang.addActionListener(e -> {
            selectMenuButton(btnDonHang);
            cardLayout.show(mainContentPanel, "DonHang");
        });
        btnHoaDon.addActionListener(e -> {
            selectMenuButton(btnHoaDon);
            cardLayout.show(mainContentPanel, "HoaDon");
        });
        btnKhuyenMai.addActionListener(e -> {
            selectMenuButton(btnKhuyenMai);
            cardLayout.show(mainContentPanel, "KhuyenMai");
        });
        btnNhanVien.addActionListener(e -> {
            selectMenuButton(btnNhanVien);
            cardLayout.show(mainContentPanel, "NhanVien");
        });
        btnDuyetYeuCau.addActionListener(e -> {
            selectMenuButton(btnDuyetYeuCau);
            cardLayout.show(mainContentPanel, "DuyetYeuCau");
        });
        btnKhachHang.addActionListener(e -> {
            selectMenuButton(btnKhachHang);
            cardLayout.show(mainContentPanel, "KhachHang");
        });
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginForm().setVisible(true);
            }
        });
        selectMenuButton(btnThongKe);
        cardLayout.show(mainContentPanel, "ThongKe");
    }

    private void selectMenuButton(JButton selectedButton) {
        JButton oldSelected = selectedMenuButton;
        if (oldSelected != null && !oldSelected.equals(selectedButton)) {
            oldSelected.setBackground(MENU_INACTIVE);
            oldSelected.setForeground(TEXT_INACTIVE);
        }
        selectedButton.setBackground(MENU_ACTIVE);
        selectedButton.setForeground(TEXT_ACTIVE);
        selectedMenuButton = selectedButton;
        if (oldSelected != null)
            oldSelected.repaint();
        selectedButton.repaint();
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                
                // Active indicator
                if (equals(selectedMenuButton)) {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(8, 12, 4, getHeight() - 24, 2, 2);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_INACTIVE);
        btn.setBackground(MENU_INACTIVE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!btn.equals(selectedMenuButton)) {
                    btn.setBackground(MENU_HOVER);
                    btn.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!btn.equals(selectedMenuButton)) {
                    btn.setBackground(MENU_INACTIVE);
                    btn.repaint();
                }
            }
        });
        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new AdminForm(-1, 1).setVisible(true));
    }
}
