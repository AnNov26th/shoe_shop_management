package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.pbl_3project.dao.DashboardDAO;

public class DashboardPanel extends JPanel {

    private DashboardDAO dashboardDAO;
    private JLabel lblTotalUsers, lblTotalProducts, lblTotalStock;
    private JTable tableBrandStats;

    public DashboardPanel() {
        dashboardDAO = new DashboardDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(248, 250, 252)); // Nền xám cực nhạt cho hiện đại
        setBorder(new EmptyBorder(25, 25, 25, 25));

        initComponents();
        loadData();
    }

    private void initComponents() {
        // --- 1. KHU VỰC 3 THẺ TỔNG QUAN Ở TRÊN CÙNG ---
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 3, 25, 0));
        topCardsPanel.setBackground(new Color(248, 250, 252));
        topCardsPanel.setPreferredSize(new Dimension(0, 150));
        topCardsPanel.setOpaque(false);

        // Tạo 3 thẻ với 3 màu sắc nổi bật
        lblTotalUsers = new JLabel("0", SwingConstants.CENTER);
        JPanel card1 = createStatCard("Tài Khoản Hệ Thống", lblTotalUsers, new Color(52, 152, 219));

        lblTotalProducts = new JLabel("0", SwingConstants.CENTER);
        JPanel card2 = createStatCard("Mẫu Giày Kinh Doanh", lblTotalProducts, new Color(155, 89, 182));

        lblTotalStock = new JLabel("0", SwingConstants.CENTER);
        JPanel card3 = createStatCard("Tổng Tồn Kho", lblTotalStock, new Color(46, 204, 113));

        topCardsPanel.add(card1);
        topCardsPanel.add(card2);
        topCardsPanel.add(card3);

        // --- 2. KHU VỰC BẢNG PHÂN TÍCH BÊN DƯỚI ---
        // Sử dụng RoundedPanel cho cả khu vực bảng để đồng bộ độ mượt
        RoundedPanel centerPanel = new RoundedPanel(25, Color.WHITE);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTableTitle = new JLabel("Phân tích tồn kho theo Thương hiệu");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTableTitle.setForeground(new Color(15, 23, 42));
        lblTableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        centerPanel.add(lblTableTitle, BorderLayout.NORTH);

        tableBrandStats = new JTable();
        tableBrandStats.setRowHeight(40);
        tableBrandStats.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableBrandStats.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableBrandStats.getTableHeader().setBackground(new Color(241, 245, 249));
        tableBrandStats.getTableHeader().setForeground(new Color(15, 23, 42));
        tableBrandStats.setGridColor(new Color(226, 232, 240));
        tableBrandStats.setSelectionBackground(new Color(239, 246, 255));
        tableBrandStats.setSelectionForeground(Color.DARK_GRAY);
        tableBrandStats.setShowVerticalLines(false);

        JScrollPane scrollPane = new JScrollPane(tableBrandStats);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // --- 3. NÚT LÀM MỚI DỮ LIỆU ---
        JButton btnRefresh = new JButton("Cập nhật số liệu") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(45, 160, 180)); // Màu xanh đậm hơn khi rê chuột
                } else {
                    g2.setColor(new Color(59, 190, 210)); // Màu xanh Cyan mặc định
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g); // Vẽ chữ đè lên trên nền
            }
        };
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setForeground(new Color(15, 23, 42)); // Đổi sang chữ Xanh Navy đậm cực kỳ dễ đọc

        // 3 Dòng siêu quan trọng để tắt giao diện nút vuông mặc định của Windows
        btnRefresh.setFocusPainted(false);
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setBorderPainted(false);

        btnRefresh.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadData());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        bottomPanel.add(btnRefresh);

        // Lắp ráp vào Form chính
        add(topCardsPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- HÀM TẠO THẺ THỐNG KÊ BO GÓC MƯỢT MÀ ---
    private JPanel createStatCard(String title, JLabel valueLabel, Color bgColor) {
        // Sử dụng RoundedPanel với độ bo góc (radius) là 25px
        RoundedPanel card = new RoundedPanel(25, bgColor);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE); // Chữ trắng trên nền màu nhìn sẽ sang hơn

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLabel.setForeground(Color.WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // --- HÀM GỌI DB ---
    private void loadData() {
        try {
            Map<String, Integer> stats = dashboardDAO.getQuickStats();
            lblTotalUsers.setText(String.valueOf(stats.getOrDefault("total_users", 0)));
            lblTotalProducts.setText(String.valueOf(stats.getOrDefault("total_products", 0)));
            lblTotalStock.setText(String.format("%,d", stats.getOrDefault("total_stock", 0)));

            tableBrandStats.setModel(dashboardDAO.getBrandStatistics());

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Thống kê: " + e.getMessage());
        }
    }

    // ==========================================
    // CLASS CUSTOM: Vẽ Panel Bo Góc & Đổ Bóng
    // ==========================================
    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false); // Quan trọng để thấy được viền bo cong
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            // Bật khử răng cưa để nét vẽ bo tròn không bị rỗ
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Vẽ bóng đổ (Drop Shadow) màu đen trong suốt nhẹ phía dưới
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 5, radius, radius);

            // Vẽ nền thẻ chính
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 6, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}