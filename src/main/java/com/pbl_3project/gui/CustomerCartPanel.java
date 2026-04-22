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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.pbl_3project.bus.CartBUS;
import com.pbl_3project.dto.CartItem;

public class CustomerCartPanel extends JPanel {

    private JTable tableCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotalAmount;
    private CartBUS cartBUS;
    private Runnable updateCartBadge;
    private boolean isUpdatingTable = false;
    private int customerId; // LƯU ID CỦA KHÁCH TỪ BÊN NGOÀI

    private static final Color BG = new Color(248, 250, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color ACCENT2 = new Color(255, 107, 74);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT_H = new Color(15, 23, 42);
    private static final Color TEXT_S = new Color(100, 116, 139);

    // ĐÃ SỬA CONSTRUCTOR
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

        String[] cols = { "Sản phẩm", "Size", "Màu sắc", " − ", "SL", " + ", "Đơn giá", "Thành tiền" };
        cartModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 4;
            }
        };

        tableCart = new JTable(cartModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? WHITE : new Color(248, 250, 252));
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
        tableCart.getColumnModel().getColumn(0).setPreferredWidth(220);
        tableCart.getColumnModel().getColumn(1).setPreferredWidth(60);
        tableCart.getColumnModel().getColumn(2).setPreferredWidth(130);
        tableCart.getColumnModel().getColumn(3).setPreferredWidth(44);
        tableCart.getColumnModel().getColumn(3).setCellRenderer(btnRenderer);
        tableCart.getColumnModel().getColumn(4).setPreferredWidth(60);
        tableCart.getColumnModel().getColumn(4).setCellRenderer(center);
        tableCart.getColumnModel().getColumn(5).setPreferredWidth(44);
        tableCart.getColumnModel().getColumn(5).setCellRenderer(btnRenderer);
        tableCart.getColumnModel().getColumn(6).setPreferredWidth(120);
        tableCart.getColumnModel().getColumn(6).setCellRenderer(center);
        tableCart.getColumnModel().getColumn(7).setPreferredWidth(130);
        tableCart.getColumnModel().getColumn(7).setCellRenderer(center);

        tableCart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableCart.rowAtPoint(e.getPoint());
                int col = tableCart.columnAtPoint(e.getPoint());
                if (row >= 0) {
                    if (tableCart.isEditing())
                        tableCart.getCellEditor().stopCellEditing();
                    int qty = cartBUS.getCartItems().get(row).getQuantity();
                    if (col == 3)
                        handleQuantityChange(row, qty - 1);
                    else if (col == 5)
                        handleQuantityChange(row, qty + 1);
                }
            }
        });

        cartModel.addTableModelListener(e -> {
            if (!isUpdatingTable
                    && e.getType() == javax.swing.event.TableModelEvent.UPDATE
                    && e.getColumn() == 4) {
                int row = e.getFirstRow();
                try {
                    int newQty = Integer.parseInt(cartModel.getValueAt(row, 4).toString());
                    handleQuantityChange(row, newQty);
                } catch (NumberFormatException ex) {
                    refreshCartGUI();
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
                new EmptyBorder(20, 32, 24, 32)));
        footer.setOpaque(false);

        JPanel totalRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        totalRow.setOpaque(false);

        JLabel lblTxt = new JLabel("TỔNG THANH TOÁN:");
        lblTxt.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTxt.setForeground(TEXT_S);

        lblTotalAmount = new JLabel("0 VNĐ");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotalAmount.setForeground(ACCENT2);

        totalRow.add(lblTxt);
        totalRow.add(lblTotalAmount);

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
        if (cartBUS.getCartItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng của bạn đang trống!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String phone = JOptionPane.showInputDialog(this, "Vui lòng nhập Số điện thoại nhận hàng:");
        if (phone == null || phone.trim().isEmpty()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận đặt hàng với tổng tiền: " + lblTotalAmount.getText() + "?",
                "Xác nhận đơn hàng", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // ĐÃ SỬA: SỬ DỤNG TRỰC TIẾP ID ĐÃ TRUYỀN VÀO TỪ LÚC ĐĂNG NHẬP
                com.pbl_3project.dao.OrderDAO orderDAO = new com.pbl_3project.dao.OrderDAO();
                boolean success = orderDAO.createOrderOnline(this.customerId, phone, cartBUS.calculateTotalAmount(),
                        cartBUS.getCartItems());

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "🎉 Đặt hàng thành công!\nĐơn hàng của bạn đang chờ xác nhận với trạng thái: Chưa thanh toán.");

                    new com.pbl_3project.dao.CartDAO().clearCartByUserId(this.customerId);

                    cartBUS.clearCart();
                    refreshCartGUI();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đặt hàng", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleQuantityChange(int rowIndex, int newQty) {
        try {
            if (newQty <= 0) {
                int ok = JOptionPane.showConfirmDialog(this,
                        "Xóa sản phẩm này khỏi giỏ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION)
                    cartBUS.removeItem(rowIndex);
            } else {
                cartBUS.updateQuantity(rowIndex, newQty);
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
            cartModel.addRow(new Object[] {
                    item.getName(), item.getSize(), item.getColor(),
                    "−", item.getQuantity(), "+",
                    String.format("%,.0f", item.getPrice()),
                    String.format("%,.0f", item.getTotalPrice())
            });
        }
        lblTotalAmount.setText(String.format("%,.0f VNĐ", cartBUS.calculateTotalAmount()));
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
}