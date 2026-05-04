package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.pbl_3project.bus.CartBUS;
import com.pbl_3project.bus.ProductBUS;

public class CustomerShopPanel extends JPanel {
    private JPanel productGrid;
    private ProductBUS productBUS;
    private JLabel lblGoiY;
    private CartBUS cartBUS;
    private Runnable updateCartBadge;
    private int customerId;
    private static final Color BG = new Color(248, 250, 252);
    private static final Color ACCENT = new Color(56, 189, 248);
    private static final Color ACCENT2 = new Color(255, 107, 74);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_C = new Color(226, 232, 240);
    private static final Color TEXT_H = new Color(15, 23, 42);
    private static final Color TEXT_S = new Color(100, 116, 139);

    public CustomerShopPanel(int customerId, CartBUS cartBUS, Runnable updateCartBadge) {
        this.customerId = customerId;
        this.productBUS = new ProductBUS();
        this.cartBUS = cartBUS;
        this.updateCartBadge = updateCartBadge;
        setLayout(new BorderLayout());
        setBackground(BG);
        initComponents();
        loadProductsFromDB("Tất cả");
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(CARD_BG);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C),
                new EmptyBorder(16, 28, 16, 28)));
        lblGoiY = new JLabel("Tất cả sản phẩm");
        lblGoiY.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblGoiY.setForeground(TEXT_H);
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setBackground(CARD_BG);
        JButton btnAll = createFilterBtn("Tất cả", true);
        JButton btnMen = createFilterBtn("Nam", false);
        JButton btnWomen = createFilterBtn("Nữ", false);
        JButton btnUnisex = createFilterBtn("Unisex", false);
        JButton[] filters = { btnAll, btnMen, btnWomen, btnUnisex };
        filterRow.add(btnAll);
        filterRow.add(btnMen);
        filterRow.add(btnWomen);
        filterRow.add(btnUnisex);
        topPanel.add(lblGoiY, BorderLayout.WEST);
        topPanel.add(filterRow, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        productGrid = new JPanel(new GridLayout(0, 4, 18, 18));
        productGrid.setBackground(BG);
        productGrid.setBorder(new EmptyBorder(24, 28, 24, 28));
        JScrollPane scroll = new JScrollPane(productGrid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);
        btnAll.addActionListener(e -> {
            setActiveFilter(btnAll, filters);
            lblGoiY.setText("Tất cả sản phẩm");
            loadProductsFromDB("Tất cả");
        });
        btnMen.addActionListener(e -> {
            setActiveFilter(btnMen, filters);
            lblGoiY.setText("Giày Nam");
            loadProductsFromDB("Nam");
        });
        btnWomen.addActionListener(e -> {
            setActiveFilter(btnWomen, filters);
            lblGoiY.setText("Giày Nữ");
            loadProductsFromDB("Nữ");
        });
        btnUnisex.addActionListener(e -> {
            setActiveFilter(btnUnisex, filters);
            lblGoiY.setText("Giày Unisex");
            loadProductsFromDB("Unisex");
        });
    }

    private JButton createFilterBtn(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (Boolean.TRUE.equals(getClientProperty("active"))) {
                    g2.setColor(ACCENT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(56, 189, 248, 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.setColor(ACCENT);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                } else {
                    g2.setColor(CARD_BG);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.setColor(BORDER_C);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.putClientProperty("active", active);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(active ? Color.WHITE : TEXT_H);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 20, 7, 20));
        return btn;
    }

    private void setActiveFilter(JButton active, JButton[] all) {
        for (JButton b : all) {
            b.putClientProperty("active", false);
            b.setForeground(TEXT_H);
        }
        active.putClientProperty("active", true);
        active.setForeground(Color.WHITE);
        for (JButton b : all)
            b.repaint();
    }

    private void loadProductsFromDB(String gender) {
        try {
            productGrid.removeAll();
            DefaultTableModel model = productBUS.getBaseProducts(gender);
            if (model.getRowCount() == 0) {
                JLabel empty = new JLabel("Không có sản phẩm", SwingConstants.CENTER);
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                empty.setForeground(TEXT_S);
                productGrid.setLayout(new BorderLayout());
                productGrid.add(empty, BorderLayout.CENTER);
            } else {
                productGrid.setLayout(new GridLayout(0, 4, 18, 18));
                for (int i = 0; i < model.getRowCount(); i++) {
                    int id = Integer.parseInt(model.getValueAt(i, 0).toString());
                    String name = model.getValueAt(i, 1).toString();
                    double price = Double.parseDouble(model.getValueAt(i, 2).toString());
                    String color = model.getValueAt(i, 3).toString();
                    String stock = model.getValueAt(i, 4).toString();
                    productGrid.add(createCard(id, name, String.format("%,.0f VNĐ", price), price, color, stock));
                }
            }
            productGrid.revalidate();
            productGrid.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải sản phẩm: " + e.getMessage());
        }
    }

    private JPanel createCard(int productId, String name, String priceStr, double rawPrice, String sampleColor,
            String stock) {
        JPanel shadow = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 6, 18, 18);
                g2.setColor(CARD_BG);
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
        imgPanel.setPreferredSize(new Dimension(0, 180));
        JLabel lblImg = new JLabel("", SwingConstants.CENTER);
        try {
            javax.swing.ImageIcon productIcon = com.pbl_3project.util.IconUtils.findProductImage(name, 160, 160);
            if (productIcon != null) {
                lblImg.setIcon(productIcon);
            } else {
                lblImg.setText("👟");
                lblImg.setFont(new Font("Segoe UI", Font.PLAIN, 56));
            }
        } catch (Exception ignored) {
            lblImg.setText("👟");
            lblImg.setFont(new Font("Segoe UI", Font.PLAIN, 56));
        }
        imgPanel.add(lblImg, BorderLayout.CENTER);
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(12, 14, 14, 14));
        JLabel lblName = new JLabel("<html><div style='text-align:center'>" + name + "</div></html>");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(TEXT_H);
        lblName.setAlignmentX(CENTER_ALIGNMENT);
        JLabel lblStock = new JLabel("Còn " + stock + " đôi", SwingConstants.CENTER);
        lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStock.setForeground(TEXT_S);
        lblStock.setAlignmentX(CENTER_ALIGNMENT);
        JLabel lblPrice = new JLabel(priceStr, SwingConstants.CENTER);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPrice.setForeground(ACCENT2);
        lblPrice.setAlignmentX(CENTER_ALIGNMENT);
        JButton btnView = new JButton("Xem chi tiết") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, ACCENT, getWidth(), 0, new Color(99, 210, 255)));
                } else {
                    g2.setColor(new Color(56, 189, 248, 200));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnView.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnView.setForeground(new Color(10, 18, 40));
        btnView.setContentAreaFilled(false);
        btnView.setBorderPainted(false);
        btnView.setFocusPainted(false);
        btnView.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnView.setBorder(new EmptyBorder(8, 0, 8, 0));
        btnView.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnView.setAlignmentX(CENTER_ALIGNMENT);
        btnView.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            ProductDetailDialog dialog = new ProductDetailDialog(
                    parent, productId, name, rawPrice, "CUSTOMER", this.customerId, cartBUS, updateCartBadge);
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

    private String extractEnglishColor(String fullColor) {
        if (fullColor != null && fullColor.contains("(") && fullColor.contains(")")) {
            return fullColor.substring(fullColor.lastIndexOf("(") + 1, fullColor.lastIndexOf(")")).trim();
        }
        return fullColor;
    }
}
