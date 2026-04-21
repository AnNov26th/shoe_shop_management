package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadData();
    }

    private void initComponents() {
        // --- 1. KHU VỰC 3 THẺ TỔNG QUAN Ở TRÊN CÙNG ---
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        topCardsPanel.setBackground(new Color(245, 245, 245));
        topCardsPanel.setPreferredSize(new Dimension(0, 140));

        // Tạo 3 thẻ với 3 màu sắc khác nhau
        lblTotalUsers = new JLabel("0", SwingConstants.CENTER);
        JPanel card1 = createStatCard("👥 Tài Khoản Hệ Thống", lblTotalUsers, new Color(52, 152, 219));

        lblTotalProducts = new JLabel("0", SwingConstants.CENTER);
        JPanel card2 = createStatCard("👟 Mẫu Giày Kinh Doanh", lblTotalProducts, new Color(155, 89, 182));

        lblTotalStock = new JLabel("0", SwingConstants.CENTER);
        JPanel card3 = createStatCard("📦 Tổng Tồn Kho", lblTotalStock, new Color(46, 204, 113));

        topCardsPanel.add(card1);
        topCardsPanel.add(card2);
        topCardsPanel.add(card3);

        // --- 2. KHU VỰC BẢNG PHÂN TÍCH BÊN DƯỚI ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "📊 Phân tích tồn kho theo Thương hiệu",
                0, 0, new Font("Arial", Font.BOLD, 14)));

        tableBrandStats = new JTable();
        tableBrandStats.setRowHeight(35);
        tableBrandStats.setFont(new Font("Arial", Font.PLAIN, 12));
        tableBrandStats.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tableBrandStats.getTableHeader().setBackground(new Color(59, 190, 210));
        tableBrandStats.getTableHeader().setForeground(Color.BLACK);
        tableBrandStats.setGridColor(new Color(220, 220, 220));
        tableBrandStats.setSelectionBackground(new Color(200, 230, 255));

        JScrollPane scrollPane = new JScrollPane(tableBrandStats);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Nút làm mới dữ liệu
        JButton btnRefresh = new JButton("[Cập nhật số liệu]");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setBackground(new Color(59, 190, 210));
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRefresh.addActionListener(e -> loadData());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(btnRefresh);

        // Lắp ráp vào Form chính
        add(topCardsPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Hàm tiện ích để vẽ một thẻ thống kê
    private JPanel createStatCard(String title, JLabel valueLabel, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(3, 3, 8, 8),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitle.setForeground(Color.BLACK);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 40));
        valueLabel.setForeground(Color.BLACK);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // Hàm gọi DB để lấy dữ liệu đổ lên màn hình
    private void loadData() {
        try {
            // Lấy 3 số tổng quan
            Map<String, Integer> stats = dashboardDAO.getQuickStats();
            lblTotalUsers.setText(String.valueOf(stats.getOrDefault("total_users", 0)));
            lblTotalProducts.setText(String.valueOf(stats.getOrDefault("total_products", 0)));
            lblTotalStock.setText(String.valueOf(stats.getOrDefault("total_stock", 0)));

            // Lấy bảng thống kê thương hiệu
            tableBrandStats.setModel(dashboardDAO.getBrandStatistics());

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Thống kê: " + e.getMessage());
        }
    }
}