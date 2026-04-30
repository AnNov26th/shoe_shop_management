package com.pbl_3project.gui;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.pbl_3project.dao.UserDAO;

public class EmployeeRequestPanel extends JPanel {
    private JTable requestTable;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);

    public EmployeeRequestPanel() {
        userDAO = new UserDAO();
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);
        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Duyệt Yêu Cầu Cập Nhật Hồ Sơ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setBackground(new Color(226, 232, 240));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadData());
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID Yêu cầu", "Mã NV", "Họ Tên Cũ -> Mới", "Email Mới", "SĐT Mới", "Ngày Tạo"}, 0);
        requestTable = new JTable(tableModel);
        requestTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        requestTable.setRowHeight(35);
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestTable.setSelectionBackground(new Color(224, 242, 254));
        requestTable.setSelectionForeground(new Color(15, 23, 42));
        
        JTableHeader tableHeader = requestTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setBackground(new Color(241, 245, 249));
        tableHeader.setForeground(new Color(15, 23, 42));
        tableHeader.setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);

        JButton btnReject = new JButton("Từ chối");
        btnReject.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReject.setBackground(new Color(239, 68, 68));
        btnReject.setForeground(Color.WHITE);
        btnReject.setFocusPainted(false);
        
        JButton btnApprove = new JButton("Phê duyệt");
        btnApprove.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnApprove.setBackground(new Color(34, 197, 94));
        btnApprove.setForeground(Color.WHITE);
        btnApprove.setFocusPainted(false);

        btnReject.addActionListener(e -> handleAction("Rejected"));
        btnApprove.addActionListener(e -> handleAction("Approved"));

        actionPanel.add(btnReject);
        actionPanel.add(btnApprove);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            DefaultTableModel model = userDAO.getPendingProfileRequests();
            requestTable.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAction(String status) {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một yêu cầu để xử lý!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) requestTable.getValueAt(selectedRow, 0);
        String actionName = status.equals("Approved") ? "phê duyệt" : "từ chối";
        
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn " + actionName + " yêu cầu này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (userDAO.updateProfileRequestStatus(requestId, status)) {
                    JOptionPane.showMessageDialog(this, "Đã " + actionName + " yêu cầu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Thao tác thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
