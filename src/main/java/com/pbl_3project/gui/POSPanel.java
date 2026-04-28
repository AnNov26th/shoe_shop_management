package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
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

    // Khai báo biến ID
    private int staffId;

    // Timer để tự động clear expired items
    private Timer reserveTimer;

    // ĐÃ FIX: Hàm khởi tạo giờ nhận vào ID để hết lỗi đỏ
    public POSPanel(int staffId) {
        this.staffId = staffId;
        productBUS = new ProductBUS();
        cartBUS = new CartBUS();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initComponents();
        loadProductsFromDB();

        // ✅ Bắt đầu timer để check expired items mỗi 5 giây
        startReserveTimer();
    }

    private void initComponents() {
        // --- BÊN TRÁI: DANH SÁCH SẢN PHẨM ---
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

        // --- BÊN PHẢI: GIỎ HÀNG & THANH TOÁN ---
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
        lblTotal.setForeground(Color.BLACK); // ✅ Chữ đen

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

        // --- 1. NHẬP THÔNG TIN KHÁCH HÀNG ---
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

        // --- 2. LỰA CHỌN PHƯƠNG THỨC THANH TOÁN TẠI QUẦY ---
        String[] options = { "Tiền mặt (Hoàn thành ngay)", "Chuyển khoản / Quét mã QR" };
        int choice = JOptionPane.showOptionDialog(this,
                "Chọn phương thức thanh toán:", "Thanh toán",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == -1)
            return;

        boolean isPaymentConfirmed = false;
        String status = "Chưa thanh toán";

        if (choice == 1) { // Thanh toán QR
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
        } else { // Tiền mặt
            isPaymentConfirmed = true;
            status = "Hoàn thành"; // Tiền mặt tại quầy coi như hoàn thành luôn
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
            // Đối với POS Grid, chúng ta có thể cần một hàm search riêng trả về
            // BaseProducts
            // Hoặc tạm thời dùng searchAdmin vì nó trả về danh sách mẫu giày
            renderProductGrid(productBUS.searchProductsAdmin(keyword));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void renderProductGrid(DefaultTableModel model) {
        pnlProductGrid.removeAll();
        for (int i = 0; i < model.getRowCount(); i++) {
            int id = (int) model.getValueAt(i, 0);
            String name = (String) model.getValueAt(i, 1);
            double price = (double) model.getValueAt(i, 2);
            String color = (String) model.getValueAt(i, 3);
            String imageUrl = (String) model.getValueAt(i, model.getColumnCount() - 1); // Hình ảnh ở cột cuối
            pnlProductGrid.add(new ProductCard(id, name, price, color, imageUrl));
            pnlProductGrid.add(Box.createVerticalStrut(10));
        }
        pnlProductGrid.revalidate();
        pnlProductGrid.repaint();
    }

    private void handleAddToCart(String sku, String name, String size, String color, double price, int stock) {
        try {
            CartItem newItem = new CartItem(sku, name, size, color, price, 1, stock);
            int reserveMinutes = com.pbl_3project.util.ConfigUtils.getReserveDurationMinutes();
            newItem.setExpiresAt(LocalDateTime.now().plusMinutes(reserveMinutes));
            cartBUS.addItem(newItem);
            refreshCartGUI();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cảnh báo kho", JOptionPane.WARNING_MESSAGE);
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

    // --- INNER CLASS: PRODUCT CARD ---
    class ProductCard extends JPanel {
        private final int productId;
        private final String name;
        private final double basePrice;
        private final JPanel pnlVariants;
        private boolean isExpanded = false;

        public ProductCard(int id, String name, double price, String sampleColor, String imageUrl) {
            this.productId = id;
            this.name = name;
            this.basePrice = price;

            setLayout(new BorderLayout(15, 0));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                    new EmptyBorder(10, 15, 10, 15)));

            // Ảnh sản phẩm bên trái
            JLabel lblImage = new JLabel();
            lblImage.setPreferredSize(new Dimension(80, 80));
            lblImage.setHorizontalAlignment(JLabel.CENTER);
            lblImage.setBorder(BorderFactory.createLineBorder(new Color(241, 245, 249)));
            
            try {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    javax.swing.ImageIcon icon = new javax.swing.ImageIcon(imageUrl);
                    if (icon.getIconWidth() > 0) {
                        java.awt.Image img = icon.getImage().getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
                        lblImage.setIcon(new javax.swing.ImageIcon(img));
                    } else {
                        lblImage.setText("No Img");
                    }
                } else {
                    lblImage.setText("No Img");
                }
            } catch (Exception e) {
                lblImage.setText("Error");
            }
            add(lblImage, BorderLayout.WEST);

            // Container cho thông tin bên phải của ảnh
            JPanel pnlInfo = new JPanel(new BorderLayout());
            pnlInfo.setOpaque(false);

            // Header: Tên và giá
            JPanel pnlHeader = new JPanel(new BorderLayout());
            pnlHeader.setOpaque(false);

            JLabel lblName = new JLabel(name);
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblName.setForeground(new Color(30, 41, 59));

            JLabel lblPrice = new JLabel(String.format("%,.0f VNĐ", price));
            lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblPrice.setForeground(new Color(59, 130, 246));

            pnlHeader.add(lblName, BorderLayout.CENTER);
            pnlHeader.add(lblPrice, BorderLayout.EAST);
            pnlInfo.add(pnlHeader, BorderLayout.NORTH);

            // Vùng chi tiết (Variants)
            pnlVariants = new JPanel(new GridLayout(0, 4, 5, 5));
            pnlVariants.setOpaque(false);
            pnlVariants.setVisible(false);
            pnlVariants.setBorder(new EmptyBorder(10, 0, 0, 0));
            pnlInfo.add(pnlVariants, BorderLayout.CENTER);
            
            add(pnlInfo, BorderLayout.CENTER);

            // Sự kiện nhấn để mở rộng
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleExpand();
                }
            });
        }

        private void toggleExpand() {
            isExpanded = !isExpanded;
            pnlVariants.setVisible(isExpanded);
            if (isExpanded && pnlVariants.getComponentCount() == 0) {
                loadVariants();
            }

            revalidate();
            pnlProductGrid.revalidate();
        }

        private void loadVariants() {
            try {
                DefaultTableModel model = productBUS.getVariantsByProductId(productId);
                for (int i = 0; i < model.getRowCount(); i++) {
                    String sku = (String) model.getValueAt(i, 0);
                    String size = (String) model.getValueAt(i, 1);
                    String color = (String) model.getValueAt(i, 2);
                    int stock = (int) model.getValueAt(i, 3);

                    JButton btnVar = new JButton(
                            "<html><center>" + size + "<br><small>" + color + "</small></center></html>");
                    btnVar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    btnVar.setToolTipText("Kho: " + stock);
                    btnVar.addActionListener(e -> handleAddToCart(sku, name, size, color, basePrice, stock));
                    pnlVariants.add(btnVar);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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

    // ✅ Bắt đầu Timer để check expired items
    private void startReserveTimer() {
        reserveTimer = new Timer(true); // Daemon thread

        // Chạy mỗi 5 giây để check các item hết hạn
        reserveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkExpiredItems();
            }
        }, 5000, 5000); // Bắt đầu sau 5 giây, chạy mỗi 5 giây
    }

    // ✅ Kiểm tra và xóa các item hết hạn
    private void checkExpiredItems() {
        try {
            boolean hasExpired = false;

            // Duyệt qua giỏ hàng và tìm các item hết hạn
            for (int i = cartBUS.getCartItems().size() - 1; i >= 0; i--) {
                CartItem item = cartBUS.getCartItems().get(i);

                if (item.isExpired()) {
                    System.out.println("⏰ Item hết hạn: " + item.getName() + " | Xóa khỏi giỏ hàng");
                    cartBUS.removeItem(i);
                    hasExpired = true;
                }
            }

            // Cập nhật UI nếu có item bị xóa
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