package com.pbl_3project.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import com.pbl_3project.bus.DiscountBUS;

public class PromotionManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtCode, txtValue, txtMinOrder, txtMaxDiscount, txtLimit, txtStart, txtEnd;
    private JComboBox<String> cbType;
    private DiscountBUS discountBUS = new DiscountBUS();

    // ---- BẢNG MÀU PREMIUM ----
    private static final Color BG_CONTENT = new Color(248, 250, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color ACCENT = new Color(34, 177, 76); // Xanh lá Admin
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT_H = new Color(15, 23, 42);
    private static final Color TEXT_S = new Color(100, 116, 139);
    private static final Color HEADER_BG = new Color(241, 245, 249);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color BTN_GRAY = new Color(148, 163, 184);

    public PromotionManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_CONTENT);
        initComponents();
        loadData();
    }

    private void initComponents() {
        // =========================================
        // 1. TOOLBAR / HEADER
        // =========================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(20, 30, 20, 30)));

        JLabel lblTitle = new JLabel("🎟️ QUẢN LÝ MÃ GIẢM GIÁ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_H);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // =========================================
        // 2. LEFT PANEL (DANH SÁCH MÃ)
        // =========================================
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BG_CONTENT);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 10));

        RoundedPanel tableCard = new RoundedPanel(15, WHITE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                switch (col) {
                    case 0:
                        return Integer.class; // ID
                    case 3:
                    case 4:
                    case 5:
                        return Double.class; // Values
                    case 6:
                    case 7:
                        return Integer.class; // Limits/Usage
                    case 8:
                    case 9:
                        return java.util.Date.class; // Dates
                    default:
                        return Object.class;
                }
            }
        };
        table = buildStyledTable(model);
        table.setAutoCreateRowSorter(true);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.getViewport().setBackground(WHITE);

        tableCard.add(scrollTable, BorderLayout.CENTER);
        leftPanel.add(tableCard, BorderLayout.CENTER);

        // =========================================
        // 3. RIGHT PANEL (FORM NHẬP LIỆU)
        // =========================================
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BG_CONTENT);
        rightPanel.setPreferredSize(new Dimension(400, 0));
        rightPanel.setBorder(new EmptyBorder(20, 10, 20, 20));

        RoundedPanel formCard = new RoundedPanel(15, WHITE);
        formCard.setLayout(new BorderLayout()); // Sử dụng BorderLayout để chia vùng rõ ràng

        // --- Phần Header của Form (NORTH) ---
        JPanel formTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        formTitlePanel.setBackground(HEADER_BG);
        formTitlePanel.setOpaque(false); // Để bo góc đẹp hơn
        JLabel lblFormTitle = new JLabel("Thông Tin Chi Tiết");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(TEXT_H);
        formTitlePanel.add(lblFormTitle);
        formTitlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        formCard.add(formTitlePanel, BorderLayout.NORTH);

        // --- Phần nhập liệu (CENTER - Cho phép cuộn) ---
        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setBackground(WHITE);
        formContent.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0); // Khoảng cách giữa các ô
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        txtCode = createStyledTextField();
        cbType = new JComboBox<>(new String[] { "Percentage (%)", "Fixed Amount (VNĐ)" });
        cbType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbType.setBackground(WHITE);
        txtValue = createStyledTextField();
        txtMinOrder = createStyledTextField();
        txtMinOrder.setText("0");
        txtMaxDiscount = createStyledTextField();
        txtMaxDiscount.setText("0");
        txtLimit = createStyledTextField();
        txtLimit.setText("100");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        txtStart = createStyledTextField();
        txtStart.setText(LocalDateTime.now().format(dtf));
        txtEnd = createStyledTextField();
        txtEnd.setText(LocalDateTime.now().plusMonths(1).format(dtf));

        int row = 0;
        addFormGroup(formContent, "Mã giảm giá (Code):", txtCode, gbc, row++);
        addFormGroup(formContent, "Loại giảm giá:", cbType, gbc, row++);
        addFormGroup(formContent, "Giá trị giảm:", txtValue, gbc, row++);
        addFormGroup(formContent, "Đơn tối thiểu (VNĐ):", txtMinOrder, gbc, row++);
        addFormGroup(formContent, "Giảm tối đa (Chỉ áp dụng cho %):", txtMaxDiscount, gbc, row++);
        addFormGroup(formContent, "Giới hạn số lượt dùng:", txtLimit, gbc, row++);
        addFormGroup(formContent, "Ngày bắt đầu:", txtStart, gbc, row++);
        addFormGroup(formContent, "Ngày kết thúc:", txtEnd, gbc, row++);

        JScrollPane scrollForm = new JScrollPane(formContent);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        formCard.add(scrollForm, BorderLayout.CENTER); // Gắn form vào giữa

        // --- Phần Nút bấm (SOUTH - Cố định không bị trôi hay đè) ---
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // Lưới 2x2 cho 3 nút
        btnPanel.setBackground(WHITE);
        btnPanel.setBorder(new EmptyBorder(15, 20, 20, 20)); // Căn lề cho nút

        JButton btnAdd = createRoundButton("Thêm Mã", ACCENT);
        JButton btnClear = createRoundButton("Làm Mới", BTN_GRAY);
        JButton btnDelete = createRoundButton("Xóa Mã", DANGER);

        btnPanel.add(btnAdd);
        btnPanel.add(btnClear);
        btnPanel.add(btnDelete);

        formCard.add(btnPanel, BorderLayout.SOUTH); // Gắn cố định dưới đáy

        rightPanel.add(formCard, BorderLayout.CENTER);

        // =========================================
        // GHÉP LẠI (SPLIT PANE)
        // =========================================
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(720);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_CONTENT);
        add(splitPane, BorderLayout.CENTER);

        // Sự kiện nút
        btnAdd.addActionListener(e -> handleAdd());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());
    }

    // =========================================================
    // HÀM TIỆN ÍCH UI
    // =========================================================
    private void addFormGroup(JPanel p, String label, JComponent field, GridBagConstraints gbc, int y) {
        gbc.gridy = y * 2;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_S);
        lbl.setBorder(new EmptyBorder(0, 0, 5, 0));
        p.add(lbl, gbc);

        gbc.gridy = y * 2 + 1;
        p.add(field, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(TEXT_H);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12))); // Box to, nhập liệu thoải mái
        return tf;
    }

    private JButton createRoundButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(bg.darker());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 42));
        return btn;
    }

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? WHITE : new Color(248, 250, 252));
                }
                return c;
            }
        };
        t.setRowHeight(42);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(220, 252, 231)); // Highlight xanh lá
        t.setSelectionForeground(TEXT_H);

        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setBackground(HEADER_BG);
        h.setForeground(TEXT_S);
        h.setPreferredSize(new Dimension(0, 42));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        t.setDefaultRenderer(Object.class, centerRenderer);

        return t;
    }

    // =========================================================
    // LOGIC XỬ LÝ (GIỮ NGUYÊN CỦA BRO)
    // =========================================================
    private void loadData() {
        try {
            DefaultTableModel newData = discountBUS.getAllPromotions();
            model.setRowCount(0);
            model.setColumnIdentifiers(new Object[] { "ID", "Mã", "Loại", "Giá trị", "Đơn tối thiểu", "Giảm tối đa",
                    "Giới hạn", "Đã dùng", "Bắt đầu", "Kết thúc" });

            for (int i = 0; i < newData.getRowCount(); i++) {
                Object[] row = new Object[newData.getColumnCount()];
                for (int j = 0; j < newData.getColumnCount(); j++) {
                    row[j] = newData.getValueAt(i, j);
                }
                model.addRow(row);
            }

            if (table.getColumnCount() > 0) {
                table.getColumnModel().getColumn(0).setPreferredWidth(40);
                table.getColumnModel().getColumn(1).setPreferredWidth(130);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void handleAdd() {
        try {
            String code = txtCode.getText().trim();
            if (code.isEmpty())
                throw new Exception("Mã không được để trống!");

            String type = cbType.getSelectedIndex() == 0 ? "Percentage" : "Fixed";
            double val = Double.parseDouble(txtValue.getText());
            double min = Double.parseDouble(txtMinOrder.getText());
            double max = Double.parseDouble(txtMaxDiscount.getText());
            int limit = Integer.parseInt(txtLimit.getText());
            String start = txtStart.getText();
            String end = txtEnd.getText();

            if (discountBUS.addPromotion(code, type, val, min, max, limit, start, end)) {
                JOptionPane.showMessageDialog(this, "✅ Thêm mã giảm giá thành công!");
                loadData();
                clearForm();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtCode.setText("");
        txtValue.setText("");
        txtMinOrder.setText("0");
        txtMaxDiscount.setText("0");
        txtLimit.setText("100");
        cbType.setSelectedIndex(0);
        table.clearSelection();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        txtStart.setText(LocalDateTime.now().format(dtf));
        txtEnd.setText(LocalDateTime.now().plusMonths(1).format(dtf));
    }

    private void handleTableSelection() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;

        int modelRow = table.convertRowIndexToModel(row);

        txtCode.setText(model.getValueAt(modelRow, 1).toString());
        cbType.setSelectedItem(model.getValueAt(modelRow, 2).toString().equals("Percentage") ? "Percentage (%)"
                : "Fixed Amount (VNĐ)");
        txtValue.setText(model.getValueAt(modelRow, 3).toString());
        txtMinOrder.setText(model.getValueAt(modelRow, 4).toString());
        txtMaxDiscount.setText(model.getValueAt(modelRow, 5).toString());
        txtLimit.setText(model.getValueAt(modelRow, 6).toString());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Object startObj = model.getValueAt(modelRow, 8);
        Object endObj = model.getValueAt(modelRow, 9);

        if (startObj instanceof java.sql.Timestamp) {
            txtStart.setText(((java.sql.Timestamp) startObj).toLocalDateTime().format(dtf));
        }
        if (endObj instanceof java.sql.Timestamp) {
            txtEnd.setText(((java.sql.Timestamp) endObj).toLocalDateTime().format(dtf));
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn mã muốn xóa!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa mã này?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (discountBUS.deletePromotion(id)) {
                    loadData();
                    clearForm();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage());
            }
        }
    }

    // =========================================================
    // CLASS VẼ PANEL BO GÓC
    // =========================================================
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
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}