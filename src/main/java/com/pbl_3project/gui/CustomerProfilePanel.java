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
    private JLabel lblPoints;
    private JLabel lblAvatar;
    private String currentAvatarUrl = "";
    private UserDAO userDAO;
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
        JPanel topHeaderPanel = new JPanel(new BorderLayout());
        topHeaderPanel.setOpaque(false);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Hồ Sơ Của Tôi", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_MAIN);
        JLabel lblSub = new JLabel("Quản lý thông tin liên hệ và tài khoản", SwingConstants.LEFT);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(TEXT_SUB);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        topHeaderPanel.add(titlePanel, BorderLayout.WEST);

        JPanel pointsPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        pointsPanel.setOpaque(false);
        lblPoints = new JLabel("Điểm tích lũy: 0");
        lblPoints.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPoints.setForeground(new Color(239, 68, 68));
        lblPoints.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JButton btnExchange = createButton("Đổi Voucher");
        btnExchange.setPreferredSize(new Dimension(150, 35));
        btnExchange.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExchange.addActionListener(e -> handleExchangeVoucher());
        
        pointsPanel.add(lblPoints);
        pointsPanel.add(btnExchange);
        
        topHeaderPanel.add(pointsPanel, BorderLayout.EAST);
        card.add(topHeaderPanel, BorderLayout.NORTH);

        JPanel avatarContainer = new JPanel(new BorderLayout());
        avatarContainer.setOpaque(false);
        
        JPanel avatarPanel = new JPanel();
        avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.Y_AXIS));
        avatarPanel.setOpaque(false);
        
        lblAvatar = new JLabel();
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAvatar.setPreferredSize(new Dimension(100, 100));
        lblAvatar.setMinimumSize(new Dimension(100, 100));
        lblAvatar.setMaximumSize(new Dimension(100, 100));
        lblAvatar.setBorder(new RoundedBorder(50));
        updateAvatarImage(null);
        
        JButton btnUploadAvatar = createButton("Đổi ảnh");
        btnUploadAvatar.setPreferredSize(new Dimension(100, 30));
        btnUploadAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnUploadAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUploadAvatar.addActionListener(e -> handleUploadAvatar());
        
        avatarPanel.add(lblAvatar);
        avatarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        avatarPanel.add(btnUploadAvatar);
        avatarContainer.add(avatarPanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
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
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        formPanel.add(createLabel("Địa chỉ giao hàng:"), gbc);
        gbc.gridy = 7;
        txtAddress = createTextField();
        formPanel.add(txtAddress, gbc);
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(avatarContainer, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        card.add(centerPanel, BorderLayout.CENTER);
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton btnSave = createButton("Lưu Thay Đổi");
        btnSave.addActionListener(e -> saveProfile());
        footerPanel.add(btnSave);
        card.add(footerPanel, BorderLayout.SOUTH);
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

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
                currentAvatarUrl = profile.getOrDefault("avatar_url", "");
                updateAvatarImage(currentAvatarUrl);
                if (lblPoints != null) {
                    lblPoints.setText("Điểm tích lũy: " + profile.getOrDefault("reward_points", "0"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu thông tin: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleExchangeVoucher() {
        int currentPoints = 0;
        try {
            Map<String, String> profile = userDAO.getCustomerProfile(customerId);
            if (profile != null && profile.containsKey("reward_points")) {
                currentPoints = Integer.parseInt(profile.get("reward_points"));
            }
        } catch (Exception e) {}
        
        String name = txtFirstName.getText() + " " + txtLastName.getText();
        VoucherExchangeDialog dialog = new VoucherExchangeDialog((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this), customerId, name, currentPoints);
        dialog.setVisible(true);
        loadProfileData();
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
            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, radius, radius);
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private void handleUploadAvatar() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try {
                String ext = file.getName().substring(file.getName().lastIndexOf("."));
                String newFileName = "avatar_cus_" + customerId + "_" + System.currentTimeMillis() + ext;
                java.nio.file.Path targetPath = java.nio.file.Paths.get("src", "main", "resources", "images", "avatars", newFileName);
                java.nio.file.Files.copy(file.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                String dbUrl = "images/avatars/" + newFileName;
                if (userDAO.updateAvatar(customerId, dbUrl)) {
                    currentAvatarUrl = dbUrl;
                    updateAvatarImage(currentAvatarUrl);
                    JOptionPane.showMessageDialog(this, "Cập nhật ảnh đại diện thành công!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi tải ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateAvatarImage(String avatarUrl) {
        try {
            java.awt.image.BufferedImage img = null;
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                java.io.File f = new java.io.File("src/main/resources/" + avatarUrl);
                if (f.exists()) img = javax.imageio.ImageIO.read(f);
            }
            if (img == null) {
                java.io.File defaultF = new java.io.File("src/main/resources/icons/icon_avatar.png");
                if (defaultF.exists()) img = javax.imageio.ImageIO.read(defaultF);
            }
            
            if (img != null) {
                Image scaled = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                lblAvatar.setIcon(new ImageIcon(getCircularImage(scaled, 100)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image getCircularImage(Image img, int size) {
        java.awt.image.BufferedImage circleBuffer = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circleBuffer.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillOval(0, 0, size, size);
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawImage(img, 0, 0, size, size, null);
        g2.dispose();
        return circleBuffer;
    }

    class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        RoundedBorder(int radius) {
            this.radius = radius;
        }
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }
        public boolean isBorderOpaque() {
            return true;
        }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BORDER_COLOR);
            g2.drawOval(x, y, width-1, height-1);
            g2.dispose();
        }
    }
}
