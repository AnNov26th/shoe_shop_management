package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.pbl_3project.bus.ProductBUS;

public class InventoryLookupPanel extends JPanel {
    private JPanel productGrid;
    private JTextField txtSearch;
    private ProductBUS productBUS = new ProductBUS();

    public InventoryLookupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadInventory("");
    }

    private void initComponents() {
        // --- Thanh tìm kiếm ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("TRA CỨU TỒN KHO TRỰC TUYẾN", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Cambria", Font.BOLD, 22));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        searchBar.setOpaque(false);

        txtSearch = new JTextField(30);
        txtSearch.setPreferredSize(new Dimension(300, 35));
        JButton btnSearch = new JButton("Kiểm tra kho");
        btnSearch.setBackground(new Color(59, 190, 210));
        btnSearch.setForeground(Color.BLACK); // Chữ đen

        searchBar.add(new JLabel("Nhập tên giày hoặc hãng:"));
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);

        topPanel.add(searchBar, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Lưới hiển thị ---
        productGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        productGrid.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(productGrid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Sự kiện
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
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setPreferredSize(new Dimension(200, 290));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // --- HÌNH ẢNH SẢN PHẨM ---
        JLabel lblImg = new JLabel("Đang tải...", SwingConstants.CENTER);
        lblImg.setPreferredSize(new Dimension(180, 160));

        // Tách lấy màu tiếng Anh trong ngoặc (VD: Đỏ (Red) -> Red)
        String colorEn = "";
        if (color != null && color.contains("(") && color.contains(")")) {
            colorEn = color.substring(color.lastIndexOf("(") + 1, color.lastIndexOf(")")).trim();
        }

        try {
            String fileName = name + " - " + colorEn + ".png";
            String absolutePath = "F:\\CNTT\\shoe_shop_management\\src\\main\\resources\\images\\" + fileName;
            File file = new File(absolutePath);

            if (file.exists()) {
                BufferedImage originalImg = ImageIO.read(file);
                if (originalImg != null) {
                    Image scaledImg = originalImg.getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                    lblImg.setIcon(new ImageIcon(scaledImg));
                    lblImg.setText(""); // Có ảnh thì xóa chữ
                } else {
                    lblImg.setText("Lỗi định dạng");
                }
            } else {
                lblImg.setText("Chưa có ảnh");
            }
        } catch (Exception e) {
            lblImg.setText("Lỗi tải ảnh");
        }

        card.add(lblImg, BorderLayout.NORTH);

        // --- THÔNG TIN & NÚT BẤM ---
        JPanel info = new JPanel(new GridLayout(3, 1));
        info.setBackground(new Color(245, 245, 245));
        info.setBorder(new EmptyBorder(0, 5, 5, 5));

        JLabel lblName = new JLabel("<html><center>" + name + "</center></html>", SwingConstants.CENTER);
        lblName.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblStock = new JLabel("Tổng tồn: " + stock + " đôi", SwingConstants.CENTER);
        // Cảnh báo hết hàng bằng màu đỏ
        if (stock <= 10) {
            lblStock.setForeground(new Color(255, 80, 80));
            lblStock.setFont(new Font("Arial", Font.BOLD, 13));
        } else {
            lblStock.setForeground(new Color(46, 204, 113));
            lblStock.setFont(new Font("Arial", Font.ITALIC, 12));
        }

        JButton btnDetail = new JButton("Check Size/Màu");
        btnDetail.setForeground(Color.BLACK);
        btnDetail.setBackground(new Color(230, 230, 230));
        btnDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDetail.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            // Dùng lại cái Dialog để tra cứu không cần add giỏ hàng (truyền null)
            new ProductDetailDialog(parent, id, name, price, null, null).setVisible(true);
        });

        info.add(lblName);
        info.add(lblStock);
        info.add(btnDetail);

        card.add(info, BorderLayout.CENTER);
        return card;
    }
}