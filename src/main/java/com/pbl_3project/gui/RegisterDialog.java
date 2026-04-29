package com.pbl_3project.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import com.pbl_3project.dao.UserDAO;

public class RegisterDialog extends JDialog {
    private JTextField txtName, txtEmail, txtPhone;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnRegister, btnCancel;
    private UserDAO userDAO = new UserDAO();

    public RegisterDialog(Frame parent) {
        super(parent, "Đăng ký tài khoản", true);
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel("ĐĂNG KÝ HỆ THỐNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(59, 130, 246));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        int row = 1;

        addLabelAndField(mainPanel, "Họ và Tên:", txtName = new JTextField(), gbc, row++);
        addLabelAndField(mainPanel, "Email:", txtEmail = new JTextField(), gbc, row++);
        addLabelAndField(mainPanel, "Số điện thoại:", txtPhone = new JTextField(), gbc, row++);
        addLabelAndField(mainPanel, "Mật khẩu:", txtPassword = new JPasswordField(), gbc, row++);
        addLabelAndField(mainPanel, "Xác nhận mật khẩu:", txtConfirmPassword = new JPasswordField(), gbc, row++);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.setBackground(Color.WHITE);
        btnRegister = new JButton("Đăng ký");
        btnCancel = new JButton("Hủy");
        
        btnRegister.setBackground(new Color(59, 130, 246));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        pnlButtons.add(btnCancel);
        pnlButtons.add(btnRegister);

        gbc.gridy = row;
        mainPanel.add(pnlButtons, gbc);

        add(mainPanel, BorderLayout.CENTER);

        btnRegister.addActionListener(e -> handleRegister());
        btnCancel.addActionListener(e -> dispose());
    }

    private void addLabelAndField(JPanel pnl, String label, JTextField field, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        pnl.add(new JLabel(label), gbc);
        gbc.gridy = row + 1;
        field.setPreferredSize(new Dimension(300, 35));
        pnl.add(field, gbc);
    }

    private void handleRegister() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        String confirmPass = new String(txtConfirmPassword.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền các trường bắt buộc (*)");
            return;
        }

        if (!pass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
            return;
        }

        try {
            if (userDAO.checkEmailExists(email)) {
                JOptionPane.showMessageDialog(this, "Email này đã được sử dụng!");
                return;
            }
            // Role 4 = Customer
            if (userDAO.addUser(name, email, pass, phone, 4)) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Bạn có thể đăng nhập ngay.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký thất bại. Vui lòng thử lại.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}
