package com.pbl_3project.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;
import com.pbl_3project.dao.UserDAO;

public class CustomerInfoDialog extends JDialog {
    private JTextField txtPhone, txtName, txtEmail, txtAddress;
    private JCheckBox chkCreateAccount;
    private JButton btnSearch, btnConfirm, btnCancel;
    private boolean confirmed = false;
    private int customerId = -1;
    private String customerPhone = "";
    private String customerName = "Khách vãng lai";
    private String customerAddress = "";
    private UserDAO userDAO = new UserDAO();

    public CustomerInfoDialog(Frame parent) {
        super(parent, "Thông tin khách hàng", true);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(400, 500);
        setLocationRelativeTo(getParent());
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Số điện thoại:"), gbc);
        JPanel phonePanel = new JPanel(new BorderLayout(5, 0));
        phonePanel.setOpaque(false);
        txtPhone = new JTextField();
        txtPhone.setPreferredSize(new Dimension(150, 35));
        btnSearch = new JButton("🔍 Tìm");
        btnSearch.addActionListener(e -> searchCustomer());
        phonePanel.add(txtPhone, BorderLayout.CENTER);
        phonePanel.add(btnSearch, BorderLayout.EAST);
        gbc.gridx = 1;
        mainPanel.add(phonePanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Họ tên:"), gbc);
        txtName = new JTextField();
        txtName.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        mainPanel.add(txtName, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        chkCreateAccount = new JCheckBox("Tạo tài khoản thành viên?");
        chkCreateAccount.setOpaque(false);
        chkCreateAccount.addActionListener(e -> txtEmail.setEnabled(chkCreateAccount.isSelected()));
        mainPanel.add(chkCreateAccount, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField();
        txtEmail.setEnabled(false);
        txtEmail.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        mainPanel.add(txtEmail, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Địa chỉ:"), gbc);
        txtAddress = new JTextField();
        txtAddress.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        mainPanel.add(txtAddress, gbc);

        add(mainPanel, BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(245, 245, 245));
        btnCancel = new JButton("Bỏ qua (Khách vãng lai)");
        btnConfirm = new JButton("Xác nhận");
        btnConfirm.setBackground(new Color(52, 152, 219));
        btnConfirm.setForeground(Color.BLACK);
        btnCancel.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        btnConfirm.addActionListener(e -> handleConfirm());
        btnPanel.add(btnCancel);
        btnPanel.add(btnConfirm);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void searchCustomer() {
        String phone = txtPhone.getText().trim();
        if (phone.isEmpty())
            return;
        try {
            Map<String, String> customer = userDAO.findCustomerByPhone(phone);
            if (customer != null) {
                customerId = Integer.parseInt(customer.get("id"));
                txtName.setText(customer.get("name"));
                txtEmail.setText(customer.get("email"));
                txtAddress.setText(customer.get("address"));
                txtEmail.setEnabled(false);
                chkCreateAccount.setSelected(true);
                chkCreateAccount.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Đã tìm thấy khách hàng thành viên!");
            } else {
                JOptionPane.showMessageDialog(this, "Khách hàng mới.");
                txtName.setText("");
                txtEmail.setText("");
                txtAddress.setText("");
                txtEmail.setEnabled(chkCreateAccount.isSelected());
                chkCreateAccount.setEnabled(true);
                customerId = -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleConfirm() {
        customerPhone = txtPhone.getText().trim();
        customerName = txtName.getText().trim();
        customerAddress = txtAddress.getText().trim();
        if (customerPhone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!");
            return;
        }
        if (chkCreateAccount.isSelected() && customerId == -1) {
            String email = txtEmail.getText().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Email để tạo tài khoản!");
                return;
            }
            try {
                customerId = userDAO.createQuickCustomer(customerName, customerPhone, email, "123456", customerAddress);
                JOptionPane.showMessageDialog(this, "Đã tạo tài khoản thành viên thành công!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi tạo tài khoản: " + e.getMessage());
                return;
            }
        }
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }
}
