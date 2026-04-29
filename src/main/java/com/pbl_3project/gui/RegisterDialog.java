package com.pbl_3project.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import com.pbl_3project.dao.UserDAO;

public class RegisterDialog extends JDialog {
    private JTextField txtName, txtEmail, txtPhone;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnRegister, btnCancel;
    private UserDAO userDAO = new UserDAO();

    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private Color primaryColor = new Color(59, 130, 246);
    private Color hoverColor = new Color(37, 99, 235);

    public RegisterDialog(Frame parent) {
        super(parent, "Đăng ký tài khoản", true);
        setSize(450, 620);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(primaryColor);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        int row = 1;

        addLabelAndField(mainPanel, "Họ và Tên *:", txtName = new JTextField(), gbc, row);
        row += 2;
        addLabelAndField(mainPanel, "Email *:", txtEmail = new JTextField(), gbc, row);
        row += 2;
        addLabelAndField(mainPanel, "Số điện thoại:", txtPhone = new JTextField(), gbc, row);
        row += 2;
        addLabelAndField(mainPanel, "Mật khẩu *:", txtPassword = new JPasswordField(), gbc, row);
        row += 2;
        addLabelAndField(mainPanel, "Xác nhận mật khẩu *:", txtConfirmPassword = new JPasswordField(), gbc, row);
        row += 2;

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlButtons.setBackground(Color.WHITE);
        pnlButtons.setBorder(new EmptyBorder(15, 0, 0, 0));

        btnCancel = createStyledButton("Hủy", new Color(243, 244, 246), new Color(55, 65, 81), new Color(229, 231, 235));
        btnRegister = createStyledButton("Đăng ký", primaryColor, Color.WHITE, hoverColor);
        
        pnlButtons.add(btnCancel);
        pnlButtons.add(btnRegister);

        gbc.gridy = row;
        gbc.insets = new Insets(20, 0, 10, 0);
        mainPanel.add(pnlButtons, gbc);

        JScrollPane scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        btnRegister.addActionListener(e -> handleRegister());
        btnCancel.addActionListener(e -> dispose());
    }

    private void addLabelAndField(JPanel pnl, String labelText, JTextField field, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
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
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Add focus effect
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(primaryColor, 2, true),
                    BorderFactory.createEmptyBorder(4, 9, 4, 9)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(209, 213, 219), 1, true),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
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

    private void handleRegister() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        String confirmPass = new String(txtConfirmPassword.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền các trường bắt buộc (*)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!pass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (userDAO.checkEmailExists(email)) {
                JOptionPane.showMessageDialog(this, "Email này đã được sử dụng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Role 4 = Customer
            if (userDAO.addUser(name, email, pass, phone, 4)) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Bạn có thể đăng nhập ngay.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
