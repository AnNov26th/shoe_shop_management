package com.pbl_3project.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.pbl_3project.util.TimeDisplayPanel;

public class EmployeeForm extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JButton selectedMenuButton;
    private int currentStaffId;

    private final Color MENU_INACTIVE = new Color(15, 23, 42);
    private final Color MENU_ACTIVE = new Color(56, 189, 248);
    private final Color MENU_HOVER = new Color(30, 41, 59);
    private final Color TEXT_INACTIVE = Color.WHITE;
    private final Color TEXT_ACTIVE = new Color(15, 23, 42);
    private final Color APP_BACKGROUND = new Color(241, 245, 249);

    public EmployeeForm(int staffId) {
        this.currentStaffId = staffId;
        setTitle("Dashboard Nhân Viên - T&T Shoes");
        setSize(1250, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(APP_BACKGROUND);

        initComponents();
    }

    private void initComponents() {
        add(new TimeDisplayPanel(), BorderLayout.NORTH);

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(260, 0));
        sidebarPanel.setBackground(MENU_INACTIVE);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(new EmptyBorder(25, 15, 25, 15));

        JLabel lblEmployee = new JLabel("STAFF DASHBOARD", SwingConstants.CENTER);
        lblEmployee.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblEmployee.setForeground(MENU_ACTIVE);
        lblEmployee.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(lblEmployee);
        sidebarPanel.add(Box.createVerticalStrut(40));

        JButton btnPOS = createMenuButton("Bán hàng tại quầy (POS)", com.pbl_3project.util.IconUtils.IconType.TROLLEY);
        JButton btnDonOnline = createMenuButton("Đơn hàng Online", com.pbl_3project.util.IconUtils.IconType.TAG);
        JButton btnTonKho = createMenuButton("Tra cứu tồn kho", com.pbl_3project.util.IconUtils.IconType.WAREHOUSE);
        JButton btnDoiTra = createMenuButton("Yêu cầu Đổi/Trả", com.pbl_3project.util.IconUtils.IconType.USER);
        JButton btnLogout = createMenuButton("Đăng xuất", com.pbl_3project.util.IconUtils.IconType.LOGOUT);

        sidebarPanel.add(btnPOS);
        sidebarPanel.add(Box.createVerticalStrut(8));
        sidebarPanel.add(btnDonOnline);
        sidebarPanel.add(Box.createVerticalStrut(8));
        sidebarPanel.add(btnTonKho);
        sidebarPanel.add(Box.createVerticalStrut(8));
        sidebarPanel.add(btnDoiTra);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(btnLogout);

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

        // ĐÃ FIX: TRUYỀN ID CHO POS VÀ INVENTORY
        mainContentPanel.add(new POSPanel(this.currentStaffId), "POS");
        mainContentPanel.add(new OrderManagementPanel(false), "DonOnline");
        mainContentPanel.add(new InventoryLookupPanel(this.currentStaffId), "TonKho");
        mainContentPanel.add(createDummyPanel("TIẾP NHẬN ĐỔI / TRẢ", "Chờ cập nhật..."), "DoiTra");

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(APP_BACKGROUND);
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
        wrapper.add(mainContentPanel, BorderLayout.CENTER);

        add(sidebarPanel, BorderLayout.WEST);
        add(wrapper, BorderLayout.CENTER);

        btnPOS.addActionListener(e -> {
            selectMenuButton(btnPOS);
            cardLayout.show(mainContentPanel, "POS");
        });
        btnDonOnline.addActionListener(e -> {
            selectMenuButton(btnDonOnline);
            cardLayout.show(mainContentPanel, "DonOnline");
        });
        btnTonKho.addActionListener(e -> {
            selectMenuButton(btnTonKho);
            cardLayout.show(mainContentPanel, "TonKho");
        });
        btnDoiTra.addActionListener(e -> {
            selectMenuButton(btnDoiTra);
            cardLayout.show(mainContentPanel, "DoiTra");
        });

        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginForm().setVisible(true);
            }
        });

        selectMenuButton(btnPOS);
        cardLayout.show(mainContentPanel, "POS");
    }

    private void selectMenuButton(JButton btn) {
        if (selectedMenuButton != null) {
            selectedMenuButton.setBackground(MENU_INACTIVE);
            selectedMenuButton.setForeground(TEXT_INACTIVE);
        }
        btn.setBackground(MENU_ACTIVE);
        btn.setForeground(TEXT_ACTIVE);
        selectedMenuButton = btn;
    }

    private JButton createMenuButton(String text, com.pbl_3project.util.IconUtils.IconType iconType) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (text.equals("Đăng xuất") && getModel().isRollover())
                    g2.setColor(new Color(239, 68, 68));
                else
                    g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_INACTIVE);
        btn.setBackground(MENU_INACTIVE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);
        if (iconType != null) {
            btn.setIcon(com.pbl_3project.util.IconUtils.loadMenuIcon(iconType));
        }
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != selectedMenuButton)
                    btn.setBackground(MENU_HOVER);
            }

            public void mouseExited(MouseEvent e) {
                if (btn != selectedMenuButton)
                    btn.setBackground(MENU_INACTIVE);
            }
        });
        return btn;
    }

    private JPanel createDummyPanel(String title, String desc) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }
}