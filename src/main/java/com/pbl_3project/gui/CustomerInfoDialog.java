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

    public CustomerInfoDialog(Frame parent) {
        super(parent, "Thông tin nhận hàng", true);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(480, 650);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(BG);

        // Header with Icon and Title
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
        pnlHeader.setPreferredSize(new Dimension(0, 80));
        pnlHeader.setBorder(new EmptyBorder(0, 30, 0, 30));
        
        JLabel lblTitle = new JLabel("🛒 THÔNG TIN NHẬN HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(BG);

        JLabel lblSub = new JLabel("Vui lòng cung cấp thông tin để chúng tôi giao hàng sớm nhất");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_S);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblSub);
        mainPanel.add(Box.createVerticalStrut(25));

        // Phone Search Row
        mainPanel.add(createLabel("Số điện thoại liên hệ *"));
        JPanel phonePanel = new JPanel(new BorderLayout(12, 0));
        phonePanel.setOpaque(false);
        phonePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPhone = createStyledField();
        btnSearch = createStyledButton("Tìm thành viên", PRIMARY);
        btnSearch.setPreferredSize(new Dimension(130, 42));
        btnSearch.addActionListener(e -> searchCustomer());
        phonePanel.add(txtPhone, BorderLayout.CENTER);
        phonePanel.add(btnSearch, BorderLayout.EAST);
        mainPanel.add(phonePanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Name
        mainPanel.add(createLabel("Họ tên người nhận"));
        txtName = createStyledField();
        mainPanel.add(txtName);
        mainPanel.add(Box.createVerticalStrut(20));

        // Create Account Checkbox
        chkCreateAccount = new JCheckBox("Đăng ký thành viên để nhận ưu đãi?");
        chkCreateAccount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        chkCreateAccount.setForeground(PRIMARY);
        chkCreateAccount.setOpaque(false);
        chkCreateAccount.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkCreateAccount.addActionListener(e -> txtEmail.setEnabled(chkCreateAccount.isSelected()));
        mainPanel.add(chkCreateAccount);
        mainPanel.add(Box.createVerticalStrut(10));

        // Email
        mainPanel.add(createLabel("Email xác nhận đơn hàng"));
        txtEmail = createStyledField();
        txtEmail.setEnabled(false);
        mainPanel.add(txtEmail);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Address
        mainPanel.add(createLabel("Địa chỉ giao hàng chi tiết *"));
        txtAddress = createStyledField();
        mainPanel.add(txtAddress);

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Buttons Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footer.setBackground(new Color(248, 250, 252));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        
        btnCancel = createStyledButton("Hủy bỏ", new Color(148, 163, 184));
        btnConfirm = createStyledButton("Xác nhận đặt hàng", ACCENT);
        btnConfirm.setPreferredSize(new Dimension(200, 48));

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
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_H);
        lbl.setBorder(new EmptyBorder(0, 2, 5, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setBackground(INPUT_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(0, 15, 0, 15)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(bg.darker());
                else if (getModel().isRollover()) g2.setColor(bg.brighter());
                else g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 42));
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
