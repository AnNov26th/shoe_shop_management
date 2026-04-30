package com.pbl_3project.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ReturnRequestDialog extends JDialog {
    private static final Color BG_LIGHT = new Color(248, 250, 252);
    private static final Color SHOPEE_ORANGE = new Color(238, 77, 45);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color TEXT_MAIN = new Color(15, 23, 42);
    private static final Color TEXT_SUB = new Color(100, 116, 139);

    private JComboBox<String> cmbReason;
    private JTextField txtOtherReason;
    private JRadioButton rbRefund, rbExchange;
    private JTextArea txtDetails;
    private boolean confirmed = false;

    public ReturnRequestDialog(Frame parent, int orderId) {
        super(parent, "Yêu cầu Đổi / Trả hàng - Đơn #" + orderId, true);
        initComponents();
    }

    private void initComponents() {
        setSize(550, 650);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_LIGHT);

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));
        JLabel lblTitle = new JLabel("Yêu cầu Đổi / Trả hàng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_MAIN);
        header.add(lblTitle, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // --- CONTENT ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Section 1: Lý do
        mainPanel.add(createSectionHeader("Lý do đổi / trả"));
        JPanel pnlReason = createCardPanel();
        pnlReason.setLayout(new BorderLayout(0, 10));
        
        String[] reasons = {
            "Lỗi sản phẩm (Rách, hỏng, bong keo...)", 
            "Giao sai mẫu mã / kích cỡ / màu sắc", 
            "Kích cỡ không vừa", 
            "Sản phẩm không giống mô tả", 
            "Khác (Vui lòng ghi rõ bên dưới)"
        };
        cmbReason = new JComboBox<>(reasons);
        cmbReason.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbReason.setBackground(Color.WHITE);
        pnlReason.add(cmbReason, BorderLayout.NORTH);

        txtOtherReason = new JTextField();
        txtOtherReason.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtOtherReason.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER_COLOR), "Lý do cụ thể"));
        txtOtherReason.setVisible(false);
        pnlReason.add(txtOtherReason, BorderLayout.CENTER);

        cmbReason.addActionListener(e -> {
            txtOtherReason.setVisible(cmbReason.getSelectedItem().equals("Khác (Vui lòng ghi rõ bên dưới)"));
            revalidate();
            repaint();
        });
        mainPanel.add(pnlReason);
        mainPanel.add(Box.createVerticalStrut(20));

        // Section 2: Hình thức
        mainPanel.add(createSectionHeader("Hình thức mong muốn"));
        JPanel pnlType = createCardPanel();
        pnlType.setLayout(new GridLayout(2, 1, 0, 10));
        rbRefund = new JRadioButton("Trả hàng và Hoàn tiền (Nếu không muốn nhận SP nữa)", true);
        rbExchange = new JRadioButton("Đổi sang sản phẩm khác / size khác");
        rbRefund.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rbExchange.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rbRefund.setBackground(Color.WHITE);
        rbExchange.setBackground(Color.WHITE);
        ButtonGroup group = new ButtonGroup();
        group.add(rbRefund);
        group.add(rbExchange);
        pnlType.add(rbRefund);
        pnlType.add(rbExchange);
        mainPanel.add(pnlType);
        mainPanel.add(Box.createVerticalStrut(20));

        // Section 3: Chi tiết
        mainPanel.add(createSectionHeader("Mô tả chi tiết & Sản phẩm muốn đổi"));
        JPanel pnlDetails = createCardPanel();
        pnlDetails.setLayout(new BorderLayout());
        txtDetails = new JTextArea(6, 20);
        txtDetails.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDetails.setLineWrap(true);
        txtDetails.setWrapStyleWord(true);
        txtDetails.setPlaceholder("Ví dụ: Giày bị rộng size 40, muốn đổi sang size 39 cùng mẫu...");
        JScrollPane scroll = new JScrollPane(txtDetails);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        pnlDetails.add(scroll, BorderLayout.CENTER);
        mainPanel.add(pnlDetails);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JButton btnCancel = createStyledButton("Hủy bỏ", Color.WHITE, TEXT_SUB, true);
        JButton btnSubmit = createStyledButton("Gửi yêu cầu", SHOPEE_ORANGE, Color.WHITE, false);
        
        btnCancel.addActionListener(e -> dispose());
        btnSubmit.addActionListener(e -> {
            if (txtDetails.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mô tả chi tiết để cửa hàng xử lý nhanh hơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            confirmed = true;
            dispose();
        });

        footer.add(btnCancel);
        footer.add(btnSubmit);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createCardPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private JLabel createSectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(TEXT_MAIN);
        lbl.setBorder(new EmptyBorder(0, 5, 10, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton createStyledButton(String text, Color bg, Color fg, boolean hasBorder) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (hasBorder) {
                    g2.setColor(BORDER_COLOR);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    static class JTextArea extends javax.swing.JTextArea {
        private String placeholder;
        public JTextArea(int rows, int cols) { super(rows, cols); }
        public void setPlaceholder(String p) { this.placeholder = p; }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && placeholder != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.LIGHT_GRAY);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                g2.drawString(placeholder, 10, 20);
                g2.dispose();
            }
        }
    }

    public boolean isConfirmed() { return confirmed; }
    public String getReason() {
        if (cmbReason.getSelectedItem().equals("Khác (Vui lòng ghi rõ bên dưới)")) return txtOtherReason.getText();
        return (String) cmbReason.getSelectedItem();
    }
    public String getReturnRequestType() { return rbRefund.isSelected() ? "Trả hàng hoàn tiền" : "Đổi hàng"; }
    public String getDetails() { return txtDetails.getText(); }
}
