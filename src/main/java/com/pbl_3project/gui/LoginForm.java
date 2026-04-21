package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.pbl_3project.bus.CartMonitor;
import com.pbl_3project.dao.UserDAO;
import com.pbl_3project.util.TimeDisplayPanel;

public class LoginForm extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginForm() {
        setTitle("Hệ thống Quản lí Cửa hàng Giày dép");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        TimeDisplayPanel timePanel = new TimeDisplayPanel();
        add(timePanel, BorderLayout.NORTH);

        Color themeColor = new Color(59, 190, 210);
        Color accentColor = new Color(255, 127, 102);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBackground(themeColor);

        JLabel lblShopName = new JLabel("SHOP GIÀY DÉP T&T", SwingConstants.CENTER);
        lblShopName.setFont(new Font("Cambria", Font.BOLD, 24));
        lblShopName.setForeground(Color.BLACK);
        lblShopName.setBorder(new EmptyBorder(30, 0, 10, 0));
        leftPanel.add(lblShopName, BorderLayout.NORTH);

        Image tempImage = null;
        try {
            java.net.URL imgURL = getClass().getResource("/images/Shoe Shop.png");
            if (imgURL != null) {
                tempImage = new ImageIcon(imgURL).getImage();
            } else {
                String absolutePath = "F:\\CNTT\\shoe_shop_management\\src\\main\\resources\\images\\Shoe Shop.png";
                tempImage = new ImageIcon(absolutePath).getImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Image originalImage = tempImage;

        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (originalImage != null) {
                    int panelW = getWidth();
                    int panelH = getHeight();
                    int imgW = originalImage.getWidth(this);
                    int imgH = originalImage.getHeight(this);
                    double scale = Math.min((double) panelW / imgW, (double) panelH / imgH);
                    int drawW = (int) (imgW * scale);
                    int drawH = (int) (imgH * scale);
                    int x = (panelW - drawW) / 2;
                    int y = (panelH - drawH) / 2;
                    g.drawImage(originalImage, x, y, drawW, drawH, this);
                } else {
                    g.setColor(Color.WHITE);
                    g.drawString("Không thể tải hình ảnh", getWidth() / 2 - 60, getHeight() / 2);
                }
            }
        };

        imagePanel.setOpaque(false);
        leftPanel.add(imagePanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(themeColor);

        JPanel loginBox = new JPanel();
        loginBox.setBackground(Color.WHITE);
        loginBox.setLayout(new GridBagLayout());

        loginBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Cambria", Font.BOLD, 22));
        lblTitle.setForeground(accentColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginBox.add(lblTitle, gbc);

        gbc.gridwidth = 1;

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Cambria", Font.BOLD, 13));
        lblEmail.setForeground(new Color(80, 80, 80));
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginBox.add(lblEmail, gbc);

        txtEmail = new JTextField(20);
        txtEmail.setPreferredSize(new Dimension(220, 38));
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 12));
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginBox.add(txtEmail, gbc);

        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Cambria", Font.BOLD, 13));
        lblPassword.setForeground(new Color(80, 80, 80));
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        loginBox.add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setPreferredSize(new Dimension(220, 38));
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        loginBox.add(txtPassword, gbc);

        btnLogin = new JButton("[ĐĂNG NHẬP]");
        btnLogin.setPreferredSize(new Dimension(220, 42));
        btnLogin.setBackground(accentColor);
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFont(new Font("Cambria", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(BorderFactory.createEmptyBorder());
        btnLogin.setOpaque(true);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 10, 5, 10);
        loginBox.add(btnLogin, gbc);

        rightPanel.add(loginBox);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);

        btnLogin.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Email và Mật khẩu!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            int loginResult = userDAO.checkLogin(email, password);

            if (loginResult == -1) {
                JOptionPane.showMessageDialog(this, "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Admin!",
                        "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);

            } else if (loginResult > 0) {
                int loggedInId = userDAO.getUserIdByEmail(email);

                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                this.dispose();

                if (loginResult == 1 || loginResult == 2) {
                    new AdminForm().setVisible(true);
                } else if (loginResult == 3) {
                    new EmployeeForm(loggedInId).setVisible(true);
                } else if (loginResult == 4) {
                    // ĐÃ FIX: TRUYỀN ID KHÁCH HÀNG VÀO LUÔN
                    new CustomerForm(loggedInId).setVisible(true);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Email hoặc mật khẩu không chính xác!", "Đăng nhập thất bại",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        CartMonitor.startMonitoring();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}