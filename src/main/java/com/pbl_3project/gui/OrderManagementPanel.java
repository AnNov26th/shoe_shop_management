package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.pbl_3project.bus.OrderBUS;

/**
 * Panel Quản lý Đơn hàng dành cho Admin.
 * Hiển thị toàn bộ đơn hàng, xem chi tiết và cập nhật trạng thái.
 */
public class OrderManagementPanel extends JPanel {

    // ---- BẢNG MÀU ----
    private static final Color BG_CONTENT = new Color(248, 250, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color ACCENT = new Color(34, 177, 76); // Xanh lá (đồng bộ AdminForm)
    private static final Color BTN_BLUE = new Color(56, 189, 248); // Cyan
    private static final Color BTN_GRAY = new Color(148, 163, 184);
    private static final Color BTN_DANGER = new Color(239, 68, 68);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT_H = new Color(15, 23, 42);
    private static final Color TEXT_S = new Color(100, 116, 139);
    private static final Color HEADER_BG = new Color(241, 245, 249);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color DANGER_CLR = new Color(239, 68, 68);
    private static final Color WARNING = new Color(245, 158, 11);

    private static final String[] STATUSES = {
            "Chưa thanh toán", "Đã thanh toán", "Đang giao", "Hoàn thành", "Đã hủy", "Yêu cầu Đổi/Trả"
    };

    private JTable tableOrders;
    private DefaultTableModel ordersModel;
    private JTable tableDetails;
    private DefaultTableModel detailsModel;
    private JTextField txtSearch;
    private JLabel lblDetailTitle;
    private JLabel lblTotalOrders;
    private JComboBox<String> cmbStatus;

    private final OrderBUS orderBUS = new OrderBUS();
    private final boolean isAdmin;

    public OrderManagementPanel(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_CONTENT);
        initComponents();
        loadOrders("");
    }

    public OrderManagementPanel() {
        this(true);
    }

