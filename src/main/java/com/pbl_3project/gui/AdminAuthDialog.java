package com.pbl_3project.gui;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import com.pbl_3project.dao.UserDAO;

public class AdminAuthDialog extends JDialog {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private boolean authenticated = false;

    public AdminAuthDialog(Window owner) {
        super(owner, "Yêu cầu Xác nhận từ Quản lý", ModalityType.APPLICATION_MODAL);
        initComponents();
        setSize(400, 250);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBackground(new Color(248, 250, 252));
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Cần quyền Admin để thực hiện", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(239, 68, 68));
        contentPane.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblEmail = new JLabel("Email Admin:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblEmail, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtEmail = new JTextField();
        txtEmail.setPreferredSize(new Dimension(200, 30));
        txtEmail.setBorder(new LineBorder(new Color(203, 213, 225)));
        formPanel.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblPassword, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(200, 30));
        txtPassword.setBorder(new LineBorder(new Color(203, 213, 225)));
        formPanel.add(txtPassword, gbc);

        contentPane.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton btnConfirm = new JButton("Xác nhận");
        btnConfirm.setBackground(new Color(34, 197, 94));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JButton btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(226, 232, 240));
        btnCancel.setFocusPainted(false);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnPanel.add(btnCancel);
        btnPanel.add(btnConfirm);
        contentPane.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);

        btnConfirm.addActionListener(e -> authenticate());
        btnCancel.addActionListener(e -> dispose());
    }

    private void authenticate() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Email và Mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            int role = userDAO.checkLogin(email, password);
            if (role == 1 || role == 2) {
                authenticated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Thông tin xác thực không đúng hoặc tài khoản không có quyền Admin!", "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
