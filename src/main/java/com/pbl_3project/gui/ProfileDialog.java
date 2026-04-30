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

        int row = 1;
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
}
