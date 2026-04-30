package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import com.pbl_3project.bus.CartBUS;
import com.pbl_3project.bus.ProductBUS;
import com.pbl_3project.dto.CartItem;
import com.pbl_3project.util.ConfigUtils;

public class POSPanel extends JPanel {
    private JPanel pnlProductGrid;
    private JTable tableCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotalAmount;
    private JTextField txtSearch;
    private ProductBUS productBUS;
    private CartBUS cartBUS;
    private boolean isUpdatingTable = false;
    private int staffId;
    private Timer reserveTimer;

    public POSPanel(int staffId) {
        this.staffId = staffId;
        productBUS = new ProductBUS();
        cartBUS = new CartBUS();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initComponents();
        loadProductsFromDB();
        startReserveTimer();
    }

    private void initComponents() {
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 10, 10, 10)));
        JLabel lblLeftTitle = new JLabel("DANH MỤC SẢN PHẨM");
        lblLeftTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLeftTitle.setForeground(new Color(30, 41, 59));
        lblLeftTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        leftPanel.add(lblLeftTitle, BorderLayout.NORTH);
        pnlProductGrid = new JPanel();
        pnlProductGrid.setLayout(new GridLayout(0, 2, 10, 10));
        pnlProductGrid.setBackground(Color.WHITE);
        JScrollPane scrollGrid = new JScrollPane(pnlProductGrid);
        scrollGrid.setBorder(null);
        scrollGrid.getVerticalScrollBar().setUnitIncrement(16);
        leftPanel.add(scrollGrid, BorderLayout.CENTER);
        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setOpaque(false);
        searchWrapper.add(lblLeftTitle, BorderLayout.WEST);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("🔍 Tìm: "));
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(txtSearch);
        searchWrapper.add(searchPanel, BorderLayout.EAST);
        leftPanel.add(searchWrapper, BorderLayout.NORTH);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                handleSearch();
            }
        });
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(550, 0));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 10, 10, 10)));
        JLabel lblRightTitle = new JLabel("GIỎ HÀNG CHỜ");
        lblRightTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRightTitle.setForeground(new Color(30, 41, 59));
        lblRightTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        rightPanel.add(lblRightTitle, BorderLayout.NORTH);
        String[] cartColumns = { "Tên SP", "Size", " - ", "SL", " + ", "Đơn giá", "Thành tiền" };
        cartModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        tableCart = new JTable(cartModel);
        tableCart.setRowHeight(35);
        tableCart.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableCart.getTableHeader().setBackground(new Color(59, 190, 210));
        tableCart.getTableHeader().setForeground(Color.BLACK);
        tableCart.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableCart.getColumnModel().getColumn(1).setPreferredWidth(50);
        tableCart.getColumnModel().getColumn(2).setPreferredWidth(40);
        tableCart.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        tableCart.getColumnModel().getColumn(3).setPreferredWidth(50);
        tableCart.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tableCart.getColumnModel().getColumn(4).setPreferredWidth(40);
        tableCart.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        tableCart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableCart.rowAtPoint(e.getPoint());
                int col = tableCart.columnAtPoint(e.getPoint());
                if (row >= 0) {
                    if (tableCart.isEditing())
                        tableCart.getCellEditor().stopCellEditing();
                    int currentQty = cartBUS.getCartItems().get(row).getQuantity();
                    if (col == 2)
                        handleQuantityChange(row, currentQty - 1);
                    else if (col == 4)
                        handleQuantityChange(row, currentQty + 1);
                }
            }
        });
        cartModel.addTableModelListener(e -> {
            if (!isUpdatingTable && e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 3) {
                int row = e.getFirstRow();
                try {
                    int newQty = Integer.parseInt(cartModel.getValueAt(row, 3).toString());
                    handleQuantityChange(row, newQty);
                } catch (NumberFormatException ex) {
                    refreshCartGUI();
                }
            }
        });
        JPanel checkoutPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        checkoutPanel.setBackground(new Color(245, 245, 245));
        checkoutPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(new Color(245, 245, 245));
        JLabel lblTotal = new JLabel("TỔNG TIỀN: ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(Color.BLACK);
        lblTotalAmount = new JLabel("0 VNĐ");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotalAmount.setForeground(new Color(255, 80, 80));
        totalPanel.add(lblTotal, BorderLayout.WEST);
        totalPanel.add(lblTotalAmount, BorderLayout.EAST);
        JButton btnCheckout = new JButton("[THANH TOÁN]") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = new Color(46, 204, 113);
                Color c2 = new Color(39, 174, 96);
                GradientPaint gp = getModel().isRollover()
                        ? new GradientPaint(0, 0, c2, getWidth(), 0, c1)
                        : new GradientPaint(0, 0, c1, getWidth(), 0, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCheckout.setForeground(Color.BLACK);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCheckout.setContentAreaFilled(false);
        btnCheckout.setBorderPainted(false);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCheckout.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btnCheckout.addActionListener(e -> handleCheckout());
        JButton btnClear = new JButton("[XÓA GIỎ HÀNG]") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = new Color(255, 80, 80);
                Color c2 = new Color(231, 76, 60);
                GradientPaint gp = getModel().isRollover()
                        ? new GradientPaint(0, 0, c2, getWidth(), 0, c1)
                        : new GradientPaint(0, 0, c1, getWidth(), 0, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnClear.setForeground(Color.BLACK);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setContentAreaFilled(false);
        btnClear.setBorderPainted(false);
        btnClear.setFocusPainted(false);
        btnClear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btnClear.addActionListener(e -> {
            cartBUS.clearCart();
            refreshCartGUI();
        });
        checkoutPanel.add(totalPanel);
        checkoutPanel.add(btnCheckout);
        checkoutPanel.add(btnClear);
        rightPanel.add(new JScrollPane(tableCart), BorderLayout.CENTER);
        rightPanel.add(checkoutPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private void handleCheckout() {
        if (cartBUS.getCartItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CustomerInfoDialog infoDialog = new CustomerInfoDialog(
                (Frame) javax.swing.SwingUtilities.getWindowAncestor(this));
        infoDialog.setVisible(true);
        if (!infoDialog.isConfirmed())
            return;
        String customerInfo = infoDialog.getCustomerId() > 0 ? String.valueOf(infoDialog.getCustomerId())
                : infoDialog.getCustomerPhone();
        if (customerInfo == null || customerInfo.trim().isEmpty()) {
            customerInfo = "Khách vãng lai";
        }
        double totalAmount = cartBUS.calculateTotalAmount();
        String[] options = { "Tiền mặt (Hoàn thành ngay)", "Chuyển khoản / Quét mã QR" };
        int choice = JOptionPane.showOptionDialog(this,
                "Chọn phương thức thanh toán:", "Thanh toán",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (choice == -1)
            return;
        boolean isPaymentConfirmed = false;
        String status = "Chưa thanh toán";
        if (choice == 1) {
            PaymentQRDialog qrDialog = new PaymentQRDialog((Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
                    totalAmount, "POS_" + System.currentTimeMillis() % 10000);
            qrDialog.setVisible(true);
            if (qrDialog.isPaymentSuccessful()) {
                isPaymentConfirmed = true;
                status = "Đã thanh toán";
            } else {
                JOptionPane.showMessageDialog(this, "Thanh toán đã bị hủy.", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } else {
            isPaymentConfirmed = true;
            status = "Hoàn thành";
        }
        if (isPaymentConfirmed) {
            try {
                com.pbl_3project.dao.OrderDAO orderDAO = new com.pbl_3project.dao.OrderDAO();
                boolean success = orderDAO.createOrder(customerInfo, totalAmount, cartBUS.getCartItems(), status);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "🎉 " + (choice == 1 ? "Thanh toán QR" : "Thanh toán tiền mặt") + " thành công!");
                    cartBUS.clearCart();
                    refreshCartGUI();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi thanh toán",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadProductsFromDB() {
        try {
            renderProductGrid(productBUS.getBaseProducts("Tất cả"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleSearch() {
        String keyword = txtSearch.getText();
        try {
            renderProductGrid(productBUS.searchProductsAdmin(keyword));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleQuantityChange(int rowIndex, int newQty) {
        try {
            if (newQty <= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Xóa sản phẩm này?", "Xác nhận",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    cartBUS.removeItem(rowIndex);
                }
            } else {
                cartBUS.updateQuantity(rowIndex, newQty);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cảnh báo kho", JOptionPane.WARNING_MESSAGE);
        } finally {
            refreshCartGUI();
        }
    }

    private void refreshCartGUI() {
        isUpdatingTable = true;
        cartModel.setRowCount(0);
        for (CartItem item : cartBUS.getCartItems()) {
            cartModel.addRow(new Object[] {
                    item.getName(), item.getSize(), "-", item.getQuantity(), "+",
                    String.format("%,.0f", item.getPrice()),
                    String.format("%,.0f", item.getTotalPrice())
            });
        }
        lblTotalAmount.setText(String.format("%,.0f VNĐ", cartBUS.calculateTotalAmount()));
        isUpdatingTable = false;
    }

    private void renderProductGrid(DefaultTableModel model) {
        pnlProductGrid.removeAll();
        pnlProductGrid.setLayout(new GridLayout(0, 3, 15, 15));
        pnlProductGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < model.getRowCount(); i++) {
            int id = (int) model.getValueAt(i, 0);
            String name = (String) model.getValueAt(i, 1);
            double price = (double) model.getValueAt(i, 2);
            String color = (String) model.getValueAt(i, 3);
            int stock = (int) model.getValueAt(i, 4);
            String imageUrl = (String) model.getValueAt(i, model.getColumnCount() - 1);
            pnlProductGrid.add(createCard(id, name, price, color, stock, imageUrl));
        }
        pnlProductGrid.revalidate();
        pnlProductGrid.repaint();
    }

    private JPanel createCard(int productId, String name, double price, String sampleColor, int stock,
            String imageUrl) {
        JPanel shadow = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 6, 18, 18);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 6, 18, 18);
                g2.dispose();
            }
        };
        shadow.setOpaque(false);
        shadow.setBorder(new EmptyBorder(0, 0, 8, 4));

        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);

        JPanel imgPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        imgPanel.setOpaque(false);
        imgPanel.setPreferredSize(new Dimension(0, 150));

        JLabel lblImg = new JLabel("", SwingConstants.CENTER);
        try {
            boolean imageLoaded = false;
            if (imageUrl != null && !imageUrl.isEmpty()) {
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(imageUrl);
                if (icon.getIconWidth() > 0) {
                    java.awt.Image img = icon.getImage().getScaledInstance(130, 130, java.awt.Image.SCALE_SMOOTH);
                    lblImg.setIcon(new javax.swing.ImageIcon(img));
                    imageLoaded = true;
                }
            }
            if (!imageLoaded) {
                javax.swing.Icon defaultIcon = com.pbl_3project.util.IconUtils
                        .loadLargeIcon(com.pbl_3project.util.IconUtils.IconType.SHOE);
                if (defaultIcon != null) {
                    lblImg.setIcon(defaultIcon);
                } else {
                    lblImg.setText("👟");
                    lblImg.setFont(new Font("Segoe UI", Font.PLAIN, 56));
                }
            }
        } catch (Exception e) {
            lblImg.setText("👟");
            lblImg.setFont(new Font("Segoe UI", Font.PLAIN, 56));
        }
        imgPanel.add(lblImg, BorderLayout.CENTER);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblName = new JLabel("<html><div style='text-align:center'>" + name + "</div></html>");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(new Color(30, 41, 59));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblStock = new JLabel("Tồn kho: " + stock, SwingConstants.CENTER);
        lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStock.setForeground(new Color(100, 116, 139));
        lblStock.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPrice = new JLabel(String.format("%,.0f VNĐ", price), SwingConstants.CENTER);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPrice.setForeground(new Color(238, 77, 45));
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnView = new JButton("Thêm / Xem Chi Tiết") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setPaint(
                            new GradientPaint(0, 0, new Color(16, 185, 129), getWidth(), 0, new Color(52, 211, 153)));
                } else {
                    g2.setColor(new Color(16, 185, 129, 220));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnView.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnView.setForeground(Color.WHITE);
        btnView.setContentAreaFilled(false);
        btnView.setBorderPainted(false);
        btnView.setFocusPainted(false);
        btnView.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnView.setBorder(new EmptyBorder(8, 0, 8, 0));
        btnView.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnView.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnView.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            ProductDetailDialog dialog = new ProductDetailDialog(
                    parent, productId, name, price, "POS", this.staffId, cartBUS, () -> refreshCartGUI());
            dialog.setVisible(true);
        });

        info.add(lblName);
        info.add(Box.createVerticalStrut(4));
        info.add(lblStock);
        info.add(Box.createVerticalStrut(6));
        info.add(lblPrice);
        info.add(Box.createVerticalStrut(10));
        info.add(btnView);

        card.add(imgPanel, BorderLayout.CENTER);
        card.add(info, BorderLayout.SOUTH);
        shadow.add(card, BorderLayout.CENTER);

        return shadow;
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setBackground(new Color(220, 220, 220));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private void startReserveTimer() {
        reserveTimer = new Timer(true);
        reserveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkExpiredItems();
            }
        }, 5000, 5000);
    }

    private void checkExpiredItems() {
        try {
            boolean hasExpired = false;
            for (int i = cartBUS.getCartItems().size() - 1; i >= 0; i--) {
                CartItem item = cartBUS.getCartItems().get(i);
                if (item.isExpired()) {
                    System.out.println("⏰ Item hết hạn: " + item.getName() + " | Xóa khỏi giỏ hàng");
                    cartBUS.removeItem(i);
                    hasExpired = true;
                }
            }
            if (hasExpired) {
                javax.swing.SwingUtilities.invokeLater(() -> refreshCartGUI());
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi check expired items: " + e.getMessage());
        }
    }

    @Override
    public void finalize() throws Throwable {
        try {
            if (reserveTimer != null) {
                reserveTimer.cancel();
            }
        } finally {
            super.finalize();
        }
    }
}
