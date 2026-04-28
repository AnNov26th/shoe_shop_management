package com.pbl_3project.gui;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.pbl_3project.bus.CartMonitor;
import com.pbl_3project.dao.UserDAO;
import com.pbl_3project.util.TimeDisplayPanel;

public class LoginForm extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;

    public LoginForm() {
        setTitle("Hệ thống Quản lý Cửa hàng Giày dép");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        add(new TimeDisplayPanel(), BorderLayout.NORTH);

        Color themeColor = new Color(59, 190, 210);
        Color accentColor = new Color(255, 127, 102);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // --- BÊN TRÁI: BANNER ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(themeColor);

        JLabel lblShopName = new JLabel("SHOP GIÀY DÉP T&T", SwingConstants.CENTER);
        lblShopName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblShopName.setForeground(Color.BLACK);
        lblShopName.setBorder(new EmptyBorder(40, 0, 10, 0));
        leftPanel.add(lblShopName, BorderLayout.NORTH);

        Image tempImage = null;
        try {
            java.net.URL imgURL = getClass().getResource("/images/Shoe Shop.png");
            if (imgURL != null)
                tempImage = new ImageIcon(imgURL).getImage();
            else
                tempImage = new ImageIcon("F:\\CNTT\\shoe_shop_management\\src\\main\\resources\\images\\Shoe Shop.png")
                        .getImage();
        } catch (Exception e) {
        }

        final Image originalImage = tempImage;
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (originalImage != null) {
                    int w = getWidth(), h = getHeight();
                    int iw = originalImage.getWidth(this), ih = originalImage.getHeight(this);
                    double scale = Math.min((double) w / iw, (double) h / ih);
                    int dw = (int) (iw * scale), dh = (int) (ih * scale);
                    g.drawImage(originalImage, (w - dw) / 2, (h - dh) / 2, dw, dh, this);
                }
            }
        };
        imagePanel.setOpaque(false);
        leftPanel.add(imagePanel, BorderLayout.CENTER);

        // --- BÊN PHẢI: FORM ĐĂNG NHẬP BO GÓC ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(248, 250, 252));

        JPanel loginBox = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 10)); // Đổ bóng nhẹ
                g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 25, 25);
                g2.setColor(Color.WHITE); // Nền trắng
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 6, 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginBox.setOpaque(false);
        loginBox.setBorder(new EmptyBorder(35, 45, 35, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(accentColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginBox.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBox.add(lblEmail, gbc);

        txtEmail = new JTextField(20);
        txtEmail.setPreferredSize(new Dimension(240, 42));
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true), new EmptyBorder(0, 12, 0, 12)));
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginBox.add(txtEmail, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBox.add(lblPass, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setPreferredSize(new Dimension(240, 42));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true), new EmptyBorder(0, 12, 0, 12)));
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        loginBox.add(txtPassword, gbc);

        // Nút Đăng nhập dạng Pill
        JButton btnLogin = new JButton("ĐĂNG NHẬP") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = getModel().isRollover()
                        ? new GradientPaint(0, 0, accentColor.darker(), getWidth(), 0, accentColor)
                        : new GradientPaint(0, 0, accentColor, getWidth(), 0, accentColor.brighter());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogin.setPreferredSize(new Dimension(240, 45));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy = 5;
        gbc.insets = new Insets(25, 5, 5, 5);
        loginBox.add(btnLogin, gbc);

        rightPanel.add(loginBox);
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Email và Mật khẩu!");
            return;
        }
        try {
            UserDAO userDAO = new UserDAO();
            int loginResult = userDAO.checkLogin(email, password);

            if (loginResult == -1) {
                JOptionPane.showMessageDialog(this, "Tài khoản của bạn đã bị khóa!");
            } else if (loginResult > 0) {
                int loggedInId = userDAO.getUserIdByEmail(email);
                this.dispose();

                if (loginResult == 1 || loginResult == 2)
                    new AdminForm().setVisible(true);
                else if (loginResult == 3)
                    new EmployeeForm(loggedInId).setVisible(true);
                else if (loginResult == 4)
                    new CustomerForm(loggedInId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Sai email hoặc mật khẩu!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        CartMonitor.startMonitoring();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}