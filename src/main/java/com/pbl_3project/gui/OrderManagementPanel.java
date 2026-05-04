package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.ArrayList;
import java.awt.Window;
import com.pbl_3project.bus.OrderBUS;
import com.pbl_3project.dto.CartItem;

public class OrderManagementPanel extends JPanel {
    private static final Color BG_CONTENT = new Color(248, 250, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color ACCENT = new Color(34, 197, 94); // Modern Green
    private static final Color BTN_BLUE = new Color(37, 99, 235); // Modern Blue
    private static final Color BTN_GRAY = new Color(100, 116, 139); // Modern Slate
    private static final Color BTN_DANGER = new Color(220, 38, 38); // Modern Red
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT_H = new Color(15, 23, 42);
    private static final Color TEXT_S = new Color(100, 116, 139);
    private static final Color HEADER_BG = new Color(241, 245, 249);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color DANGER_CLR = new Color(239, 68, 68);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final String[] STATUSES = {
            "Chưa thanh toán", "Đã thanh toán", "Đang giao", "Hoàn thành", "Đã hủy", "Yêu cầu Đổi/Trả", "Đã đổi/trả",
            "Từ chối đổi/trả"
    };
    private JTable tableOrders;
    private DefaultTableModel ordersModel;
    private JTable tableDetails;
    private DefaultTableModel detailsModel;
    private JTextField txtSearch;
    private JTextField txtFromDate;
    private JTextField txtToDate;
    private JLabel lblDetailTitle;
    private JLabel lblTotalOrders;
    private JComboBox<String> cmbStatus;
    private JEditorPane previewPane; // For invoice preview
    private JTextArea txtAreaDetails; // For text view
    private JScrollPane scrollText;
    private JButton btnToggleView;
    private boolean isTextView = false;
    private final OrderBUS orderBUS = new OrderBUS();
    private final boolean isAdmin;
    private final boolean isInvoiceMode;

    public OrderManagementPanel(boolean isAdmin) {
        this(isAdmin, false);
    }

    public OrderManagementPanel(boolean isAdmin, boolean isInvoiceMode) {
        this.isAdmin = isAdmin;
        this.isInvoiceMode = isInvoiceMode;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_CONTENT);
        initComponents();
        loadOrders("");
    }