    // =========================================================
    // KHỞI TẠO GIAO DIỆN
    // =========================================================
    private void initComponents() {

        // -------- TOOLBAR --------
        JPanel toolBar = new JPanel(new BorderLayout());
        toolBar.setBackground(WHITE);
        toolBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(16, 24, 16, 24)));

        // Bên trái: Tiêu đề + badge tổng đơn
        JPanel leftBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftBar.setOpaque(false);

        JLabel lblTitle = new JLabel("QUẢN LÝ ĐƠN HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_H);

        lblTotalOrders = new JLabel("0 đơn");
        lblTotalOrders.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalOrders.setForeground(WHITE);
        lblTotalOrders.setBackground(ACCENT);
        lblTotalOrders.setOpaque(true);
        lblTotalOrders.setBorder(new EmptyBorder(4, 10, 4, 10));

        leftBar.add(lblTitle);
        leftBar.add(lblTotalOrders);

        // Bên phải: Ô tìm kiếm + nút
        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightBar.setOpaque(false);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSearch.setForeground(TEXT_H);

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(220, 36));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(0, 12, 0, 12)));
        txtSearch.setToolTipText("Nhập mã ĐH hoặc số điện thoại");

        JButton btnSearch = createRoundButton("Tìm kiếm", BTN_BLUE);
        JButton btnRefresh = createRoundButton("Làm mới", BTN_GRAY);

        rightBar.add(lblSearch);
        rightBar.add(txtSearch);
        rightBar.add(btnSearch);
        rightBar.add(btnRefresh);

        toolBar.add(leftBar, BorderLayout.WEST);
        toolBar.add(rightBar, BorderLayout.EAST);

        // -------- BẢNG DANH SÁCH ĐƠN --------
        String[] colsOrders = { "ID", "Mã Đơn", "Khách Hàng", "SĐT", "Tổng Tiền", "Ngày Đặt", "Trạng Thái" };
        ordersModel = new DefaultTableModel(colsOrders, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableOrders = buildStyledTable(ordersModel);

        // Ẩn cột ID khỏi giao diện người dùng nhưng vẫn giữ trong Model
        tableOrders.getColumnModel().removeColumn(tableOrders.getColumnModel().getColumn(0));

        tableOrders.getColumnModel().getColumn(0).setPreferredWidth(100); // Mã Đơn
        tableOrders.getColumnModel().getColumn(1).setPreferredWidth(140); // Khách Hàng
        tableOrders.getColumnModel().getColumn(2).setPreferredWidth(110); // SĐT
        tableOrders.getColumnModel().getColumn(3).setPreferredWidth(130); // Tổng Tiền
        tableOrders.getColumnModel().getColumn(4).setPreferredWidth(150); // Ngày Đặt
        tableOrders.getColumnModel().getColumn(5).setPreferredWidth(130); // Trạng Thái
        tableOrders.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        JScrollPane scrollOrders = buildScrollPane(tableOrders);

        JPanel panelOrders = new JPanel(new BorderLayout());
        panelOrders.setBackground(WHITE);
        panelOrders.setBorder(new EmptyBorder(14, 20, 0, 20));
        panelOrders.add(buildSectionTitle("Danh sách đơn hàng"), BorderLayout.NORTH);

        JPanel ordersCard = new JPanel(new BorderLayout());
        ordersCard.setBorder(new LineBorder(BORDER, 1, true));
        ordersCard.add(scrollOrders, BorderLayout.CENTER);
        panelOrders.add(ordersCard, BorderLayout.CENTER);

        // -------- BẢNG CHI TIẾT ĐƠN --------
        String[] colsDetail = { "Tên sản phẩm", "SKU", "Size", "Màu sắc", "SL", "Đơn giá (VNĐ)", "Thành tiền (VNĐ)" };
        detailsModel = new DefaultTableModel(colsDetail, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableDetails = buildStyledTable(detailsModel);
        tableDetails.getColumnModel().getColumn(0).setPreferredWidth(200);
        tableDetails.getColumnModel().getColumn(1).setPreferredWidth(120);
        tableDetails.getColumnModel().getColumn(2).setPreferredWidth(60);
        tableDetails.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableDetails.getColumnModel().getColumn(4).setPreferredWidth(50);
        tableDetails.getColumnModel().getColumn(5).setPreferredWidth(130);
        tableDetails.getColumnModel().getColumn(6).setPreferredWidth(140);

        JScrollPane scrollDetails = buildScrollPane(tableDetails);

        // Header khu chi tiết (tiêu đề + toolbar cập nhật trạng thái)
        JPanel detailHeader = new JPanel(new BorderLayout());
        detailHeader.setBackground(HEADER_BG);
        detailHeader.setBorder(new EmptyBorder(10, 16, 10, 16));

        lblDetailTitle = new JLabel("← Chọn một đơn hàng để xem chi tiết & cập nhật trạng thái");
        lblDetailTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDetailTitle.setForeground(TEXT_S);

        // Toolbar cập nhật trạng thái (bên phải)
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        statusBar.setOpaque(false);

        JLabel lblChangeStatus = new JLabel("Cập nhật trạng thái:");
        lblChangeStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblChangeStatus.setForeground(TEXT_H);

        cmbStatus = new JComboBox<>(new DefaultComboBoxModel<>(STATUSES));
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbStatus.setPreferredSize(new Dimension(180, 32));

        JButton btnUpdateStatus = createRoundButton("Lưu", ACCENT);

        statusBar.add(lblChangeStatus);
        statusBar.add(cmbStatus);
        statusBar.add(btnUpdateStatus);

        if (!isAdmin) {
            statusBar.setVisible(false);
            lblDetailTitle.setText("Chọn một đơn hàng để xem chi tiết");
        }

        detailHeader.add(lblDetailTitle, BorderLayout.WEST);
        detailHeader.add(statusBar, BorderLayout.EAST);

        JPanel panelDetails = new JPanel(new BorderLayout());
        panelDetails.setBackground(WHITE);
        panelDetails.setBorder(new EmptyBorder(0, 20, 14, 20));
        panelDetails.add(buildSectionTitle("Chi tiết đơn hàng"), BorderLayout.NORTH);

        JPanel detailCard = new JPanel(new BorderLayout());
        detailCard.setBorder(new LineBorder(BORDER, 1, true));
        detailCard.add(detailHeader, BorderLayout.NORTH);
        detailCard.add(scrollDetails, BorderLayout.CENTER);
        panelDetails.add(detailCard, BorderLayout.CENTER);

        // -------- SPLIT PANE --------
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelOrders, panelDetails);
        split.setDividerLocation(300);
        split.setDividerSize(8);
        split.setBorder(null);
        split.setBackground(BG_CONTENT);
        split.setOpaque(false);

        add(toolBar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        // -------- SỰ KIỆN --------
        btnSearch.addActionListener(e -> loadOrders(txtSearch.getText()));
        txtSearch.addActionListener(e -> loadOrders(txtSearch.getText()));

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadOrders("");
        });

        tableOrders.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = tableOrders.getSelectedRow();
                if (viewRow >= 0) {
                    // Chuyển index từ View sang Model để lấy đúng cột ID ẩn
                    int modelRow = tableOrders.convertRowIndexToModel(viewRow);
                    int orderId = Integer.parseInt(ordersModel.getValueAt(modelRow, 0).toString());
                    String status = ordersModel.getValueAt(modelRow, 6).toString();
                    loadOrderDetail(orderId, status);
                }
            }
        });

        btnUpdateStatus.addActionListener(e -> handleUpdateStatus());
    }

    // =========================================================
    // LOAD DỮ LIỆU
    // =========================================================
    private void loadOrders(String keyword) {
        try {
            DefaultTableModel model = orderBUS.getAllOrders(keyword);
            ordersModel.setRowCount(0);
            for (int i = 0; i < model.getRowCount(); i++) {
                ordersModel.addRow(new Object[] {
                        model.getValueAt(i, 0), // ID (int)
                        model.getValueAt(i, 1), // Mã Đơn
                        model.getValueAt(i, 2), // Khách Hàng
                        model.getValueAt(i, 3), // SĐT
                        model.getValueAt(i, 4), // Tổng Tiền
                        model.getValueAt(i, 5), // Ngày Đặt
                        model.getValueAt(i, 6) // Trạng Thái
                });
            }
            lblTotalOrders.setText(model.getRowCount() + " đơn");
            // Reset chi tiết
            detailsModel.setRowCount(0);
            if (isAdmin) {
                lblDetailTitle.setText("← Chọn một đơn hàng để xem chi tiết & cập nhật trạng thái");
            } else {
                lblDetailTitle.setText("← Chọn một đơn hàng để xem chi tiết");
            }
            lblDetailTitle.setForeground(TEXT_S);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOrderDetail(int orderId, String currentStatus) {
        try {
            DefaultTableModel model = orderBUS.getOrderDetails(orderId);
            detailsModel.setRowCount(0);
            for (int i = 0; i < model.getRowCount(); i++) {
                detailsModel.addRow(new Object[] {
                        model.getValueAt(i, 0),
                        model.getValueAt(i, 1),
                        model.getValueAt(i, 2),
                        model.getValueAt(i, 3),
                        model.getValueAt(i, 4),
                        model.getValueAt(i, 5),
                        model.getValueAt(i, 6)
                });
            }
            lblDetailTitle.setText("Chi tiết Đơn hàng #" + orderId);
            lblDetailTitle.setForeground(TEXT_H);
            // Set combobox về trạng thái hiện tại
            cmbStatus.setSelectedItem(currentStatus);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải chi tiết: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateStatus() {
        int viewRow = tableOrders.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một đơn hàng trước!", "Chưa chọn đơn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tableOrders.convertRowIndexToModel(viewRow);
        int orderId = Integer.parseInt(ordersModel.getValueAt(modelRow, 0).toString());
        String newStatus = (String) cmbStatus.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Cập nhật trạng thái Đơn #" + orderId + " → \"" + newStatus + "\"?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (orderBUS.updateOrderStatus(orderId, newStatus)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!");
                    loadOrders(txtSearch.getText());
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================
    // HÀM TIỆN ÍCH XÂY DỰNG UI
    // =========================================================
    private JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? WHITE : new Color(248, 250, 252));
                    c.setForeground(TEXT_H);
                } else {
                    c.setBackground(new Color(220, 252, 231)); // Xanh lá nhạt khi chọn
                    c.setForeground(TEXT_H);
                }
                return c;
            }
        };
        table.setRowHeight(42);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(220, 252, 231));
        table.setSelectionForeground(TEXT_H);
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(HEADER_BG);
        th.setForeground(TEXT_S);
        th.setPreferredSize(new Dimension(0, 42));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        return table;
    }

    private JScrollPane buildScrollPane(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel buildSectionTitle(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 0, 8, 0));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(TEXT_H);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    private JButton createRoundButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = getModel().isRollover()
                        ? new GradientPaint(0, 0, color.darker(), getWidth(), 0, color)
                        : new GradientPaint(0, 0, color, getWidth(), 0, color.brighter());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private Color getStatusColor(String status) {
        if (status == null)
            return TEXT_S;
        switch (status.trim()) {
            case "Đã thanh toán":
                return SUCCESS;
            case "Đang giao":
                return BTN_BLUE;
            case "Hoàn thành":
                return new Color(16, 185, 129);
            case "Đã hủy":
                return DANGER_CLR;
            case "Yêu cầu Đổi/Trả":
                return WARNING;
            case "Chưa thanh toán":
                return WARNING;
            default:
                return TEXT_S;
        }
    }

    // ---- Custom Renderer cho cột Trạng thái ----
    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);
            String status = value == null ? "" : value.toString().trim();
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

            if (!isSelected) {
                Color bg = getStatusColor(status);
                lbl.setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 35));
                lbl.setForeground(bg.darker());
            } else {
                lbl.setBackground(new Color(220, 252, 231));
                lbl.setForeground(TEXT_H);
            }
            lbl.setOpaque(true);
            return lbl;
        }
    }
}
