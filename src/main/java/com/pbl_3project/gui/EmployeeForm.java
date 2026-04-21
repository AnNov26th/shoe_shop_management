package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
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

public class EmployeeForm extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private int currentStaffId;

    private final Color SIDEBAR_COLOR = new Color(41, 53, 65);
    private final Color HOVER_COLOR = new Color(59, 190, 210);
    private final Color TEXT_COLOR = Color.BLACK;

    public EmployeeForm(int staffId) {
        this.currentStaffId = staffId;
        setTitle("Hệ thống Quản lý Cửa hàng Giày dép - [QUYỀN NHÂN VIÊN]");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        // Thêm TimeDisplayPanel ở trên
        TimeDisplayPanel timePanel = new TimeDisplayPanel();
        add(timePanel, BorderLayout.NORTH);

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setLayout(new BorderLayout());

        JLabel lblEmployee = new JLabel("STAFF DASHBOARD", SwingConstants.CENTER);
        lblEmployee.setFont(new Font("Cambria", Font.BOLD, 18));
        lblEmployee.setForeground(new Color(59, 190, 210));
        lblEmployee.setBorder(new EmptyBorder(20, 10, 20, 10));
        sidebarPanel.add(lblEmployee, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(8, 1, 0, 5));
        menuPanel.setBackground(SIDEBAR_COLOR);

        JButton btnPOS = createMenuButton("Bán hàng tại quầy (POS)");
        JButton btnDonOnline = createMenuButton("Đơn hàng Online");
        JButton btnTonKho = createMenuButton("Tra cứu tồn kho");
        JButton btnDoiTra = createMenuButton("Yêu cầu Đổi/Trả");

        JButton btnLogout = createMenuButton("Đăng xuất");
        btnLogout.setForeground(new Color(255, 100, 100));

        menuPanel.add(btnPOS);
        menuPanel.add(btnDonOnline);
        menuPanel.add(btnTonKho);
        menuPanel.add(btnDoiTra);
        menuPanel.add(new JLabel());
        menuPanel.add(new JLabel());
        menuPanel.add(new JLabel());
        menuPanel.add(btnLogout);

        sidebarPanel.add(menuPanel, BorderLayout.CENTER);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(240, 244, 247));

        // Vẫn truyền đúng ID vào POSPanel
        mainContentPanel.add(new POSPanel(this.currentStaffId), "POS");
        mainContentPanel.add(
                createDummyPanel("QUẢN LÝ ĐƠN HÀNG ONLINE", "Tiếp nhận đơn từ app, xác nhận đóng gói, hủy đơn..."),
                "DonOnline");

        // Nhớ đảm bảo bro đã tạo class InventoryLookupPanel nhé!
        mainContentPanel.add(new InventoryLookupPanel(), "TonKho");

        mainContentPanel.add(createDummyPanel("TIẾP NHẬN ĐỔI / TRẢ", "Tạo phiếu đổi trả chờ Admin duyệt..."), "DoiTra");

        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        btnPOS.addActionListener(e -> cardLayout.show(mainContentPanel, "POS"));
        btnDonOnline.addActionListener(e -> cardLayout.show(mainContentPanel, "DonOnline"));
        btnTonKho.addActionListener(e -> cardLayout.show(mainContentPanel, "TonKho"));
        btnDoiTra.addActionListener(e -> cardLayout.show(mainContentPanel, "DoiTra"));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginForm().setVisible(true);
            }
        });
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Cambria", Font.BOLD, 14));
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(SIDEBAR_COLOR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_COLOR);
            }
        });
        return btn;
    }

    private JPanel createDummyPanel(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Cambria", Font.BOLD, 28));
        lblTitle.setForeground(new Color(51, 51, 51));
        JLabel lblDesc = new JLabel(description);
        lblDesc.setFont(new Font("Cambria", Font.ITALIC, 16));
        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblDesc, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ĐÃ FIX: Truyền tạm ID = 44 (nhân viên test) vào để chạy độc lập không bị lỗi
        // đỏ
        SwingUtilities.invokeLater(() -> new EmployeeForm(44).setVisible(true));
    }
}