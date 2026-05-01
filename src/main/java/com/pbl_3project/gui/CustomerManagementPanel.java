package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.SQLException;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import com.pbl_3project.bus.CustomerBUS;

public class CustomerManagementPanel extends JPanel {
    private JTable tableCustomers;
    private JTextField txtSearch;
    private CustomerBUS customerBUS = new CustomerBUS();

    public CustomerManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initComponents();
        loadDataToTable("");
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(41, 53, 65));
        topPanel.add(lblTitle, BorderLayout.NORTH);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(10, 15, 10, 15)));
        JLabel lblSearch = new JLabel("TÌM KIẾM: ");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchPanel.add(lblSearch);
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(200, 36));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(0, 10, 0, 10)));
        JButton btnSearch = createRoundButton("Tìm kiếm", new Color(37, 99, 235));
        JButton btnRefresh = createRoundButton("Làm mới", new Color(100, 116, 139));
        JButton btnBan = createRoundButton("Khóa tài khoản", new Color(220, 38, 38));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(btnBan);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        tableCustomers = new JTable();
        tableCustomers.setRowHeight(32);
        tableCustomers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableCustomers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableCustomers.getTableHeader().setBackground(new Color(59, 190, 210));
        tableCustomers.getTableHeader().setForeground(Color.BLACK);
        tableCustomers.setGridColor(new Color(220, 220, 220));
        tableCustomers.setSelectionBackground(new Color(200, 230, 255));
        tableCustomers.setSelectionForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(tableCustomers);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "Danh sách Khách hàng",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));
        add(scrollPane, BorderLayout.CENTER);
        btnSearch.addActionListener(e -> loadDataToTable(txtSearch.getText()));
        txtSearch.addActionListener(e -> loadDataToTable(txtSearch.getText()));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadDataToTable("");
        });
        btnBan.addActionListener(e -> handleBanCustomer());
    }

    private void loadDataToTable(String keyword) {
        try {
            tableCustomers.setModel(customerBUS.getCustomerTableModel(keyword));
            javax.swing.table.DefaultTableCellRenderer center = new javax.swing.table.DefaultTableCellRenderer();
            center.setHorizontalAlignment(JLabel.CENTER);
            if (tableCustomers.getColumnCount() > 0) {
                tableCustomers.getColumnModel().getColumn(0).setCellRenderer(center);
                tableCustomers.getColumnModel().getColumn(0).setPreferredWidth(50);
                tableCustomers.getColumnModel().getColumn(4).setCellRenderer(center);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void handleBanCustomer() {
        int selectedRow = tableCustomers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng!", "Chưa chọn",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(tableCustomers.getValueAt(selectedRow, 0).toString());
        String name = tableCustomers.getValueAt(selectedRow, 1).toString();
        String currentStatus = tableCustomers.getValueAt(selectedRow, 5).toString();
        if ("Inactive".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Khách hàng này đã bị khóa từ trước!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận khóa tài khoản khách hàng:\n" + name + " (ID: " + id + ")?",
                "Khóa Tài Khoản", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (customerBUS.xoaKhachHang(id)) {
                    JOptionPane.showMessageDialog(this, "Đã khóa thành công!");
                    loadDataToTable(txtSearch.getText());
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối DB: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private JButton createRoundButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(baseColor.darker());
                } else {
                    g2.setColor(baseColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        return btn;
    }
}
