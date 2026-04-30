package com.pbl_3project.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.pbl_3project.bus.EmployeeBUS;
public class EmployeeManagementPanel extends JPanel {
    private static final Color BG_COLOR = new Color(240, 244, 247);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color TEXT_MAIN = new Color(30, 41, 59);
    private static final Color TEXT_SUB = new Color(100, 116, 139);
    private static final Color BTN_PRIMARY = new Color(59, 190, 210);
    private static final Color BTN_SUCCESS = new Color(74, 222, 128);
    private static final Color BTN_DANGER = new Color(248, 113, 113);
    private static final Color BTN_SECONDARY = new Color(203, 213, 225);
    private JTextField txtName, txtEmail, txtPhone, txtSearch;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JTable tableEmployees;
    private JLabel lblNewestEmployee;
    private EmployeeBUS employeeBUS;
    public EmployeeManagementPanel() {
        employeeBUS = new EmployeeBUS();
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 25, 20, 25));
        initComponents();
        loadTableData("");
    }
    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout(25, 0));
        topPanel.setOpaque(false);
        RoundedPanel formCard = new RoundedPanel(25, CARD_BG);
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(new EmptyBorder(20, 25, 20, 25));
        JLabel lblFormTitle = new JLabel("Thêm Nhân Viên Mới");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblFormTitle.setForeground(TEXT_MAIN);
        lblFormTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        formCard.add(lblFormTitle, BorderLayout.NORTH);
        JPanel inputGrid = new JPanel(new GridBagLayout());
        inputGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.gridy = 0;
        gbc.gridx = 0;
        inputGrid.add(createLabel("Mã ID:"), gbc);
        gbc.gridx = 1;
        JTextField txtId = createTextField();
        txtId.setText("*Tự động tăng*");
        txtId.setEnabled(false);
        txtId.setBackground(new Color(241, 245, 249));
        inputGrid.add(txtId, gbc);
        gbc.gridx = 2;
        inputGrid.add(createLabel("Họ và Tên:"), gbc);
        gbc.gridx = 3;
        txtName = createTextField();
        inputGrid.add(txtName, gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        inputGrid.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = createTextField();
        inputGrid.add(txtEmail, gbc);
        gbc.gridx = 2;
        inputGrid.add(createLabel("Số điện thoại:"), gbc);
        gbc.gridx = 3;
        txtPhone = createTextField();
        inputGrid.add(txtPhone, gbc);
        gbc.gridy = 2;
        gbc.gridx = 0;
        inputGrid.add(createLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        inputGrid.add(txtPassword, gbc);
        gbc.gridx = 2;
        inputGrid.add(createLabel("Quyền hạn:"), gbc);
        gbc.gridx = 3;
        cbRole = new JComboBox<>(new String[] { "Admin", "Manager", "Staff" });
        cbRole.setPreferredSize(new Dimension(0, 36));
        cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbRole.setBackground(Color.WHITE);
        inputGrid.add(cbRole, gbc);
        formCard.add(inputGrid, BorderLayout.CENTER);
        JPanel formBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        formBtnPanel.setOpaque(false);
        formBtnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        JButton btnClear = createButton("Xóa Trắng Form", BTN_SECONDARY, TEXT_MAIN);
        JButton btnSave = createButton("Lưu Nhân Viên", BTN_SUCCESS, Color.WHITE);
        formBtnPanel.add(btnClear);
        formBtnPanel.add(btnSave);
        formCard.add(formBtnPanel, BorderLayout.SOUTH);
        JPanel statsContainer = new JPanel();
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setOpaque(false);
        statsContainer.setPreferredSize(new Dimension(280, 0));
        JLabel lblStatsTitle = new JLabel("Thông tin nhanh");
        lblStatsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblStatsTitle.setForeground(TEXT_MAIN);
        lblStatsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsContainer.add(lblStatsTitle);
        statsContainer.add(Box.createVerticalStrut(15));
        statsContainer.add(createStatCard("Manager:", "Nguyễn Bá Giàu"));
        statsContainer.add(Box.createVerticalStrut(12));
        statsContainer.add(createStatCard("Admin:", "Trần Hoài An"));
        statsContainer.add(Box.createVerticalStrut(12));
        lblNewestEmployee = new JLabel("Đang tải...");
        statsContainer.add(createStatCard("Nhân viên mới nhất:", lblNewestEmployee));
        topPanel.add(formCard, BorderLayout.CENTER);
        topPanel.add(statsContainer, BorderLayout.EAST);
        RoundedPanel bottomPanel = new RoundedPanel(25, CARD_BG);
        bottomPanel.setLayout(new BorderLayout(0, 15));
        bottomPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolBar.setOpaque(false);
        toolBar.add(createLabel("Tìm kiếm (Tên/Email/SĐT):"));
        txtSearch = createTextField();
        txtSearch.setPreferredSize(new Dimension(250, 36));
        toolBar.add(txtSearch);
        JButton btnSearch = createButton("Tìm kiếm", BTN_PRIMARY, Color.WHITE);
        JButton btnReload = createButton("Tải lại Bảng", BTN_PRIMARY, Color.WHITE);
        JButton btnDelete = createButton("Xóa Nhân Viên Chọn", BTN_DANGER, Color.WHITE);
        toolBar.add(btnSearch);
        toolBar.add(btnReload);
        toolBar.add(Box.createHorizontalStrut(20));
        toolBar.add(btnDelete);
        tableEmployees = new JTable();
        tableEmployees.setRowHeight(38);
        tableEmployees.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableEmployees.setSelectionBackground(new Color(239, 246, 255));
        tableEmployees.setSelectionForeground(TEXT_MAIN);
        tableEmployees.setShowGrid(false);
        tableEmployees.setIntercellSpacing(new Dimension(0, 0));
        JTableHeader header = tableEmployees.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_MAIN);
        header.setPreferredSize(new Dimension(0, 42));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        JScrollPane scrollPane = new JScrollPane(tableEmployees);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        bottomPanel.add(toolBar, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
        btnSearch.addActionListener(e -> loadTableData(txtSearch.getText()));
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            loadTableData("");
        });
        btnClear.addActionListener(e -> clearForm());
        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String pass = new String(txtPassword.getPassword());
            int roleId = cbRole.getSelectedIndex() + 1; 
            String result = employeeBUS.xuLyThemNhanVien(name, email, pass, phone, roleId);
            switch (result) {
                case "EMPTY":
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                    break;
                case "INVALID_EMAIL":
                    JOptionPane.showMessageDialog(this, "Email không hợp lệ!");
                    break;
                case "INVALID_PHONE":
                    JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ!");
                    break;
                case "SUCCESS":
                    JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
                    clearForm();
                    loadTableData(""); 
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Lỗi hệ thống hoặc Email đã tồn tại!");
            }
        });
        btnDelete.addActionListener(e -> {
            int row = tableEmployees.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!");
                return;
            }
            int id = Integer.parseInt(tableEmployees.getValueAt(row, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(this, "Chuyển trạng thái nhân viên này thành Ngưng hoạt động?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (employeeBUS.xoaNhanVien(id)) {
                        JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái thành công!");
                        loadTableData(txtSearch.getText());
                    } else {
                        JOptionPane.showMessageDialog(this, "Không thể xóa Admin hoặc thao tác thất bại!");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi DB: " + ex.getMessage());
                }
            }
        });
    }
    private JPanel createStatCard(String title, String value) {
        return createStatCard(title, new JLabel(value));
    }
    private JPanel createStatCard(String title, JLabel lblValue) {
        RoundedPanel p = new RoundedPanel(20, CARD_BG);
        p.setLayout(new GridLayout(2, 1, 0, 5));
        p.setBorder(new EmptyBorder(12, 18, 12, 18));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(280, 70)); 
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(TEXT_SUB);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblValue.setForeground(TEXT_MAIN);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        p.add(lblTitle);
        p.add(lblValue);
        return p;
    }
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_MAIN);
        return lbl;
    }
    private JTextField createTextField() {
        JTextField txt = new JTextField();
        styleTextField(txt);
        return txt;
    }
    private void styleTextField(JTextField txt) {
        txt.setPreferredSize(new Dimension(0, 36));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(0, 10, 0, 10)));
    }
    private JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(bgColor.darker());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fgColor);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }
    private void clearForm() {
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtPassword.setText("");
        cbRole.setSelectedIndex(0);
    }
    private void loadTableData(String keyword) {
        try {
            DefaultTableModel model = employeeBUS.getEmployeeTableModel(keyword);
            tableEmployees.setModel(model);
            if ((keyword == null || keyword.trim().isEmpty()) && model.getRowCount() > 0) {
                int maxId = -1;
                String newestName = "Chưa có";
                for (int i = 0; i < model.getRowCount(); i++) {
                    int id = Integer.parseInt(model.getValueAt(i, 0).toString());
                    if (id > maxId) {
                        maxId = id;
                        newestName = model.getValueAt(i, 1).toString();
                    }
                }
                lblNewestEmployee.setText(newestName);
            }
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            tableEmployees.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            tableEmployees.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            tableEmployees.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
            tableEmployees.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }
    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, radius, radius);
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
