package com.pbl_3project.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import com.pbl_3project.dao.UserDAO;

public class ProfileDialog extends JDialog {
    private int userId;
    private int roleId;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JPasswordField txtPassword;
    private JLabel lblAvatar;
    private String currentAvatarUrl = "";
    private UserDAO userDAO = new UserDAO();

    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private Color primaryColor = new Color(59, 130, 246);
    private Color hoverColor = new Color(37, 99, 235);

    public ProfileDialog(Window owner, int userId, int roleId) {
        super(owner, "Hồ sơ cá nhân", ModalityType.APPLICATION_MODAL);
        this.userId = userId;
        this.roleId = roleId;
        setSize(450, 550);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        initComponents();
        loadProfile();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        mainPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel("THÔNG TIN CÁ NHÂN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(primaryColor);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(lblTitle, gbc);

        JPanel avatarPanel = new JPanel();
        avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.Y_AXIS));
        avatarPanel.setBackground(Color.WHITE);
        
        lblAvatar = new JLabel();
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAvatar.setPreferredSize(new Dimension(100, 100));
        lblAvatar.setMinimumSize(new Dimension(100, 100));
        lblAvatar.setMaximumSize(new Dimension(100, 100));
        lblAvatar.setBorder(new RoundedBorder(50));
        updateAvatarImage(null);
        
        JButton btnUploadAvatar = createStyledButton("Đổi ảnh", new Color(243, 244, 246), new Color(55, 65, 81), new Color(229, 231, 235));
        btnUploadAvatar.setPreferredSize(new Dimension(100, 30));
        btnUploadAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUploadAvatar.addActionListener(e -> handleUploadAvatar());
        
        avatarPanel.add(lblAvatar);
        avatarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        avatarPanel.add(btnUploadAvatar);
        
        gbc.gridy = 1;
        mainPanel.add(avatarPanel, gbc);

        int row = 2;
        addLabelAndField(mainPanel, "Họ và Tên *:", txtFullName = new JTextField(), gbc, row);
        row += 2;
        addLabelAndField(mainPanel, "Email *:", txtEmail = new JTextField(), gbc, row);
        row += 2;
        addLabelAndField(mainPanel, "Số điện thoại:", txtPhone = new JTextField(), gbc, row);
        row += 2;
        addLabelAndField(mainPanel, "Mật khẩu *:", txtPassword = new JPasswordField(), gbc, row);
        row += 2;

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlButtons.setBackground(Color.WHITE);
        pnlButtons.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnCancel = createStyledButton("Đóng", new Color(243, 244, 246), new Color(55, 65, 81),
                new Color(229, 231, 235));
        JButton btnSave = createStyledButton("Lưu thay đổi", primaryColor, Color.WHITE, hoverColor);

        pnlButtons.add(btnCancel);
        pnlButtons.add(btnSave);

        gbc.gridy = row;
        gbc.insets = new Insets(20, 0, 10, 0);
        mainPanel.add(pnlButtons, gbc);

        JScrollPane scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveProfile());
    }

    private void addLabelAndField(JPanel pnl, String labelText, JTextField field, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.insets = new Insets(10, 0, 5, 0);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(labelFont);
        lbl.setForeground(new Color(55, 65, 81));
        pnl.add(lbl, gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        field.setFont(mainFont);
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(primaryColor, 2, true),
                        BorderFactory.createEmptyBorder(4, 9, 4, 9)));
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(209, 213, 219), 1, true),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            }
        });
        pnl.add(field, gbc);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor, Color hoverBgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 40));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBgColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    private void loadProfile() {
        try {
            Map<String, String> profile = userDAO.getEmployeeProfile(userId);
            if (!profile.isEmpty()) {
                txtFullName.setText(profile.get("fullName"));
                txtEmail.setText(profile.get("email"));
                txtPhone.setText(profile.get("phone"));
                txtPassword.setText(profile.get("password"));
                currentAvatarUrl = profile.get("avatar_url");
                updateAvatarImage(currentAvatarUrl);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải hồ sơ: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveProfile() {
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (roleId == 3) {
                if (userDAO.addProfileUpdateRequest(userId, fullName, email, phone, password)) {
                    JOptionPane.showMessageDialog(this, "Yêu cầu thay đổi hồ sơ đã được gửi đến Quản lý để xét duyệt!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể gửi yêu cầu. Vui lòng thử lại.", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (userDAO.updateEmployeeProfile(userId, fullName, email, phone, password)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật hồ sơ thành công!", "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật hồ sơ. Vui lòng thử lại.", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void handleUploadAvatar() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try {
                String ext = file.getName().substring(file.getName().lastIndexOf("."));
                String newFileName = "avatar_emp_" + userId + "_" + System.currentTimeMillis() + ext;
                java.nio.file.Path targetPath = java.nio.file.Paths.get("src", "main", "resources", "images", "avatars", newFileName);
                java.nio.file.Files.copy(file.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                String dbUrl = "images/avatars/" + newFileName;
                if (userDAO.updateAvatar(userId, dbUrl)) {
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
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }
        public boolean isBorderOpaque() {
            return true;
        }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(209, 213, 219));
            g2.drawOval(x, y, width-1, height-1);
            g2.dispose();
        }
    }
}
