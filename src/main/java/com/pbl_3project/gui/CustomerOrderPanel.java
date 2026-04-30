package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.pbl_3project.bus.OrderBUS;

public class CustomerOrderPanel extends JPanel {
    private static final Color BG = new Color(248, 250, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color ACCENT = new Color(56, 189, 248);
    private static final Color ACCENT2 = new Color(255, 107, 74);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT_H = new Color(15, 23, 42);
    private static final Color TEXT_S = new Color(100, 116, 139);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final Color HEADER_BG = new Color(241, 245, 249);
    private JTable tableOrders;
    private DefaultTableModel ordersModel;
    private JTable tableDetails;
    private DefaultTableModel detailsModel;
    private JLabel lblDetailTitle;
    private JLabel lblStatus;
    private JButton btnCancel;
    private JButton btnPay;
    private JPanel detailHeader;
    private final int customerId;
    private final OrderBUS orderBUS = new OrderBUS();
    private final com.pbl_3project.dao.ReviewDAO reviewDAO = new com.pbl_3project.dao.ReviewDAO();

    public CustomerOrderPanel(int customerId) {
        this.customerId = customerId;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        initComponents();
        loadOrders();
    }

    public void refresh() {
        loadOrders();
    }

    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(20, 32, 20, 32)));
        JLabel lblTitle = new JLabel("ĐƠN HÀNG CỦA TÔI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_H);
        JButton btnRefresh = createAccentButton("Làm mới", ACCENT);
        btnRefresh.addActionListener(e -> loadOrders());
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        String[] colsOrders = { "ID", "Mã ĐH", "Số điện thoại", "Tổng tiền (VNĐ)", "Ngày đặt", "Trạng thái" };
        ordersModel = new DefaultTableModel(colsOrders, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableOrders = buildStyledTable(ordersModel);
        tableOrders.getColumnModel().removeColumn(tableOrders.getColumnModel().getColumn(0));
        tableOrders.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableOrders.getColumnModel().getColumn(1).setPreferredWidth(130);
        tableOrders.getColumnModel().getColumn(2).setPreferredWidth(160);
        tableOrders.getColumnModel().getColumn(3).setPreferredWidth(160);
        tableOrders.getColumnModel().getColumn(4).setPreferredWidth(150);
        tableOrders.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        JScrollPane scrollOrders = buildScrollPane(tableOrders);
        JPanel panelOrders = new JPanel(new BorderLayout());
        panelOrders.setBackground(WHITE);
        panelOrders.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(16, 24, 0, 24),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1, true),
                        new EmptyBorder(0, 0, 0, 0))));
        panelOrders.add(buildSectionLabel("Danh sách đơn hàng", true), BorderLayout.NORTH);
        panelOrders.add(scrollOrders, BorderLayout.CENTER);
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
        detailHeader = new JPanel(new BorderLayout());
        detailHeader.setBackground(HEADER_BG);
        detailHeader.setBorder(new EmptyBorder(10, 16, 10, 16));
        lblDetailTitle = new JLabel("Chọn một đơn hàng để xem chi tiết");
        lblDetailTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDetailTitle.setForeground(TEXT_S);
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false);
        btnCancel = createAccentButton("Hủy đơn", DANGER);
        btnPay = createAccentButton("Thanh toán", SUCCESS);
        btnConfirmReceipt = createAccentButton("Đã nhận hàng", SUCCESS);
        btnReturnRequest = createAccentButton("Đổi trả", WARNING);
        lblStatus = new JLabel("");
        btnCancel.setVisible(false);
        btnPay.setVisible(false);
        btnConfirmReceipt.setVisible(false);
        btnReturnRequest.setVisible(false);
        rightHeader.add(btnCancel);
        rightHeader.add(btnPay);
        rightHeader.add(btnConfirmReceipt);
        rightHeader.add(btnReturnRequest);
        rightHeader.add(lblStatus);
        detailHeader.add(lblDetailTitle, BorderLayout.WEST);
        detailHeader.add(rightHeader, BorderLayout.EAST);
        btnCancel.addActionListener(e -> handleCancelOrder());
        btnPay.addActionListener(e -> handlePayment());
        btnConfirmReceipt.addActionListener(e -> handleConfirmReceipt());
        btnReturnRequest.addActionListener(e -> handleRequestReturn());
        JPanel panelDetails = new JPanel(new BorderLayout());
        panelDetails.setBackground(WHITE);
        panelDetails.setBorder(new EmptyBorder(0, 24, 16, 24));
        panelDetails.add(buildSectionLabel("Chi tiết đơn hàng", false), BorderLayout.NORTH);
        JPanel detailInner = new JPanel(new BorderLayout());
        detailInner.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        detailInner.add(detailHeader, BorderLayout.NORTH);
        detailInner.add(scrollDetails, BorderLayout.CENTER);
        panelDetails.add(detailInner, BorderLayout.CENTER);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelOrders, panelDetails);
        split.setDividerLocation(280);
        split.setDividerSize(8);
        split.setBorder(null);
        split.setBackground(BG);
        split.setOpaque(false);
        add(split, BorderLayout.CENTER);
        tableOrders.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = tableOrders.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = tableOrders.convertRowIndexToModel(viewRow);
                    int orderId = Integer.parseInt(ordersModel.getValueAt(modelRow, 0).toString());
                    String status = ordersModel.getValueAt(modelRow, 5).toString();
                    loadOrderDetail(orderId, status);
                }
            }
        });
    }

    private void loadOrders() {
        try {
            DefaultTableModel model = orderBUS.getOrdersByCustomer(customerId);
            ordersModel.setRowCount(0);
            for (int i = 0; i < model.getRowCount(); i++) {
                int id = (int) model.getValueAt(i, 0);
                String status = (String) model.getValueAt(i, 5);
                if (status.equals("Hoàn thành") && reviewDAO.isOrderReviewed(id)) {
                    status += " (Đã đánh giá)";
                }
                ordersModel.addRow(new Object[] {
                        id,
                        model.getValueAt(i, 1),
                        model.getValueAt(i, 2),
                        model.getValueAt(i, 3),
                        model.getValueAt(i, 4),
                        status
                });
            }
            detailsModel.setRowCount(0);
            lblDetailTitle.setText("Chọn một đơn hàng để xem chi tiết");
            lblDetailTitle.setForeground(TEXT_S);
            lblStatus.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải dữ liệu đơn hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton btnConfirmReceipt;
    private JButton btnReturnRequest;
    private JButton btnReview;

    private void loadOrderDetail(int orderId, String status) {
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
            lblDetailTitle.setText("Chi tiết Đơn #" + orderId);
            lblDetailTitle.setForeground(TEXT_H);
            lblStatus.setText("  " + status + "  ");
            lblStatus.setOpaque(true);
            lblStatus.setBorder(new EmptyBorder(4, 10, 4, 10));
            Color sc = getStatusColor(status);
            lblStatus.setBackground(sc);
            lblStatus.setForeground(Color.WHITE);
            lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnCancel.setVisible(false);
            btnPay.setVisible(false);
            btnConfirmReceipt.setVisible(false);
            btnReturnRequest.setVisible(false);
            if (btnReview == null) {
                btnReview = createAccentButton("Đánh giá", new Color(238, 77, 45));
                btnReview.addActionListener(e -> {
                    int row = tableOrders.getSelectedRow();
                    if (row < 0)
                        return;
                    int modelRow = tableOrders.convertRowIndexToModel(row);
                    int oId = (int) ordersModel.getValueAt(modelRow, 0);
                    ReviewDialog reviewDialog = new ReviewDialog((Frame) SwingUtilities.getWindowAncestor(this),
                            customerId, oId);
                    reviewDialog.setVisible(true);
                    loadOrders();
                });
                ((JPanel) btnCancel.getParent()).add(btnReview, 0);
            }
            btnReview.setVisible(false);

            if (status.equals("Chưa thanh toán")) {
                btnCancel.setVisible(true);
                btnPay.setVisible(true);
            } else if (status.equals("Đã thanh toán")) {
                btnCancel.setVisible(true);
            } else if (status.equals("Đang giao")) {
                btnConfirmReceipt.setVisible(true);
                btnReturnRequest.setVisible(true);
            } else if (status.startsWith("Hoàn thành")) {
                btnReview.setVisible(true);
                btnReturnRequest.setVisible(true);
                try {
                    if (reviewDAO.isOrderReviewed(orderId)) {
                        btnReview.setEnabled(false);
                        btnReview.setText("Đã đánh giá");
                    } else {
                        btnReview.setEnabled(true);
                        btnReview.setText("Đánh giá");
                    }
                } catch (Exception ex) {
                    btnReview.setEnabled(true);
                }
            }
            detailHeader.revalidate();
            detailHeader.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải chi tiết: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleConfirmReceipt() {
        int row = tableOrders.getSelectedRow();
        if (row < 0)
            return;
        int modelRow = tableOrders.convertRowIndexToModel(row);
        int orderId = (int) ordersModel.getValueAt(modelRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận bạn đã nhận được đơn hàng #" + orderId + "?\nBạn sẽ có thể đánh giá sản phẩm sau đó.",
                "Xác nhận đã nhận hàng", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (orderBUS.confirmReceipt(orderId)) {
                    JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái đơn hàng!");
                    loadOrders();
                    ReviewDialog reviewDialog = new ReviewDialog((Frame) SwingUtilities.getWindowAncestor(this),
                            customerId, orderId);
                    reviewDialog.setVisible(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    private void handleRequestReturn() {
        int row = tableOrders.getSelectedRow();
        if (row < 0)
            return;
        int modelRow = tableOrders.convertRowIndexToModel(row);
        int orderId = (int) ordersModel.getValueAt(modelRow, 0);
        ReturnRequestDialog dialog = new ReturnRequestDialog((Frame) SwingUtilities.getWindowAncestor(this), orderId);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                if (orderBUS.requestReturn(orderId, dialog.getReason(), dialog.getReturnRequestType(),
                        dialog.getDetails())) {
                    JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu thành công!");
                    loadOrders();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    private void handleCancelOrder() {
        int row = tableOrders.getSelectedRow();
        if (row < 0)
            return;
        int modelRow = tableOrders.convertRowIndexToModel(row);
        int orderId = (int) ordersModel.getValueAt(modelRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn hủy đơn hàng #" + orderId + "?\nSản phẩm sẽ được hoàn lại kho.",
                "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (orderBUS.cancelOrder(orderId)) {
                    JOptionPane.showMessageDialog(this, "Đã hủy đơn hàng thành công!");
                    loadOrders();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi hủy: " + ex.getMessage());
            }
        }
    }

    private void handlePayment() {
        int row = tableOrders.getSelectedRow();
        if (row < 0)
            return;
        int modelRow = tableOrders.convertRowIndexToModel(row);
        int orderId = (int) ordersModel.getValueAt(modelRow, 0);
        String totalStr = (String) ordersModel.getValueAt(modelRow, 3);
        double total = Double.parseDouble(totalStr.replaceAll("[^0-9]", ""));
        String[] options = { "Thanh toán khi nhận hàng (COD)", "Chuyển khoản / Ví điện tử" };
        String method = (String) JOptionPane.showInputDialog(this,
                "Chọn phương thức thanh toán cho đơn #" + orderId + ":",
                "Thanh toán", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (method != null) {
            boolean canProceed = true;
            if (method.equals("Chuyển khoản / Ví điện tử")) {
                PaymentQRDialog qrDialog = new PaymentQRDialog((Frame) SwingUtilities.getWindowAncestor(this), total,
                        "PAY_" + orderId + "_" + System.currentTimeMillis() % 1000);
                qrDialog.setVisible(true);
                if (!qrDialog.isPaymentSuccessful()) {
                    canProceed = false;
                }
            }
            if (canProceed) {
                try {
                    if (orderBUS.payOrder(orderId, method, total)) {
                        JOptionPane.showMessageDialog(this,
                                "✅ " + (method.contains("COD") ? "Đã chọn phương thức COD" : "Thanh toán thành công")
                                        + " cho đơn #" + orderId);
                        loadOrders();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
                }
            }
        }
    }

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? WHITE : new Color(248, 250, 252));
                    c.setForeground(TEXT_H);
                } else {
                    c.setBackground(new Color(224, 242, 255));
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
        table.setSelectionBackground(new Color(224, 242, 255));
        table.setSelectionForeground(TEXT_H);
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(HEADER_BG);
        th.setForeground(TEXT_S);
        th.setPreferredSize(new Dimension(0, 40));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
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

    private JPanel buildSectionLabel(String text, boolean isTop) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(isTop ? WHITE : WHITE);
        p.setBorder(new EmptyBorder(isTop ? 12 : 12, 16, 8, 16));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(TEXT_H);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    private JButton createAccentButton(String text, Color color) {
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
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        return btn;
    }

    private Color getStatusColor(String status) {
        if (status == null)
            return TEXT_S;
        String s = status.trim();
        if (s.startsWith("Đã thanh toán"))
            return SUCCESS;
        if (s.startsWith("Đang giao"))
            return ACCENT;
        if (s.startsWith("Hoàn thành"))
            return new Color(16, 185, 129);
        if (s.startsWith("Đã hủy"))
            return DANGER;
        if (s.startsWith("Yêu cầu Đổi/Trả"))
            return WARNING;
        if (s.startsWith("Chưa thanh toán"))
            return WARNING;
        return TEXT_S;
    }

    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            String status = value == null ? "" : value.toString().trim();
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            if (!isSelected) {
                Color bg = getStatusColor(status);
                lbl.setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 40));
                lbl.setForeground(bg.darker());
            } else {
                lbl.setBackground(new Color(224, 242, 255));
                lbl.setForeground(TEXT_H);
            }
            lbl.setOpaque(true);
            return lbl;
        }
    }
}
