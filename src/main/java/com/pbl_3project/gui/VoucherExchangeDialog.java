package com.pbl_3project.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.pbl_3project.util.DatabaseConnection;

public class VoucherExchangeDialog extends JDialog {
    private int customerId;
    private String customerName;
    private int currentPoints;
    private JLabel lblPoints;

    public VoucherExchangeDialog(Frame parent, int customerId, String customerName, int currentPoints) {
        super(parent, "Đổi mã giảm giá - " + customerName, true);
        this.customerId = customerId;
        this.customerName = customerName;
        this.currentPoints = currentPoints;

        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        initComponents();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(15, 23, 42), getWidth(), 0, new Color(30, 41, 59)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Danh Sách Mã Giảm Giá Khả Dụng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("(100.000đ chi tiêu = 1 điểm | 1 điểm = 10.000đ giảm giá)");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitle.setForeground(new Color(148, 163, 184));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        lblPoints = new JLabel("Điểm hiện tại: " + currentPoints);
        lblPoints.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPoints.setForeground(new Color(52, 211, 153));

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(lblPoints, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        contentPanel.setBackground(new Color(248, 250, 252));
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Load vouchers from database
        loadVouchersFromDatabase(contentPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(248, 250, 252));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JButton btnClose = new JButton("Đóng") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover())
                    g2.setColor(new Color(226, 232, 240));
                else
                    g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setForeground(new Color(15, 23, 42));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setPreferredSize(new Dimension(100, 40));
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadVouchersFromDatabase(JPanel contentPanel) {
        String sql = "SELECT * FROM [Promotion] WHERE (usage_limit IS NULL OR usage_limit > current_usage) AND end_date >= GETDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
                java.sql.Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            boolean hasVoucher = false;
            while (rs.next()) {
                hasVoucher = true;
                int id = rs.getInt("id");
                String code = rs.getString("code");
                String type = rs.getString("type");
                double value = rs.getDouble("discount_value");
                double minOrder = rs.getDouble("min_order_value");
                double maxDiscount = rs.getDouble("max_discount_amount");

                int pointsReq = 0;
                if ("Fixed".equalsIgnoreCase(type)) {
                    pointsReq = (int) (value / 10000);
                } else {
                    pointsReq = (int) (maxDiscount / 10000);
                }
                if (pointsReq <= 0)
                    pointsReq = 1;

                String title = "";
                if ("Fixed".equalsIgnoreCase(type)) {
                    title = "Giảm " + String.format("%,.0fđ", value);
                } else {
                    title = "Giảm " + String.format("%,.0f%%", value);
                }

                addVoucherCard(contentPanel, id, code, title, type, value, minOrder, maxDiscount, pointsReq);
            }
            if (!hasVoucher) {
                JLabel lblEmpty = new JLabel("Hiện không có mã giảm giá nào khả dụng.", SwingConstants.CENTER);
                lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                lblEmpty.setForeground(new Color(100, 116, 139));
                contentPanel.add(lblEmpty);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JLabel lblError = new JLabel("Lỗi khi tải mã giảm giá.", SwingConstants.CENTER);
            lblError.setForeground(Color.RED);
            contentPanel.add(lblError);
        }
    }

    private void addVoucherCard(JPanel parent, int promoId, String code, String title, String type, double value,
            double minOrder, double maxDiscount, int pointsReq) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        infoPanel.setOpaque(false);
        JLabel lblTitle = new JLabel(title + " (" + code + ")");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));

        String desc = String.format("Đơn tối thiểu: %,.0fđ - Tối đa: %,.0fđ", minOrder, maxDiscount);
        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setForeground(new Color(100, 116, 139));

        infoPanel.add(lblTitle);
        infoPanel.add(lblDesc);

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        JLabel lblReq = new JLabel(pointsReq + " Điểm");
        lblReq.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblReq.setForeground(new Color(239, 68, 68));
        lblReq.setHorizontalAlignment(SwingConstants.CENTER);
        lblReq.setBorder(new EmptyBorder(0, 0, 0, 20));

        JButton btnExchange = new JButton("Đổi ngay") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) {
                    g2.setColor(new Color(203, 213, 225));
                } else if (getModel().isRollover()) {
                    g2.setPaint(
                            new GradientPaint(0, 0, new Color(52, 211, 153), getWidth(), 0, new Color(16, 185, 129)));
                } else {
                    g2.setColor(new Color(16, 185, 129));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnExchange.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExchange.setForeground(Color.WHITE);
        btnExchange.setContentAreaFilled(false);
        btnExchange.setBorderPainted(false);
        btnExchange.setFocusPainted(false);
        btnExchange.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExchange.setPreferredSize(new Dimension(110, 40));

        if (currentPoints < pointsReq) {
            btnExchange.setEnabled(false);
        }

        btnExchange.addActionListener(e -> exchangeVoucher(promoId, code, pointsReq));

        actionPanel.add(lblReq, BorderLayout.CENTER);
        actionPanel.add(btnExchange, BorderLayout.EAST);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);

        parent.add(card);
    }

    private void exchangeVoucher(int promoId, String promoCode, int pointsReq) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn dùng " + pointsReq + " điểm để lấy mã này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false);

                // Deduct points
                String sqlDeduct = "UPDATE Customer_Profile SET reward_points = reward_points - ? WHERE user_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlDeduct)) {
                    pstmt.setInt(1, pointsReq);
                    pstmt.setInt(2, customerId);
                    int affected = pstmt.executeUpdate();
                    if (affected == 0)
                        throw new SQLException("Không tìm thấy khách hàng!");
                }

                // Decrease usage limit (simulating reducing quantity from database)
                String sqlPromo = "UPDATE [Promotion] SET usage_limit = usage_limit - 1 WHERE id = ? AND usage_limit > current_usage";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlPromo)) {
                    pstmt.setInt(1, promoId);
                    int affected = pstmt.executeUpdate();
                    if (affected == 0) {
                        throw new SQLException("Mã giảm giá này đã hết lượt sử dụng!");
                    }
                }

                // Record the exchanged voucher for the customer
                String sqlCustomerVoucher = "INSERT INTO Customer_Voucher (customer_id, promo_code) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlCustomerVoucher)) {
                    pstmt.setInt(1, customerId);
                    pstmt.setString(2, promoCode);
                    pstmt.executeUpdate();
                }

                conn.commit();
                currentPoints -= pointsReq;
                lblPoints.setText("Điểm hiện tại: " + currentPoints);
                JOptionPane.showMessageDialog(this,
                        "Đổi thành công!\nMã giảm giá của bạn là: " + promoCode
                                + "\nBạn có thể sử dụng mã này khi thanh toán.",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (SQLException ex) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException e) {
                    }
                }
                JOptionPane.showMessageDialog(this, "Lỗi khi đổi điểm: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        DatabaseConnection.closeConnection(conn);
                    } catch (SQLException e) {
                    }
                }
            }
        }
    }
}
