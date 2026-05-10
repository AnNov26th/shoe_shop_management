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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import com.pbl_3project.bus.CartBUS;
import com.pbl_3project.dto.CartItem;
import com.pbl_3project.dao.OrderDAO;
import com.pbl_3project.dao.CartDAO;

public class CustomerCartPanel extends JPanel {
    private JTable tableCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotalAmount;
    private JLabel lblSubtotalInfo;
    private JLabel lblDiscountInfo;
    private CartBUS cartBUS;
    private Runnable updateCartBadge;
    private boolean isUpdatingTable = false;
    private int customerId;
    private javax.swing.JComboBox<String> cbCoupon;
    private int currentPromoId = -1;
    private double discountAmount = 0;
    private String currentPromoType = "";
    private double currentPromoValue = 0;
    private double currentPromoMaxDiscount = 0;
    private com.pbl_3project.bus.DiscountBUS discountBUS = new com.pbl_3project.bus.DiscountBUS();
    private static final Color BG = new Color(248, 250, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color ACCENT2 = new Color(255, 107, 74);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT_H = new Color(15, 23, 42);
    private static final Color TEXT_S = new Color(100, 116, 139);

    public CustomerCartPanel(int customerId, CartBUS cartBUS, Runnable updateCartBadge) {
        this.customerId = customerId;
        this.cartBUS = cartBUS;
        this.updateCartBadge = updateCartBadge;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        initComponents();
    }

    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 245, 245));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(20, 32, 20, 32)));
        JLabel lblTitle = new JLabel("GIỎ HÀNG CỦA TÔI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_H);
        header.add(lblTitle, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        String[] cols = { "Chọn", "Sản phẩm", "Size", "Màu sắc", " − ", "SL", " + ", "Đơn giá", "Thành tiền" };
        cartModel = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return super.getColumnClass(columnIndex);
            }
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 0 || c == 5;
            }
        };
        tableCart = new JTable(cartModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                CartItem item = cartBUS.getCartItems().get(row);
                if (item.getStock() == 0) {
                    c.setBackground(new Color(241, 245, 249)); // Grey bg
                    c.setForeground(new Color(148, 163, 184)); // Grey text
                } else if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? WHITE : new Color(248, 250, 252));
                    c.setForeground(TEXT_H);
                } else {
                    c.setForeground(TEXT_H);
                }
                return c;
            }
        };
        tableCart.setRowHeight(48);
        tableCart.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableCart.setShowGrid(false);
        tableCart.setIntercellSpacing(new Dimension(0, 0));
        tableCart.setSelectionBackground(new Color(239, 246, 255));
        tableCart.setSelectionForeground(Color.BLACK);
        tableCart.setSelectionForeground(TEXT_H);
        JTableHeader th = tableCart.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(new Color(241, 245, 249));
        th.setForeground(TEXT_S);
        th.setPreferredSize(new Dimension(0, 40));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        ButtonRenderer btnRenderer = new ButtonRenderer();
        tableCart.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableCart.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableCart.getColumnModel().getColumn(2).setPreferredWidth(60);
        tableCart.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableCart.getColumnModel().getColumn(4).setPreferredWidth(44);
        tableCart.getColumnModel().getColumn(4).setCellRenderer(btnRenderer);
        tableCart.getColumnModel().getColumn(5).setPreferredWidth(60);
        tableCart.getColumnModel().getColumn(5).setCellRenderer(center);
        tableCart.getColumnModel().getColumn(6).setPreferredWidth(44);
        tableCart.getColumnModel().getColumn(6).setCellRenderer(btnRenderer);
        tableCart.getColumnModel().getColumn(7).setPreferredWidth(100);
        tableCart.getColumnModel().getColumn(7).setCellRenderer(center);
        tableCart.getColumnModel().getColumn(8).setPreferredWidth(120);
        tableCart.getColumnModel().getColumn(8).setCellRenderer(center);
        tableCart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableCart.rowAtPoint(e.getPoint());
                int col = tableCart.columnAtPoint(e.getPoint());
                if (row >= 0) {
                    if (tableCart.isEditing())
                        tableCart.getCellEditor().stopCellEditing();
                    CartItem item = cartBUS.getCartItems().get(row);
                    if (item.getStock() > 0) {
                        int qty = item.getQuantity();
                        if (col == 4)
                            handleQuantityChange(row, qty - 1);
                        else if (col == 6)
                            handleQuantityChange(row, qty + 1);
                    }
                }
            }
        });
        cartModel.addTableModelListener(e -> {
            if (!isUpdatingTable && e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                if (e.getColumn() == 0) {
                    boolean isSelected = (Boolean) cartModel.getValueAt(row, 0);
                    cartBUS.getCartItems().get(row).setSelected(isSelected);
                    refreshCartGUI();
                } else if (e.getColumn() == 5) {
                    try {
                        int newQty = Integer.parseInt(cartModel.getValueAt(row, 5).toString());
                        handleQuantityChange(row, newQty);
                    } catch (NumberFormatException ex) {
                        refreshCartGUI();
                    }
                }
            }
        });
        JScrollPane scroll = new JScrollPane(tableCart);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        scroll.getViewport().setBackground(WHITE);
        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(WHITE);
        tableWrap.setBorder(new EmptyBorder(0, 32, 0, 32));
        tableWrap.add(scroll, BorderLayout.CENTER);
        add(tableWrap, BorderLayout.CENTER);
        JPanel footer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(25, 32, 30, 32)));
        footer.setPreferredSize(new Dimension(0, 220));
        footer.setOpaque(false);
        JPanel totalRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        totalRow.setOpaque(false);
        JPanel couponPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        couponPanel.setOpaque(false);
        JLabel lblC = new JLabel("Mã giảm giá:");
        lblC.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblC.setForeground(TEXT_H);
        
        cbCoupon = new javax.swing.JComboBox<>();
        cbCoupon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbCoupon.setBackground(WHITE);
        cbCoupon.setPreferredSize(new Dimension(200, 38));
        refreshCouponList();

        JButton btnApply = new JButton("Áp dụng") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(56, 189, 248));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnApply.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnApply.setForeground(WHITE);
        btnApply.setContentAreaFilled(false);
        btnApply.setBorderPainted(false);
        btnApply.setFocusPainted(false);
        btnApply.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnApply.setPreferredSize(new Dimension(100, 38));
        btnApply.addActionListener(e -> {
            if (cbCoupon.getSelectedIndex() <= 0) {
                clearPromo();
                refreshCartGUI();
                return;
            }
            String selected = (String) cbCoupon.getSelectedItem();
            String code = selected.split(" - ")[0];
            handleApplyCoupon(code);
        });

        JButton btnExchange = new JButton("Đổi mã") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 158, 11)); // Amber color
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnExchange.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExchange.setForeground(WHITE);
        btnExchange.setContentAreaFilled(false);
        btnExchange.setBorderPainted(false);
        btnExchange.setFocusPainted(false);
        btnExchange.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExchange.setPreferredSize(new Dimension(90, 38));
        btnExchange.addActionListener(e -> handleExchangeVoucher());

        couponPanel.add(lblC);
        couponPanel.add(cbCoupon);
        couponPanel.add(btnApply);
        couponPanel.add(btnExchange);
        footer.add(couponPanel, BorderLayout.WEST);

        JPanel pnlPriceInfo = new JPanel();
        pnlPriceInfo.setLayout(new BoxLayout(pnlPriceInfo, BoxLayout.Y_AXIS));
        pnlPriceInfo.setOpaque(false);

        lblSubtotalInfo = new JLabel("Tạm tính: 0 VNĐ");
        lblSubtotalInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtotalInfo.setForeground(TEXT_S);
        lblSubtotalInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);

        lblDiscountInfo = new JLabel("");
        lblDiscountInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDiscountInfo.setForeground(new Color(34, 197, 94));
        lblDiscountInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel pnlFinal = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFinal.setOpaque(false);
        JLabel lblTxt = new JLabel("TỔNG THANH TOÁN:");
        lblTxt.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTxt.setForeground(TEXT_S);

        lblTotalAmount = new JLabel("0 VNĐ");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotalAmount.setForeground(ACCENT2);

        pnlFinal.add(lblTxt);
        pnlFinal.add(lblTotalAmount);
        pnlFinal.setAlignmentX(Component.RIGHT_ALIGNMENT);

        pnlPriceInfo.add(lblSubtotalInfo);
        pnlPriceInfo.add(Box.createVerticalStrut(5));
        pnlPriceInfo.add(lblDiscountInfo);
        pnlPriceInfo.add(Box.createVerticalStrut(5));
        pnlPriceInfo.add(pnlFinal);

        totalRow.add(pnlPriceInfo);

        JButton btnCheckout = new JButton("ĐẶT HÀNG NGAY  →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = getModel().isRollover()
                        ? new GradientPaint(0, 0, new Color(255, 130, 100), getWidth(), 0, ACCENT2)
                        : new GradientPaint(0, 0, ACCENT2, getWidth(), 0, new Color(255, 130, 100));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCheckout.setForeground(WHITE);
        btnCheckout.setContentAreaFilled(false);
        btnCheckout.setBorderPainted(false);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.setBorder(new EmptyBorder(14, 36, 14, 36));
        btnCheckout.addActionListener(e -> handleCheckout());
        JPanel bottomRight = new JPanel();
        bottomRight.setOpaque(false);
        bottomRight.setLayout(new BoxLayout(bottomRight, BoxLayout.Y_AXIS));
        totalRow.setAlignmentX(RIGHT_ALIGNMENT);
        btnCheckout.setAlignmentX(RIGHT_ALIGNMENT);
        bottomRight.add(totalRow);
        bottomRight.add(Box.createVerticalStrut(14));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnCheckout);
        bottomRight.add(btnRow);
        footer.add(bottomRight, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    private void handleCheckout() {
        java.util.List<CartItem> selectedItems = new java.util.ArrayList<>();
        for (CartItem item : cartBUS.getCartItems()) {
            if (item.isSelected()) {
                if (item.getStock() == 0) {
                    JOptionPane.showMessageDialog(this, "Sản phẩm '" + item.getName() + "' đã hết hàng. Vui lòng bỏ chọn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                selectedItems.add(item);
            }
        }
        
        if (selectedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bạn chưa chọn sản phẩm nào để thanh toán!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String defaultPhone = "";
        String defaultAddress = "";
        try {
            java.util.Map<String, String> profile = new com.pbl_3project.dao.UserDAO()
                    .getCustomerProfile(this.customerId);
            if (profile != null) {
                defaultPhone = profile.get("phone");
                defaultAddress = profile.get("address");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BorderLayout());
        pnlInfo.setPreferredSize(new Dimension(520, 480));
        pnlInfo.setBackground(Color.WHITE);

        JPanel pnlHeader = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(59, 130, 246));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        pnlHeader.setPreferredSize(new Dimension(0, 65));
        pnlHeader.setBorder(new EmptyBorder(0, 25, 0, 25));
        JLabel lblHeader = new JLabel("Xác nhận Đặt hàng");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(Color.WHITE);
        pnlHeader.add(lblHeader, BorderLayout.WEST);

        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(25, 30, 25, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        int gridy = 0;

        JLabel lblInfoTitle = new JLabel("THÔNG TIN GIAO HÀNG");
        lblInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInfoTitle.setForeground(new Color(100, 116, 139));
        gbc.gridy = gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainContent.add(lblInfoTitle, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridy = gridy++;
        mainContent.add(new JLabel("Số điện thoại *") {
            {
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setForeground(new Color(30, 41, 59));
            }
        }, gbc);

        JTextField txtPhone = new JTextField(defaultPhone);
        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPhone.setPreferredSize(new Dimension(0, 45));
        txtPhone.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(0, 15, 0, 15)));
        gbc.gridy = gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainContent.add(txtPhone, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridy = gridy++;
        mainContent.add(new JLabel("Địa chỉ chi tiết *") {
            {
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setForeground(new Color(30, 41, 59));
            }
        }, gbc);

        JTextField txtAddress = new JTextField(defaultAddress);
        txtAddress.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtAddress.setPreferredSize(new Dimension(0, 45));
        txtAddress.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(0, 15, 0, 15)));
        gbc.gridy = gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainContent.add(txtAddress, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridy = gridy++;
        mainContent.add(new JLabel("Phương thức thanh toán") {
            {
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setForeground(new Color(30, 41, 59));
            }
        }, gbc);

        String[] paymentMethodsList = { "Thanh toán khi nhận hàng (COD)",
                "Thanh toán trực tuyến (Chuyển khoản / Quét mã QR)" };
        javax.swing.JComboBox<String> cbPayment = new javax.swing.JComboBox<>(paymentMethodsList);
        cbPayment.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cbPayment.setPreferredSize(new Dimension(0, 45));
        cbPayment.setBackground(Color.WHITE);
        gbc.gridy = gridy++;
        gbc.insets = new Insets(0, 0, 25, 0);
        mainContent.add(cbPayment, gbc);

        JPanel pnlSummary = new JPanel(new BorderLayout());
        pnlSummary.setBackground(new Color(248, 250, 252));
        pnlSummary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(15, 20, 15, 20)));

        double subtotal = cartBUS.calculateTotalAmount();
        double finalAmount = subtotal - discountAmount;
        if (finalAmount < 0)
            finalAmount = 0;

        String summaryHtml = "<html><body style='font-family:Segoe UI; font-size:11px;'>" +
                "<table width='100%'>" +
                "<tr><td>Tạm tính:</td><td align='right'>" + String.format("%,.0f VNĐ", subtotal) + "</td></tr>" +
                (discountAmount > 0
                        ? "<tr><td style='color:#22c55e;'>Giảm giá:</td><td align='right' style='color:#22c55e;'>-"
                                + String.format("%,.0f VNĐ", discountAmount) + "</td></tr>"
                        : "")
                +
                "<tr><td style='font-weight:bold; font-size:13px; padding-top:10px;'>TỔNG CỘNG:</td>" +
                "<td align='right' style='font-weight:bold; font-size:13px; color:#ef4444; padding-top:10px;'>"
                + String.format("%,.0f VNĐ", finalAmount) + "</td></tr>" +
                "</table></body></html>";

        JLabel lblSummary = new JLabel(summaryHtml);
        pnlSummary.add(lblSummary, BorderLayout.CENTER);

        gbc.gridy = gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContent.add(pnlSummary, gbc);

        pnlInfo.add(pnlHeader, BorderLayout.NORTH);
        pnlInfo.add(mainContent, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, pnlInfo, "Xác nhận đơn hàng", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION)
            return;

        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        int methodChoice = cbPayment.getSelectedIndex();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại nhận hàng!");
            return;
        }
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ nhận hàng!");
            return;
        }

        boolean isPaymentConfirmed = false;
        String status = "Chưa thanh toán";
        if (methodChoice == 1) {
            PaymentQRDialog qrDialog = new PaymentQRDialog((Frame) SwingUtilities.getWindowAncestor(this), finalAmount,
                    "KH" + customerId + "_" + System.currentTimeMillis() % 10000);
            qrDialog.setVisible(true);
            if (qrDialog.isPaymentSuccessful()) {
                isPaymentConfirmed = true;
                status = "Đã thanh toán";
            } else {
                JOptionPane.showMessageDialog(this, "Thanh toán đã bị hủy hoặc chưa hoàn tất.", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xác nhận đặt hàng (COD) với tổng tiền: " + String.format("%,.0f VNĐ", finalAmount) + "?",
                    "Xác nhận đơn hàng", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                isPaymentConfirmed = true;
                status = "Chưa thanh toán";
            }
        }
        if (isPaymentConfirmed) {
            try {
                OrderDAO orderDAO = new OrderDAO();
                String paymentMethod = (methodChoice == 1) ? "Chuyển khoản / Quét mã QR"
                        : "Thanh toán khi nhận hàng (COD)";
                boolean success = orderDAO.createOrderOnline(
                        this.customerId,
                        phone,
                        paymentMethod,
                        subtotal,
                        discountAmount,
                        finalAmount,
                        (currentPromoId > 0 ? currentPromoId : null),
                        selectedItems,
                        status);
                if (success) {
                    if (methodChoice == 1) {
                    }
                    JOptionPane.showMessageDialog(this,
                            "🎉 " + (methodChoice == 1 ? "Thanh toán và đặt hàng" : "Đặt hàng") + " thành công!");
                    CartDAO cDao = new CartDAO();
                    for(CartItem sel : selectedItems) {
                        cDao.removeFromCart(this.customerId, sel.getSku());
                        cartBUS.getCartItems().remove(sel);
                    }
                    currentPromoId = -1;
                    discountAmount = 0;
                    refreshCartGUI();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đặt hàng", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleApplyCoupon(String code) {
        if (code == null || code.isEmpty()) {
            currentPromoId = -1;
            discountAmount = 0;
            refreshCartGUI();
            return;
        }
        try {
            double total = cartBUS.calculateTotalAmount();
            Object[] promo = discountBUS.validateCoupon(code, total);
            currentPromoId = (int) promo[0];
            currentPromoType = (String) promo[1];
            currentPromoValue = (double) promo[2];
            currentPromoMaxDiscount = (double) promo[4];

            recalculateDiscount(total);

            JOptionPane.showMessageDialog(this,
                    "✅ Đã áp dụng mã giảm giá: -" + String.format("%,.0f", discountAmount) + " VNĐ");
            refreshCartGUI();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi mã giảm giá", JOptionPane.WARNING_MESSAGE);
            clearPromo();
            refreshCartGUI();
        }
    }

    private void recalculateDiscount(double total) {
        if (currentPromoId == -1)
            return;

        String type = currentPromoType.toUpperCase();
        if (type.contains("PERCENT") || type.contains("PHẦN TRĂM")) {
            discountAmount = total * (currentPromoValue / 100.0);
            if (currentPromoMaxDiscount > 0 && discountAmount > currentPromoMaxDiscount)
                discountAmount = currentPromoMaxDiscount;
        } else {
            discountAmount = currentPromoValue;
        }
    }

    private void clearPromo() {
        currentPromoId = -1;
        currentPromoType = "";
        currentPromoValue = 0;
        currentPromoMaxDiscount = 0;
        discountAmount = 0;
    }

    private void handleQuantityChange(int rowIndex, int newQty) {
        try {
            CartItem item = cartBUS.getCartItems().get(rowIndex);
            CartDAO cartDAO = new CartDAO();
            if (newQty <= 0) {
                int ok = JOptionPane.showConfirmDialog(this,
                        "Xóa sản phẩm này khỏi giỏ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    cartDAO.removeFromCart(this.customerId, item.getSku());
                    cartBUS.removeItem(rowIndex);
                }
            } else {
                cartBUS.updateQuantity(rowIndex, newQty);
                cartDAO.updateCartItemQuantity(this.customerId, item.getSku(), newQty);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cảnh báo kho", JOptionPane.WARNING_MESSAGE);
        } finally {
            refreshCartGUI();
        }
    }

    public void refreshCartGUI() {
        isUpdatingTable = true;
        cartModel.setRowCount(0);
        for (CartItem item : cartBUS.getCartItems()) {
            String nameDisplay = item.getName();
            if (item.getStock() == 0) nameDisplay += " (Hết hàng)";
            cartModel.addRow(new Object[] {
                    item.isSelected() && item.getStock() > 0,
                    nameDisplay, item.getSize(), item.getColor(),
                    "−", item.getQuantity(), "+",
                    String.format("%,.0f", item.getPrice()),
                    String.format("%,.0f", item.getTotalPrice())
            });
        }
        double total = cartBUS.calculateTotalAmount();
        recalculateDiscount(total);
        double finalTotal = total - discountAmount;
        if (finalTotal < 0)
            finalTotal = 0;

        lblSubtotalInfo.setText("Tạm tính: " + String.format("%,.0f VNĐ", total));

        if (discountAmount > 0) {
            lblDiscountInfo.setText("Đã giảm: -" + String.format("%,.0f VNĐ", discountAmount));
            lblDiscountInfo.setVisible(true);
        } else {
            lblDiscountInfo.setVisible(false);
        }

        lblTotalAmount.setText(String.format("%,.0f VNĐ", finalTotal));

        isUpdatingTable = false;
        if (updateCartBadge != null)
            updateCartBadge.run();
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 18));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            setText(v == null ? "" : v.toString());
            setForeground(new Color(56, 189, 248));
            setBackground(new Color(239, 246, 255));
            setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            return this;
        }
    }

    private void refreshCouponList() {
        cbCoupon.removeAllItems();
        cbCoupon.addItem("-- Chọn mã giảm giá --");
        String sql = "SELECT cv.promo_code, p.discount_value, p.type FROM Customer_Voucher cv " +
                     "JOIN [Promotion] p ON cv.promo_code = p.code " +
                     "WHERE cv.customer_id = ? AND cv.is_used = 0 AND p.end_date >= GETDATE()";
        try (java.sql.Connection conn = com.pbl_3project.util.DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("promo_code");
                    String type = rs.getString("type");
                    double val = rs.getDouble("discount_value");
                    String display = code + " - Giảm " + ("Fixed".equals(type) ? String.format("%,.0fđ", val) : String.format("%,.0f%%", val));
                    cbCoupon.addItem(display);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleExchangeVoucher() {
        int currentPoints = 0;
        String name = "";
        try {
            java.util.Map<String, String> profile = new com.pbl_3project.dao.UserDAO().getCustomerProfile(customerId);
            if (profile != null) {
                if (profile.containsKey("reward_points")) {
                    currentPoints = Integer.parseInt(profile.get("reward_points"));
                }
                name = profile.getOrDefault("firstName", "") + " " + profile.getOrDefault("lastName", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        VoucherExchangeDialog dialog = new VoucherExchangeDialog((Frame) SwingUtilities.getWindowAncestor(this), customerId, name, currentPoints);
        dialog.setVisible(true);
        refreshCouponList();
    }
}