package com.pbl_3project.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    private final Color PRIMARY = new Color(59, 130, 246);
    private final Color PRIMARY_DARK = new Color(37, 99, 235);
    private final Color ACCENT = new Color(34, 197, 94);
    private final Color TEXT_H = new Color(30, 41, 59);
    private final Color TEXT_S = new Color(100, 116, 139);
    private final Color BG = Color.WHITE;
    private final Color INPUT_BG = new Color(248, 250, 252);
    private final Color BORDER_COLOR = new Color(226, 232, 240);

    public CustomerInfoDialog(Frame parent) {
        super(parent, "Thông tin nhận hàng", true);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(550, 750);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(BG);
        JPanel pnlHeader = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), 0, new Color(96, 165, 250));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        pnlHeader.setPreferredSize(new Dimension(0, 90));
        pnlHeader.setBorder(new EmptyBorder(0, 30, 0, 30));

        JLabel lblTitle = new JLabel("THÔNG TIN NHẬN HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BG);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);

        int gridy = 0;
        JLabel lblSub = new JLabel("Vui lòng cung cấp thông tin để chúng tôi giao hàng sớm nhất");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(TEXT_S);
        gbc.gridy = gridy++;
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(lblSub, gbc);
        gbc.insets = new Insets(0, 0, 10, 0);

        gbc.gridy = gridy++;
        mainPanel.add(createLabel("Số điện thoại liên hệ *"), gbc);

        JPanel phonePanel = new JPanel(new BorderLayout(10, 0));
        phonePanel.setOpaque(false);
        txtPhone = createStyledField();
        btnSearch = createStyledButton("Tìm thành viên", PRIMARY);
        btnSearch.setPreferredSize(new Dimension(150, 45));
        btnSearch.addActionListener(e -> searchCustomer());
        phonePanel.add(txtPhone, BorderLayout.CENTER);
        phonePanel.add(btnSearch, BorderLayout.EAST);

        gbc.gridy = gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(phonePanel, gbc);
        gbc.insets = new Insets(0, 0, 10, 0);

        gbc.gridy = gridy++;
        mainPanel.add(createLabel("Họ tên người nhận"), gbc);
        txtName = createStyledField();
        gbc.gridy = gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(txtName, gbc);
        gbc.insets = new Insets(0, 0, 10, 0);

        chkCreateAccount = new JCheckBox("Đăng ký thành viên để nhận ưu đãi?");
        chkCreateAccount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkCreateAccount.setForeground(PRIMARY);
        chkCreateAccount.setOpaque(false);
        chkCreateAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkCreateAccount.addActionListener(e -> txtEmail.setEnabled(chkCreateAccount.isSelected()));
        gbc.gridy = gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(chkCreateAccount, gbc);
        gbc.insets = new Insets(0, 0, 10, 0);

        gbc.gridy = gridy++;
        mainPanel.add(createLabel("Email xác nhận đơn hàng"), gbc);
        txtEmail = createStyledField();
        txtEmail.setEnabled(false);
        gbc.gridy = gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(txtEmail, gbc);
        gbc.insets = new Insets(0, 0, 10, 0);

        gbc.gridy = gridy++;
        mainPanel.add(createLabel("Địa chỉ giao hàng chi tiết *"), gbc);
        txtAddress = createStyledField();
        gbc.gridy = gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(txtAddress, gbc);

        gbc.gridy = gridy++;
        gbc.weighty = 1.0;
        mainPanel.add(Box.createVerticalGlue(), gbc);

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        footer.setBackground(new Color(248, 250, 252));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        btnCancel = createStyledButton("Hủy bỏ", new Color(148, 163, 184));
        btnCancel.setPreferredSize(new Dimension(120, 45));

        btnConfirm = createStyledButton("Xác nhận đặt hàng", ACCENT);
        btnConfirm.setPreferredSize(new Dimension(200, 45));

        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        btnConfirm.addActionListener(e -> handleConfirm());

        footer.add(btnCancel);
        footer.add(btnConfirm);
        add(footer, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_H);
        lbl.setBorder(new EmptyBorder(0, 2, 5, 0));
        return lbl;
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(0, 45));
        field.setBackground(INPUT_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(0, 15, 0, 15)));
        return field;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed())
                    g2.setColor(bg.darker());
                else if (getModel().isRollover())
                    g2.setColor(bg.brighter());
                else
                    g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
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
        return btn;
    }

    private void searchCustomer() {
        String phone = txtPhone.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!");
            return;
        }
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
                JOptionPane.showMessageDialog(this, "✨ Chào mừng hội viên " + customer.get("name") + " quay trở lại!");
            } else {
                JOptionPane.showMessageDialog(this, "Số điện thoại chưa có trong hệ thống.");
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
        if (customerPhone.isEmpty() || customerName.isEmpty() || customerAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ các trường có dấu *");
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
                JOptionPane.showMessageDialog(this, "🎉 Chúc mừng bạn đã trở thành thành viên!");
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