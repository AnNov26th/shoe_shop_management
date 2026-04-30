package com.pbl_3project.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import com.pbl_3project.bus.ProductBUS;

public class InventoryLookupPanel extends JPanel {
    private JPanel productGrid;
    private JTextField txtSearch;
    private ProductBUS productBUS = new ProductBUS();
    private int currentStaffId;

    public InventoryLookupPanel(int staffId) {
        this.currentStaffId = staffId;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initComponents();
        loadInventory("");
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("TRA CỨU TỒN KHO TRỰC TUYẾN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        topPanel.add(lblTitle, BorderLayout.NORTH);
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);
        JLabel lblSearch = new JLabel("Từ khóa:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtSearch = new JTextField(30);
        txtSearch.setPreferredSize(new Dimension(300, 40));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(0, 15, 0, 15)));
        JButton btnSearch = createPillButton("Kiểm tra kho", new Color(59, 190, 210), Color.WHITE);
        searchBar.add(lblSearch);
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        topPanel.add(searchBar, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        productGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        productGrid.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(productGrid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
        btnSearch.addActionListener(e -> loadInventory(txtSearch.getText()));
        txtSearch.addActionListener(e -> loadInventory(txtSearch.getText()));
    }

    private void loadInventory(String keyword) {
        try {
            productGrid.removeAll();
            DefaultTableModel model = productBUS.searchProductsAdmin(keyword);
            for (int i = 0; i < model.getRowCount(); i++) {
                int id = (int) model.getValueAt(i, 0);
                String name = model.getValueAt(i, 1).toString();
                double price = (double) model.getValueAt(i, 2);
                String color = model.getValueAt(i, 3).toString();
                int stock = (int) model.getValueAt(i, 4);
                productGrid.add(createInventoryCard(id, name, price, color, stock));
            }
            productGrid.revalidate();
            productGrid.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải kho: " + e.getMessage());
        }
    }

    private JPanel createInventoryCard(int id, String name, double price, String color, int stock) {
        JPanel shadow = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 20, 20);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 6, 20, 20);
                g2.dispose();
            }
        };
        shadow.setOpaque(false);
        shadow.setBorder(new EmptyBorder(0, 0, 6, 4));
        shadow.setPreferredSize(new Dimension(200, 300));
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        JPanel imgPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(248, 250, 252));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        imgPanel.setOpaque(false);
        imgPanel.setPreferredSize(new Dimension(0, 160));
        JLabel lblImg = new JLabel("", SwingConstants.CENTER);
        String colorEn = (color != null && color.contains("("))
                ? color.substring(color.lastIndexOf("(") + 1, color.lastIndexOf(")")).trim()
                : color;
        try {
            File file = new File(
                    "F:\\CNTT\\shoe_shop_management\\src\\main\\resources\\images\\" + name + " - " + colorEn + ".png");
            if (file.exists()) {
                BufferedImage originalImg = ImageIO.read(file);
                lblImg.setIcon(new ImageIcon(originalImg.getScaledInstance(140, 140, Image.SCALE_SMOOTH)));
            } else {
                lblImg.setText("👟");
                lblImg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            }
        } catch (Exception e) {
            lblImg.setText("Lỗi ảnh");
        }
        imgPanel.add(lblImg, BorderLayout.CENTER);
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(new EmptyBorder(10, 12, 12, 12));
        JLabel lblName = new JLabel("<html><div style='text-align:center'>" + name + "</div></html>");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setAlignmentX(CENTER_ALIGNMENT);
        JLabel lblStock = new JLabel("Tổng tồn: " + stock + " đôi");
        lblStock.setAlignmentX(CENTER_ALIGNMENT);
        if (stock <= 10) {
            lblStock.setForeground(new Color(239, 68, 68));
            lblStock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        } else {
            lblStock.setForeground(new Color(34, 197, 94));
            lblStock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        }
        JButton btnDetail = createPillButton("Chọn Size / Giữ chỗ", new Color(241, 245, 249), new Color(15, 23, 42));
        btnDetail.setAlignmentX(CENTER_ALIGNMENT);
        btnDetail.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            new ProductDetailDialog(parent, id, name, price, "STAFF", currentStaffId, null, null).setVisible(true);
        });
        info.add(lblName);
        info.add(Box.createVerticalStrut(5));
        info.add(lblStock);
        info.add(Box.createVerticalStrut(10));
        info.add(btnDetail);
        card.add(imgPanel, BorderLayout.CENTER);
        card.add(info, BorderLayout.SOUTH);
        shadow.add(card, BorderLayout.CENTER);
        return shadow;
    }

    private JButton createPillButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bgColor.darker() : bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fgColor);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        return btn;
    }
}
