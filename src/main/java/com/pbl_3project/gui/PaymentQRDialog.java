package com.pbl_3project.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class PaymentQRDialog extends JDialog {
    private boolean paymentSuccessful = false;

    public PaymentQRDialog(Frame parent, double amount, String orderId) {
        super(parent, "Thanh toán QR", true);
        initComponents(amount, orderId);
    }

    public PaymentQRDialog(Dialog parent, double amount, String orderId) {
        super(parent, "Thanh toán QR", true);
        initComponents(amount, orderId);
    }

    private void initComponents(double amount, String orderId) {
        setLayout(new BorderLayout(10, 10));
        setSize(400, 550);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        JLabel lblTitle = new JLabel("QUÉT MÃ THANH TOÁN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel lblAmount = new JLabel("Số tiền: " + String.format("%,.0f VNĐ", amount));
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAmount.setForeground(new Color(231, 76, 60));
        JLabel lblInfo = new JLabel("Nội dung: Thanh toan don hang " + orderId);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblQR = new JLabel("Đang tải mã QR...");
        lblQR.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblQR.setPreferredSize(new Dimension(300, 300));
        lblQR.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        new Thread(() -> {
            try {
                String qrUrl = String.format(
                        "https://img.vietqr.io/image/970422-0941910604-compact.jpg?amount=%f&addInfo=%s",
                        amount, orderId);
                URL url = new URL(qrUrl);
                BufferedImage img = ImageIO.read(url);
                if (img != null) {
                    ImageIcon icon = new ImageIcon(img.getScaledInstance(300, 300, Image.SCALE_SMOOTH));
                    SwingUtilities.invokeLater(() -> {
                        lblQR.setIcon(icon);
                        lblQR.setText("");
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> lblQR.setText("Lỗi tải mã QR. Vui lòng thử lại."));
                e.printStackTrace();
            }
        }).start();
        contentPanel.add(lblAmount);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(lblInfo);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(lblQR);
        contentPanel.add(Box.createVerticalStrut(20));
        add(contentPanel, BorderLayout.CENTER);
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(Color.WHITE);
        JButton btnCancel = new JButton("Hủy bỏ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(240, 240, 240) : new Color(250, 250, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());
        JButton btnConfirm = new JButton("Xác nhận đã thanh toán") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = new Color(46, 204, 113);
                Color c2 = new Color(39, 174, 96);
                GradientPaint gp = getModel().isRollover()
                        ? new GradientPaint(0, 0, c2, getWidth(), 0, c1)
                        : new GradientPaint(0, 0, c1, getWidth(), 0, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setForeground(Color.BLACK);
        btnConfirm.setContentAreaFilled(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(e -> {
            paymentSuccessful = true;
            dispose();
        });
        footerPanel.add(btnCancel);
        footerPanel.add(btnConfirm);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }
}
