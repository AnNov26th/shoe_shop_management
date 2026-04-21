package com.pbl_3project.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.util.Map;
import java.util.HashMap;
import com.pbl_3project.dao.UserDAO;

public class CustomerProfilePanel extends JPanel {

    private int customerId;
    private JTextField txtFirstName, txtLastName, txtDob, txtPhone, txtEmail, txtAddress;
    private JPasswordField txtPassword;
    private UserDAO userDAO;

    // --- BẢNG MÀU UI/UX HIỆN ĐẠI ---
    private static final Color BG_CONTENT = new Color(248, 250, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color TEXT_MAIN = new Color(15, 23, 42);
    private static final Color TEXT_SUB = new Color(100, 116, 139);
    private static final Color ACCENT = new Color(56, 189, 248);

    public CustomerProfilePanel(int customerId) {
        this.customerId = customerId;
        this.userDAO = new UserDAO();
        setLayout(new BorderLayout());
        setBackground(BG_CONTENT);
        setBorder(new EmptyBorder(20, 50, 20, 50));

        initComponents();
        loadProfileData();
    }

    private void initComponents() {
        RoundedPanel card = new RoundedPanel(25, CARD_BG);
        card.setLayout(new BorderLayout(0, 20));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setPreferredSize(new Dimension(800, 550));

        // -- HEADER (TIÊU ĐỀ) --
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Hồ Sơ Của Tôi", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_MAIN);

        JLabel lblSub = new JLabel("Quản lý thông tin liên hệ và tài khoản", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(TEXT_SUB);

        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        card.add(headerPanel, BorderLayout.NORTH);

        // -- CENTER (FORM ĐIỀN THÔNG TIN) --
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        // Row 0: Họ & Tên
        gbc.gridy = 0;
        gbc.gridx = 0;
        formPanel.add(createLabel("Họ:"), gbc);
        gbc.gridx = 1;
        formPanel.add(createLabel("Tên:"), gbc);
        
        gbc.gridy = 1;
        gbc.gridx = 0;
        txtFirstName = createTextField();
        formPanel.add(txtFirstName, gbc);
        gbc.gridx = 1;
        txtLastName = createTextField();
        formPanel.add(txtLastName, gbc);

        // Row 2: Email & Số điện thoại
        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(createLabel("Số điện thoại:"), gbc);
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        txtEmail = createTextField();
        formPanel.add(txtEmail, gbc);
        gbc.gridx = 1;
        txtPhone = createTextField();
        formPanel.add(txtPhone, gbc);

        // Row 4: Ngày sinh & Mật khẩu
        gbc.gridy = 4;
        gbc.gridx = 0;
        formPanel.add(createLabel("Ngày sinh (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(createLabel("Mật khẩu:"), gbc);
        
        gbc.gridy = 5;
        gbc.gridx = 0;
        txtDob = createTextField();
        formPanel.add(txtDob, gbc);
        gbc.gridx = 1;
        txtPassword = createPasswordField();
        formPanel.add(txtPassword, gbc);

        // Row 6: Địa chỉ (Full width)
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        formPanel.add(createLabel("Địa chỉ giao hàng:"), gbc);
        
        gbc.gridy = 7;
        txtAddress = createTextField();
        formPanel.add(txtAddress, gbc);

        // JScrollPane just in case it gets too long
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        card.add(scrollPane, BorderLayout.CENTER);

        // -- FOOTER (NÚT LƯU) --
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnSave = createButton("Lưu Thay Đổi");
        btnSave.addActionListener(e -> saveProfile());
        footerPanel.add(btnSave);

        card.add(footerPanel, BorderLayout.SOUTH);

        // Bọc Card vào giữa màn hình
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);

        add(wrapper, BorderLayout.CENTER);
    }

    // --- CÁC HÀM TIỆN ÍCH VẼ GIAO DIỆN ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_MAIN);
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(0, 42));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(0, 15, 0, 15)));
        return txt;
    }

    private JPasswordField createPasswordField() {
        JPasswordField txt = new JPasswordField();
        txt.setPreferredSize(new Dimension(0, 42));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(0, 15, 0, 15)));
        return txt;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(ACCENT.darker());
                } else {
                    g2.setColor(ACCENT);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 45));
        return btn;
    }

    // --- CÁC HÀM LOGIC KẾT NỐI DATABASE ---
    private void loadProfileData() {
        try {
            Map<String, String> profile = userDAO.getCustomerProfile(customerId);
            if (profile != null && !profile.isEmpty()) {
                txtFirstName.setText(profile.getOrDefault("firstName", ""));
                txtLastName.setText(profile.getOrDefault("lastName", ""));
                txtEmail.setText(profile.getOrDefault("email", ""));
                txtPhone.setText(profile.getOrDefault("phone", ""));
                txtDob.setText(profile.getOrDefault("dob", ""));
                txtPassword.setText(profile.getOrDefault("password", ""));
                txtAddress.setText(profile.getOrDefault("address", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveProfile() {
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, String> profile = new HashMap<>();
        profile.put("firstName", txtFirstName.getText().trim());
        profile.put("lastName", txtLastName.getText().trim());
        profile.put("email", email);
        profile.put("phone", txtPhone.getText().trim());
        profile.put("dob", txtDob.getText().trim());
        profile.put("password", new String(txtPassword.getPassword()));
        profile.put("address", txtAddress.getText().trim());

        try {
            boolean success = userDAO.updateCustomerProfile(customerId, profile);
            if (success) {
                JOptionPane.showMessageDialog(this, "Đã lưu thành công hồ sơ của bạn!");
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi lưu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu thông tin: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Lớp vẽ thẻ bo góc mềm mại đổ bóng
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

            // Vẽ đổ bóng
            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);

            // Vẽ nền
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, radius, radius);

            // Vẽ viền
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}