    private void initComponents() {
        JPanel toolBar = new JPanel(new BorderLayout());
        toolBar.setBackground(WHITE);
        toolBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(16, 24, 16, 24)));
        JPanel leftBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftBar.setOpaque(false);
        JLabel lblTitle = new JLabel(isInvoiceMode ? "DANH SÁCH HÓA ĐƠN" : "QUẢN LÝ ĐƠN HÀNG ONLINE");
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
        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightBar.setOpaque(false);
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSearch.setForeground(TEXT_H);
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(160, 36));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(0, 12, 0, 12)));
        txtSearch.setToolTipText("Mã ĐH / SĐT");

        txtFromDate = new JTextField(8);
        txtFromDate.setPreferredSize(new Dimension(100, 36));
        txtFromDate.setToolTipText("Từ ngày (YYYY-MM-DD)");
        txtFromDate.setBorder(new LineBorder(BORDER, 1, true));

        txtToDate = new JTextField(8);
        txtToDate.setPreferredSize(new Dimension(100, 36));
        txtToDate.setToolTipText("Đến ngày (YYYY-MM-DD)");
        txtToDate.setBorder(new LineBorder(BORDER, 1, true));

        JButton btnSearch = createRoundButton("Tìm kiếm", BTN_BLUE);
        JButton btnRefresh = createRoundButton("Làm mới", BTN_GRAY);

        rightBar.add(lblSearch);
        rightBar.add(txtSearch);
        rightBar.add(new JLabel(" Từ:"));
        rightBar.add(txtFromDate);
        rightBar.add(new JLabel(" Đến:"));
        rightBar.add(txtToDate);
        rightBar.add(btnSearch);
        rightBar.add(btnRefresh);
        toolBar.add(leftBar, BorderLayout.WEST);
        toolBar.add(rightBar, BorderLayout.EAST);
        String[] colsOrders = { "ID", "Mã Đơn", "Khách Hàng", "SĐT", "Tổng Tiền", "Hình thức TT", "Nhân Viên",
                "Ngày Đặt", "Trạng Thái", "Ngày Thanh Toán", "Ngày Giao" };
        ordersModel = new DefaultTableModel(colsOrders, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableOrders = buildStyledTable(ordersModel);
        tableOrders.getColumnModel().removeColumn(tableOrders.getColumnModel().getColumn(0));
        tableOrders.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableOrders.getColumnModel().getColumn(1).setPreferredWidth(140);
        tableOrders.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableOrders.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableOrders.getColumnModel().getColumn(4).setPreferredWidth(120);
        tableOrders.getColumnModel().getColumn(5).setPreferredWidth(120);
        tableOrders.getColumnModel().getColumn(6).setPreferredWidth(130);
        tableOrders.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());

        // Ẩn bớt các cột ngày thanh toán và ngày giao nếu muốn (hoặc để hiện thị hết)
        // tableOrders.getColumnModel().removeColumn(tableOrders.getColumnModel().getColumn(9));
        // // Ngày Thanh Toán
        // tableOrders.getColumnModel().removeColumn(tableOrders.getColumnModel().getColumn(10));
        // // Ngày Giao
        JScrollPane scrollOrders = buildScrollPane(tableOrders);
        JPanel panelOrders = new JPanel(new BorderLayout());
        panelOrders.setBackground(WHITE);
        panelOrders.setBorder(new EmptyBorder(14, 20, 0, 20));
        panelOrders.add(buildSectionTitle("Danh sách đơn hàng"), BorderLayout.NORTH);
        JPanel ordersCard = new JPanel(new BorderLayout());
        ordersCard.setBorder(new LineBorder(BORDER, 1, true));
        ordersCard.add(scrollOrders, BorderLayout.CENTER);
        panelOrders.add(ordersCard, BorderLayout.CENTER);
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

        previewPane = new JEditorPane();
        previewPane.setEditable(false);
        previewPane.setContentType("text/html");
        JScrollPane scrollPreview = new JScrollPane(previewPane);
        scrollPreview.setBorder(null);

        txtAreaDetails = new JTextArea();
        txtAreaDetails.setEditable(false);
        txtAreaDetails.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtAreaDetails.setBorder(new EmptyBorder(10, 10, 10, 10));
        scrollText = new JScrollPane(txtAreaDetails);
        scrollText.setBorder(null);
        scrollText.setVisible(false);

        JPanel detailCard = new JPanel(new BorderLayout());
        detailCard.setBorder(new LineBorder(BORDER, 1, true));
        JPanel detailHeader = new JPanel(new BorderLayout());
        detailHeader.setBackground(HEADER_BG);
        detailHeader.setBorder(new EmptyBorder(10, 16, 10, 16));
        lblDetailTitle = new JLabel("← Chọn một đơn hàng để xem chi tiết & cập nhật trạng thái");
        lblDetailTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDetailTitle.setForeground(TEXT_S);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        JButton btnPrintInvoice = createRoundButton("In hóa đơn", BTN_BLUE);
        btnPrintInvoice.addActionListener(e -> handlePrintInvoice());

        btnToggleView = createRoundButton("Xem hóa đơn", BTN_GRAY);
        btnToggleView.addActionListener(e -> toggleDetailView());

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

        actionPanel.add(btnPrintInvoice);
        actionPanel.add(btnToggleView);
        actionPanel.add(statusBar);

        if (!isAdmin) {
            statusBar.setVisible(false);
            lblDetailTitle.setText("Chọn một đơn hàng để xem chi tiết");
        }
        detailHeader.add(lblDetailTitle, BorderLayout.WEST);
        detailHeader.add(actionPanel, BorderLayout.EAST);
        JPanel panelDetails = new JPanel(new BorderLayout());
        panelDetails.setBackground(WHITE);
        panelDetails.setBorder(new EmptyBorder(0, 20, 14, 20));
        detailCard.add(detailHeader, BorderLayout.NORTH);
        JPanel viewContainer = new JPanel(new CardLayout());
        viewContainer.setOpaque(false);

        if (isInvoiceMode) {
            viewContainer.add(scrollPreview, "GUI");
            panelDetails.add(buildSectionTitle("Bản xem trước Hóa đơn"), BorderLayout.NORTH);
        } else {
            viewContainer.add(scrollDetails, "GUI");
            panelDetails.add(buildSectionTitle("Chi tiết sản phẩm"), BorderLayout.NORTH);
        }
        viewContainer.add(scrollText, "TEXT");
        detailCard.add(viewContainer, BorderLayout.CENTER);

        panelDetails.add(detailCard, BorderLayout.CENTER);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelOrders, panelDetails);
        split.setDividerLocation(300);
        split.setDividerSize(8);
        split.setBorder(null);
        split.setBackground(BG_CONTENT);
        split.setOpaque(false);
        add(toolBar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        btnSearch.addActionListener(e -> loadOrders(txtSearch.getText()));
        txtSearch.addActionListener(e -> loadOrders(txtSearch.getText()));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            txtFromDate.setText("");
            txtToDate.setText("");
            loadOrders("");
        });
        tableOrders.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = tableOrders.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = tableOrders.convertRowIndexToModel(viewRow);
                    int orderId = Integer.parseInt(ordersModel.getValueAt(modelRow, 0).toString());
                    String status = ordersModel.getValueAt(modelRow, 8).toString();
                    loadOrderDetail(orderId, status);
                }
            }
        });
        btnUpdateStatus.addActionListener(e -> handleUpdateStatus());
    }

    private void loadOrders(String keyword) {
        loadOrders(keyword, txtFromDate.getText().trim(), txtToDate.getText().trim());
    }

    private void loadOrders(String keyword, String fromDate, String toDate) {
        try {
            DefaultTableModel model = orderBUS.getAllOrders(keyword, fromDate, toDate);
            ordersModel.setRowCount(0);
            for (int i = 0; i < model.getRowCount(); i++) {
                ordersModel.addRow(new Object[] {
                        model.getValueAt(i, 0),
                        model.getValueAt(i, 1),
                        model.getValueAt(i, 2),
                        model.getValueAt(i, 3),
                        model.getValueAt(i, 4),
                        model.getValueAt(i, 5),
                        model.getValueAt(i, 6),
                        model.getValueAt(i, 7),
                        model.getValueAt(i, 8),
                        model.getValueAt(i, 9),
                        model.getValueAt(i, 10)
                });
            }
            lblTotalOrders.setText(model.getRowCount() + " đơn");
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
            lblDetailTitle.setText("Chi tiết Đơn hàng #" + orderId + " (" + currentStatus + ")");
            lblDetailTitle.setForeground(TEXT_H);
            cmbStatus.setSelectedItem(currentStatus);

            if (isInvoiceMode) {
                generateInvoicePreview(orderId);
            }
            updateDetailsTextView(orderId, currentStatus);
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

    private void toggleDetailView() {
        isTextView = !isTextView;
        JPanel viewContainer = (JPanel) scrollText.getParent();
        CardLayout cl = (CardLayout) viewContainer.getLayout();
        if (isTextView) {
            cl.show(viewContainer, "TEXT");
            btnToggleView.setText("Xem dạng Bảng");
        } else {
            cl.show(viewContainer, "GUI");
            btnToggleView.setText("Xem hóa đơn");
        }
    }

    private void updateDetailsTextView(int orderId, String status) {
        StringBuilder sb = new StringBuilder();
        int viewRow = tableOrders.getSelectedRow();
        if (viewRow < 0)
            return;
        int modelRow = tableOrders.convertRowIndexToModel(viewRow);

        sb.append("--- THÔNG TIN ĐƠN HÀNG ---\n");
        sb.append("Mã đơn hàng: ").append(ordersModel.getValueAt(modelRow, 1)).append("\n");
        sb.append("Khách hàng: ").append(ordersModel.getValueAt(modelRow, 2)).append("\n");
        sb.append("Số điện thoại: ").append(ordersModel.getValueAt(modelRow, 3)).append("\n");
        sb.append("Ngày đặt: ").append(ordersModel.getValueAt(modelRow, 7)).append("\n");
        sb.append("Trạng thái: ").append(status).append("\n");
        sb.append("Hình thức TT: ").append(ordersModel.getValueAt(modelRow, 5)).append("\n");
        sb.append("Nhân viên: ").append(ordersModel.getValueAt(modelRow, 6)).append("\n");
        sb.append("\n--- CHI TIẾT SẢN PHẨM ---\n");
        sb.append(String.format("%-30s | %-10s | %-5s | %-10s\n", "Tên SP", "SL", "Size", "Thành tiền"));
        sb.append("--------------------------------------------------------------\n");

        for (int i = 0; i < detailsModel.getRowCount(); i++) {
            sb.append(String.format("%-30s | %-10s | %-5s | %-10s\n",
                    detailsModel.getValueAt(i, 0),
                    detailsModel.getValueAt(i, 4),
                    detailsModel.getValueAt(i, 2),
                    detailsModel.getValueAt(i, 6)));
        }

        sb.append("--------------------------------------------------------------\n");
        sb.append("TỔNG CỘNG: ").append(ordersModel.getValueAt(modelRow, 4)).append("\n");

        txtAreaDetails.setText(sb.toString());
        txtAreaDetails.setCaretPosition(0);
    }

    private void handlePrintInvoice() {
        int viewRow = tableOrders.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một đơn hàng để in hóa đơn!", "Chưa chọn đơn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tableOrders.convertRowIndexToModel(viewRow);
        String orderCode = ordersModel.getValueAt(modelRow, 1).toString();

        // Lấy tổng tiền
        String totalAmountStr = ordersModel.getValueAt(modelRow, 4).toString();
        totalAmountStr = totalAmountStr.replace(" VNĐ", "").replace(",", "").replace(".", "").trim();
        double totalAmount = 0;
        try {
            totalAmount = Double.parseDouble(totalAmountStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // Lấy danh sách sản phẩm
        List<CartItem> cartItems = new ArrayList<>();
        for (int i = 0; i < detailsModel.getRowCount(); i++) {
            String name = detailsModel.getValueAt(i, 0).toString();
            String sku = detailsModel.getValueAt(i, 1).toString();
            String size = detailsModel.getValueAt(i, 2).toString();
            String color = detailsModel.getValueAt(i, 3).toString();
            int quantity = Integer.parseInt(detailsModel.getValueAt(i, 4).toString());

            String priceStr = detailsModel.getValueAt(i, 5).toString();
            priceStr = priceStr.replace(",", "").replace(".", "").trim();
            double price = 0;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                price = 0;
            }

            CartItem item = new CartItem(sku, name, size, color, price, quantity, 0);
            cartItems.add(item);
        }

        String paymentMethod = ordersModel.getValueAt(modelRow, 5).toString();
        String staffName = ordersModel.getValueAt(modelRow, 6).toString();

        java.sql.Timestamp orderAt = null;
        java.sql.Timestamp paymentAt = null;
        java.sql.Timestamp deliveredAt = null;

        if (ordersModel.getValueAt(modelRow, 7) instanceof java.sql.Timestamp)
            orderAt = (java.sql.Timestamp) ordersModel.getValueAt(modelRow, 7);
        if (ordersModel.getValueAt(modelRow, 9) instanceof java.sql.Timestamp)
            paymentAt = (java.sql.Timestamp) ordersModel.getValueAt(modelRow, 9);
        if (ordersModel.getValueAt(modelRow, 10) instanceof java.sql.Timestamp)
            deliveredAt = (java.sql.Timestamp) ordersModel.getValueAt(modelRow, 10);

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof java.awt.Frame) {
            InvoicePrinter printer = new InvoicePrinter((java.awt.Frame) window, orderCode, cartItems, totalAmount,
                    staffName, paymentMethod);
            printer.setTimestamps(orderAt, paymentAt, deliveredAt);
            printer.setVisible(true);
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
                    c.setBackground(new Color(220, 252, 231));
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

    private JButton createRoundButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color c1 = baseColor;
                Color c2 = baseColor.darker();

                if (getModel().isPressed()) {
                    g2.setColor(c2);
                } else if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, c1.brighter(), 0, getHeight(), c1));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Subtle border
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

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
        btn.setPreferredSize(new Dimension(100, 38));
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
            case "Đã đổi/trả":
                return SUCCESS;
            case "Từ chối đổi/trả":
                return DANGER_CLR;
            case "Chưa thanh toán":
                return WARNING;
            default:
                return TEXT_S;
        }
    }

    private void generateInvoicePreview(int orderId) {
        try {
            DefaultTableModel orderData = orderBUS.getAllOrders("#" + orderId);
            if (orderData.getRowCount() == 0)
                return;

            String customer = orderData.getValueAt(0, 1).toString();
            String phone = orderData.getValueAt(0, 2).toString();
            String staff = orderData.getValueAt(0, 3).toString();
            String method = orderData.getValueAt(0, 4).toString();
            String createdAt = orderData.getValueAt(0, 5).toString();
            double total = Double.parseDouble(orderData.getValueAt(0, 7).toString().replace(",", ""));

            StringBuilder itemsHtml = new StringBuilder();
            for (int i = 0; i < detailsModel.getRowCount(); i++) {
                itemsHtml.append("<tr>")
                        .append("<td style='padding:6px; border-bottom:1px solid #eee;'>")
                        .append(detailsModel.getValueAt(i, 0)).append("</td>")
                        .append("<td style='text-align:center; padding:6px; border-bottom:1px solid #eee;'>")
                        .append(detailsModel.getValueAt(i, 4)).append("</td>")
                        .append("<td style='text-align:right; padding:6px; border-bottom:1px solid #eee;'>")
                        .append(detailsModel.getValueAt(i, 5)).append(" VNĐ</td>")
                        .append("<td style='text-align:right; padding:6px; border-bottom:1px solid #eee;'>")
                        .append(detailsModel.getValueAt(i, 6)).append(" VNĐ</td>")
                        .append("</tr>");
            }

            String html = "<html><body style='font-family:Segoe UI, sans-serif; padding:25px; background-color:white; color:#333;'>"
                    +
                    "<div style='text-align:center; border-bottom:2px solid #3498db; padding-bottom:15px;'>" +
                    "<h1 style='color:#3498db; margin:0;'>SHOE SHOP T&T</h1>" +
                    "<p style='margin:5px 0; color:#7f8c8d;'>99 Tô Hiến Thành, Sơn Trà, Đà Nẵng | ĐT: 0123 456 789</p>"
                    +
                    "</div>" +
                    "<div style='text-align:center; margin-top:20px;'>" +
                    "<h2 style='margin:0; color:#2c3e50;'>HÓA ĐƠN BÁN HÀNG</h2>" +
                    "<p style='color:#7f8c8d;'>Mã: <b>ORD" + orderId + "</b></p>" +
                    "</div>" +
                    "<table style='width:100%; margin-top:20px; font-size:13px;'>" +
                    "<tr><td><b>Khách hàng:</b> " + customer + "</td><td style='text-align:right'><b>Ngày:</b> "
                    + createdAt + "</td></tr>" +
                    "<tr><td><b>Số điện thoại:</b> " + phone
                    + "</td><td style='text-align:right'><b>Thanh toán:</b> <span style='color:#2980b9; font-weight:bold;'>"
                    + method + "</span></td></tr>" +
                    "<tr><td><b>Nhân viên:</b> " + staff
                    + "</td><td style='text-align:right'><b>Tình trạng:</b> <span style='color:#27ae60; font-weight:bold;'>Đã thanh toán</span></td></tr>"
                    +
                    "</table>" +
                    "<table style='width:100%; margin-top:20px; border-collapse:collapse; font-size:12px;'>" +
                    "<tr style='background-color:#f8f9fa; border-bottom:2px solid #dee2e6;'>" +
                    "<th style='padding:10px; text-align:left;'>Sản phẩm</th>" +
                    "<th style='padding:10px; text-align:center;'>SL</th>" +
                    "<th style='padding:10px; text-align:right;'>Đơn giá</th>" +
                    "<th style='padding:10px; text-align:right;'>Thành tiền</th></tr>" +
                    itemsHtml.toString() +
                    "</table>" +
                    "<div style='margin-top:25px; text-align:right; border-top:2px solid #3498db; padding-top:15px;'>" +
                    "<span style='font-size:16px; font-weight:normal;'>Tổng cộng: </span>" +
                    "<span style='font-size:22px; color:#e74c3c; font-weight:bold;'>" + String.format("%,.0f", total)
                    + " VNĐ</span>" +
                    "</div>" +
                    "<div style='margin-top:40px; text-align:center; color:#95a5a6;'>" +
                    "<p style='font-style:italic; margin:0;'>Cảm ơn quý khách đã mua sắm tại Shoe Shop T&T!</p>" +
                    "<p style='font-size:10px; margin-top:5px;'>Bản xem trước - "
                    + java.time.LocalDateTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    + "</p>" +
                    "</div>" +
                    "</body></html>";

            previewPane.setText(html);
            previewPane.setCaretPosition(0);
        } catch (Exception e) {
            previewPane.setText(
                    "<html><body><p style='color:red;'>Lỗi hiển thị preview: " + e.getMessage() + "</p></body></html>");
        }
    }

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
