package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

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

    private JTable tableProducts;
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
        leftPanel.setBackground(new Color(245, 245, 245));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "□ DANH SÁCH SẢN PHẨM",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13)));

        tableProducts = new JTable();
        tableProducts.setRowHeight(32);
        tableProducts.setFont(new Font("Arial", Font.PLAIN, 12));
        tableProducts.getTableHeader().setBackground(new Color(59, 190, 210));
        tableProducts.getTableHeader().setForeground(Color.BLACK);
        tableProducts.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        leftPanel.add(new JScrollPane(tableProducts), BorderLayout.CENTER);

        JButton btnAddToCart = new JButton("[+] THÊM VÀO GIỎ");
        btnAddToCart.setBackground(new Color(52, 152, 219));
        btnAddToCart.setForeground(Color.BLACK);
        btnAddToCart.setFont(new Font("Arial", Font.BOLD, 12));
        btnAddToCart.setFocusPainted(false);
        btnAddToCart.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnAddToCart.addActionListener(e -> handleAddToCart());

        JPanel pnlAdd = new JPanel();
        pnlAdd.setBackground(new Color(245, 245, 245));
        pnlAdd.add(btnAddToCart);
        leftPanel.add(pnlAdd, BorderLayout.SOUTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(250, 250, 250));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        searchPanel.add(new JLabel("� TÌM KIẾM: "));
        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 12));
        searchPanel.add(txtSearch);

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                handleSearch();
            }
        });

        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // --- BÊN PHẢI: GIỎ HÀNG & THANH TOÁN ---
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(550, 0));
        rightPanel.setBackground(new Color(245, 245, 245));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "□ GIỎ HÀNG & THANH TOÁN",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13)));

        String[] cartColumns = { "Tên SP", "Size", " - ", "SL", " + ", "Đơn giá", "Thành tiền" };
        cartModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        tableCart = new JTable(cartModel);
        tableCart.setRowHeight(35);
        tableCart.setFont(new Font("Arial", Font.PLAIN, 12));
        tableCart.getTableHeader().setBackground(new Color(59, 190, 210));
        tableCart.getTableHeader().setForeground(Color.BLACK);
        tableCart.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

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
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setForeground(Color.BLACK); // ✅ Chữ đen

        lblTotalAmount = new JLabel("0 VNĐ");
        lblTotalAmount.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalAmount.setForeground(new Color(255, 80, 80));

        totalPanel.add(lblTotal, BorderLayout.WEST);
        totalPanel.add(lblTotalAmount, BorderLayout.EAST);

        JButton btnCheckout = new JButton("[THANH TOÁN]");
        btnCheckout.setBackground(new Color(46, 204, 113));
        btnCheckout.setForeground(Color.BLACK); // ✅ Chữ trắng
        btnCheckout.setFont(new Font("Arial", Font.BOLD, 13));
        btnCheckout.setFocusPainted(false);
        btnCheckout.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton btnClear = new JButton("[XÓA GIỎ HÀNG]");
        btnClear.setForeground(Color.BLACK); // ✅ Chữ trắng
        btnClear.setBackground(new Color(255, 80, 80)); // ✅ Nền đỏ
        btnClear.setFont(new Font("Arial", Font.BOLD, 13));
        btnClear.setFocusPainted(false);
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

    private void loadProductsFromDB() {
        try {
            tableProducts.setModel(productBUS.getProductsForPOS());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleAddToCart() {
        int row = tableProducts.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm bên trái!");
            return;
        }

        try {
            String sku = tableProducts.getValueAt(row, 0).toString();
            String name = tableProducts.getValueAt(row, 1).toString();
            String size = tableProducts.getValueAt(row, 2).toString();
            String color = tableProducts.getValueAt(row, 3).toString();
            double price = Double.parseDouble(tableProducts.getValueAt(row, 4).toString().replaceAll("[^\\d]", ""));
            int stock = Integer.parseInt(tableProducts.getValueAt(row, 5).toString());

            // ✅ Tạo CartItem và set expires_at
            CartItem newItem = new CartItem(sku, name, size, color, price, 1, stock);

            // Tính toán thời gian hết hạn (hiện tại + khoảng thời gian giữ chỗ)
            int reserveMinutes = ConfigUtils.getReserveDurationMinutes();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(reserveMinutes);
            newItem.setExpiresAt(expiresAt);

            // Thêm vào giỏ
            cartBUS.addItem(newItem);

            // Log thông tin
            System.out.println("✅ Thêm vào giỏ: " + name + " | Hết hạn lúc: " + expiresAt);

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
                    item.getName(),
                    item.getSize(),
                    "-",
                    item.getQuantity(),
                    "+",
                    String.format("%,.0f", item.getPrice()),
                    String.format("%,.0f", item.getTotalPrice())
            });
        }

        lblTotalAmount.setText(String.format("%,.0f VNĐ", cartBUS.calculateTotalAmount()));
        isUpdatingTable = false;
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Arial", Font.BOLD, 16));
            setBackground(new Color(220, 220, 220));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private void handleSearch() {
        String keyword = txtSearch.getText();
        try {
            tableProducts.setModel(productBUS.searchProductsForPOS(keyword));
            tableProducts.getColumnModel().getColumn(0).setPreferredWidth(100);
            tableProducts.getColumnModel().getColumn(1).setPreferredWidth(180);
        } catch (SQLException e) {
            e.printStackTrace();
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

    // ✅ Dừng Timer khi đóng panel
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