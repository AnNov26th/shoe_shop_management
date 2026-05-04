package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.table.DefaultTableModel;
import com.pbl_3project.dao.DashboardDAO;

public class DashboardPanel extends JPanel {
    private DashboardDAO dashboardDAO;
    private JLabel lblTotalUsers, lblTotalProducts, lblTotalStock, lblTotalRevenue;
    private JTable tableBrandStats, tableMonthlyStats, tableEmployeeStats;
    private ChartComponent chartStatus, chartCategory, chartMonthly, chartRating;

    public DashboardPanel() {
        dashboardDAO = new DashboardDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(25, 25, 25, 25));
        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 3, 25, 0));
        topCardsPanel.setBackground(new Color(248, 250, 252));
        topCardsPanel.setPreferredSize(new Dimension(0, 150));
        topCardsPanel.setOpaque(false);
        lblTotalUsers = new JLabel("0", SwingConstants.CENTER);
        JPanel card1 = createStatCard("Tài Khoản", lblTotalUsers, new Color(52, 152, 219));
        lblTotalProducts = new JLabel("0", SwingConstants.CENTER);
        JPanel card2 = createStatCard("Mẫu Giày", lblTotalProducts, new Color(155, 89, 182));
        lblTotalStock = new JLabel("0", SwingConstants.CENTER);
        JPanel card3 = createStatCard("Tổng Tồn Kho", lblTotalStock, new Color(46, 204, 113));
        lblTotalRevenue = new JLabel("0", SwingConstants.CENTER);
        JPanel card4 = createStatCard("Tổng Doanh Thu", lblTotalRevenue, new Color(230, 126, 34));
        topCardsPanel.setLayout(new GridLayout(1, 4, 20, 0));
        topCardsPanel.add(card1);
        topCardsPanel.add(card2);
        topCardsPanel.add(card3);
        topCardsPanel.add(card4);
        RoundedPanel centerPanel = new RoundedPanel(25, Color.WHITE);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel lblTableTitle = new JLabel("Báo Cáo & Phân Tích Chuyên Sâu");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTableTitle.setForeground(new Color(15, 23, 42));

        javax.swing.JTabbedPane tabbedPane = new javax.swing.JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        tableBrandStats = createStyledTable();
        tableMonthlyStats = createStyledTable();
        tabbedPane.addTab("Doanh thu theo Tháng", createTableScrollPane(tableMonthlyStats));
        tableEmployeeStats = createStyledTable();
        tabbedPane.addTab("Doanh thu theo Nhân viên", createTableScrollPane(tableEmployeeStats));
        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        JButton btnRefresh = new JButton("Cập nhật số liệu") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(45, 160, 180));
                } else {
                    g2.setColor(new Color(59, 190, 210));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setForeground(new Color(15, 23, 42));
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

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new javax.swing.BoxLayout(contentPanel, javax.swing.BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(248, 250, 252));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        topCardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(topCardsPanel);
        contentPanel.add(javax.swing.Box.createVerticalStrut(30));

        // 2. Brand Performance Section (Table + Pie Chart)
        JPanel brandSection = new JPanel(new BorderLayout(25, 0));
        brandSection.setOpaque(false);
        brandSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));

        JPanel brandTableWrapper = createTableWrapper(tableBrandStats, "Phân tích Tồn kho theo Thương hiệu");
        chartCategory = new ChartComponent(ChartComponent.ChartType.PIE, "Tỷ trọng Mẫu mã", null);
        JPanel brandChartWrapper = createChartWrapper(chartCategory, 400);
        brandChartWrapper.setPreferredSize(new Dimension(400, 400));

        brandSection.add(brandTableWrapper, BorderLayout.CENTER);
        brandSection.add(brandChartWrapper, BorderLayout.EAST);

        contentPanel.add(brandSection);
        contentPanel.add(javax.swing.Box.createVerticalStrut(40));

        // 3. Other Charts Section
        JPanel otherChartsSection = new JPanel(new GridLayout(0, 3, 25, 25)); // 3 charts per row
        otherChartsSection.setOpaque(false);
        otherChartsSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        chartStatus = new ChartComponent(ChartComponent.ChartType.PIE, "Trạng thái Đơn hàng", null);
        chartMonthly = new ChartComponent(ChartComponent.ChartType.BAR, "Doanh thu Tháng", null);
        chartRating = new ChartComponent(ChartComponent.ChartType.BAR, "Đánh giá Khách hàng", null);

        otherChartsSection.add(createChartWrapper(chartStatus, 350));
        otherChartsSection.add(createChartWrapper(chartMonthly, 350));
        otherChartsSection.add(createChartWrapper(chartRating, 350));

        contentPanel.add(otherChartsSection);
        contentPanel.add(javax.swing.Box.createVerticalStrut(40));

        // 4. Detailed Tables (Remaining)
        centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        contentPanel.add(centerPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color bgColor) {
        RoundedPanel card = new RoundedPanel(25, bgColor);
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(255, 255, 255, 230));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JTable createStyledTable() {
        JTable table = new JTable();
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(15, 23, 42));
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(Color.DARK_GRAY);
        table.setShowVerticalLines(false);
        return table;
    }

    private JScrollPane createTableScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private JPanel createTableWrapper(JTable table, String title) {
        RoundedPanel wrapper = new RoundedPanel(25, Color.WHITE);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        wrapper.add(lblTitle, BorderLayout.NORTH);
        wrapper.add(createTableScrollPane(table), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createChartWrapper(ChartComponent chart, int height) {
        RoundedPanel wrapper = new RoundedPanel(20, Color.WHITE);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        wrapper.setPreferredSize(new Dimension(300, height));
        wrapper.add(chart, BorderLayout.CENTER);
        return wrapper;
    }

    private void loadData() {
        try {
            Map<String, Object> stats = dashboardDAO.getQuickStats();
            lblTotalUsers.setText(String.valueOf(stats.getOrDefault("total_users", 0)));
            lblTotalProducts.setText(String.valueOf(stats.getOrDefault("total_products", 0)));
            lblTotalStock.setText(String.format("%,d", (Integer) stats.getOrDefault("total_stock", 0)));
            double revenue = (Double) stats.getOrDefault("total_revenue", 0.0);
            lblTotalRevenue.setText(String.format("%,.0f", revenue));

            Map<String, Integer> statusData = dashboardDAO.getOrderStatusDistribution();
            Map<String, Double> statusDataDouble = new java.util.HashMap<>();
            statusData.forEach((k, v) -> statusDataDouble.put(k, v.doubleValue()));
            chartStatus.setData(statusDataDouble);

            Map<String, Integer> catData = dashboardDAO.getCategoryDistribution();
            Map<String, Double> catDataDouble = new java.util.HashMap<>();
            catData.forEach((k, v) -> catDataDouble.put(k, v.doubleValue()));
            chartCategory.setData(catDataDouble);

            DefaultTableModel monthlyModel = dashboardDAO.getMonthlyRevenueStatistics();
            Map<String, Double> monthlyData = new java.util.LinkedHashMap<>();
            for (int i = Math.min(monthlyModel.getRowCount() - 1, 5); i >= 0; i--) {
                String month = monthlyModel.getValueAt(i, 0).toString();
                double rev = Double.parseDouble(monthlyModel.getValueAt(i, 2).toString().replace(",", ""));
                monthlyData.put(month, rev);
            }
            chartMonthly.setData(monthlyData);

            Map<Integer, Integer> ratingData = dashboardDAO.getRatingDistribution();
            Map<String, Double> ratingDataDouble = new java.util.LinkedHashMap<>();
            ratingData.forEach((k, v) -> ratingDataDouble.put(k + " Sao", v.doubleValue()));
            chartRating.setData(ratingDataDouble);

            tableBrandStats.setModel(dashboardDAO.getBrandStatistics());
            tableMonthlyStats.setModel(monthlyModel);
            tableEmployeeStats.setModel(dashboardDAO.getEmployeeRevenueStatistics());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Thống kê: " + e.getMessage());
        }
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 5, radius, radius);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 6, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
