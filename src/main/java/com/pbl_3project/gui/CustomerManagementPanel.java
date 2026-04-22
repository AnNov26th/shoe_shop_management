package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;

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
        loadDataToTable(""); // Load tất cả khi mở
    }

    private void initComponents() {
        // --- 1. TIÊU ĐỀ ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("👥 QUẢN LÝ KHÁCH HÀNG", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(41, 53, 65));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // --- 2. THANH TÌM KIẾM & NÚT KHÓA TÀI KHOẢN ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        searchPanel.setBackground(new Color(250, 250, 250));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.add(new JLabel("TÌM KIẾM: "));

        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnSearch = new JButton("[Tìm]");
        btnSearch.setBackground(new Color(59, 190, 210));
        btnSearch.setForeground(Color.BLACK);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JButton btnRefresh = new JButton("[Tải lại]");
        btnRefresh.setBackground(new Color(100, 150, 100));
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JButton btnBan = new JButton("[Khóa TK]");
        btnBan.setBackground(new Color(255, 80, 80));
        btnBan.setForeground(Color.BLACK);
        btnBan.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnBan.setFocusPainted(false);
        btnBan.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        searchPanel.add(new JLabel("   |   "));
        searchPanel.add(btnBan);

        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- 3. BẢNG DỮ LIỆU KHÁCH HÀNG ---
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
                "📋 Danh sách Khách hàng",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));
        add(scrollPane, BorderLayout.CENTER);

        // --- 4. SỰ KIỆN NÚT BẤM ---
        btnSearch.addActionListener(e -> loadDataToTable(txtSearch.getText()));
        txtSearch.addActionListener(e -> loadDataToTable(txtSearch.getText())); // Enter để tìm

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadDataToTable("");
        });

        // Sự kiện Khóa tài khoản khách hàng (Gian lận bom hàng chẳng hạn)
        btnBan.addActionListener(e -> handleBanCustomer());
    }

    private void loadDataToTable(String keyword) {
        try {
            tableCustomers.setModel(customerBUS.getCustomerTableModel(keyword));

            // Căn giữa ID và Điểm thưởng
            javax.swing.table.DefaultTableCellRenderer center = new javax.swing.table.DefaultTableCellRenderer();
            center.setHorizontalAlignment(JLabel.CENTER);
            if (tableCustomers.getColumnCount() > 0) {
                tableCustomers.getColumnModel().getColumn(0).setCellRenderer(center);
                tableCustomers.getColumnModel().getColumn(0).setPreferredWidth(50);
                tableCustomers.getColumnModel().getColumn(4).setCellRenderer(center); // Cột Điểm thưởng
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
                "⚠️ Xác nhận khóa tài khoản khách hàng:\n" + name + " (ID: " + id + ")?",
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
}