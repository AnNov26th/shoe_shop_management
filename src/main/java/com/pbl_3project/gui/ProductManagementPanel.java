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
import javax.swing.Box;
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

public class ProductManagementPanel extends JPanel {
    private JPanel productGrid;
    private ProductBUS productBUS = new ProductBUS();
    private JTextField txtSearch;

    public ProductManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        initComponents();
        loadProducts(""); // Mặc định load toàn bộ danh sách khi mở
    }

    private void initComponents() {
        // --- Thanh công cụ ở trên ---
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolBar.setBackground(new Color(250, 250, 250));
        toolBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtSearch = new JTextField(25);
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton btnSearch = new JButton("[Tìm kiếm]");
        btnSearch.setBackground(new Color(59, 190, 210));
        btnSearch.setForeground(Color.BLACK);
        btnSearch.setFont(new Font("Arial", Font.BOLD, 12));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnRefresh = new JButton("[Làm mới]");
        btnRefresh.setBackground(new Color(100, 150, 100));
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnAdd = new JButton("➕ Thêm SP mới");
        btnAdd.setBackground(new Color(255, 127, 102));
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        toolBar.add(new JLabel("TÌM SẢN PHẨM: "));
        toolBar.add(txtSearch);
        toolBar.add(btnSearch);
        toolBar.add(btnRefresh);
        toolBar.add(Box.createHorizontalStrut(50));
        toolBar.add(btnAdd);

        // --- KHU VỰC LƯỚI SẢN PHẨM (THAY THẾ JTABLE BẰNG WRAPLAYOUT) ---
        // Sử dụng WrapLayout để các thẻ ảnh tự động rớt dòng khi kéo giãn cửa sổ
        productGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
        productGrid.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(productGrid);
        scrollPane.setBorder(null);
        // Tăng tốc độ cuộn chuột cho mượt
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- SỰ KIỆN NÚT BẤM ---
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadProducts("");
        });

        btnSearch.addActionListener(e -> loadProducts(txtSearch.getText()));
        txtSearch.addActionListener(e -> loadProducts(txtSearch.getText())); // Nhấn Enter tìm kiếm

        btnAdd.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            // Mở Popup Thêm SP, sau khi lưu xong tự động Load lại danh sách
            AddProductDialog dialog = new AddProductDialog(parentFrame, () -> loadProducts(txtSearch.getText()));
            dialog.setVisible(true);
        });
    }

    // Tải dữ liệu từ DB, nhồi vào lưới Grid
    private void loadProducts(String keyword) {
        try {
            productGrid.removeAll(); // Xóa thẻ cũ

            // Gọi hàm tìm kiếm dành cho Admin (Lấy cả SP hết hàng)
            DefaultTableModel model = productBUS.searchProductsAdmin(keyword);

            for (int i = 0; i < model.getRowCount(); i++) {
                int id = Integer.parseInt(model.getValueAt(i, 0).toString());
                String name = model.getValueAt(i, 1).toString();
                double price = Double.parseDouble(model.getValueAt(i, 2).toString());
                String sampleColor = model.getValueAt(i, 3).toString();
                String totalStock = model.getValueAt(i, 4).toString();

                // Tạo thẻ và add vào lưới
                productGrid.add(createAdminProductCard(id, name, price, sampleColor, totalStock));
            }

            // Làm mới giao diện
            productGrid.revalidate();
            productGrid.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải sản phẩm: " + e.getMessage());
        }
    }

    // Hàm sinh Giao diện Thẻ Sản Phẩm (Có nút Chi Tiết & Xóa)
    private JPanel createAdminProductCard(int productId, String name, double price, String sampleColor, String stock) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        card.setPreferredSize(new Dimension(180, 290));

        // --- KHU VỰC HÌNH ẢNH ---
        JLabel lblImage = new JLabel("Đang tải...", SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(160, 160));

        String colorEn = "";
        if (sampleColor != null && sampleColor.contains("(") && sampleColor.contains(")")) {
            colorEn = sampleColor.substring(sampleColor.lastIndexOf("(") + 1, sampleColor.lastIndexOf(")")).trim();
        }

        try {
            String fileName = name + " - " + colorEn + ".png";
            File file = new File("F:\\CNTT\\shoe_shop_management\\src\\main\\resources\\images\\" + fileName);
            if (file.exists()) {
                BufferedImage originalImg = ImageIO.read(file);
                if (originalImg != null) {
                    Image img = originalImg.getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                    lblImage.setIcon(new ImageIcon(img));
                    lblImage.setText("");
                } else {
                    lblImage.setText("Lỗi File");
                }
            } else {
                lblImage.setText("Không có ảnh");
            }
        } catch (Exception e) {
            lblImage.setText("Lỗi tải ảnh");
        }
        card.add(lblImage, BorderLayout.NORTH);

        // --- KHU VỰC THÔNG TIN & NÚT BẤM ---
        JPanel infoPanel = new JPanel(new BorderLayout(0, 5));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel lblName = new JLabel("<html><center>" + name + "</center></html>", SwingConstants.CENTER);
        lblName.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblPriceStock = new JLabel(String.format("%,.0f VNĐ - Tồn: %s", price, stock), SwingConstants.CENTER);
        lblPriceStock.setForeground(new Color(200, 50, 50));

        infoPanel.add(lblName, BorderLayout.NORTH);
        infoPanel.add(lblPriceStock, BorderLayout.CENTER);

        // Hai nút chức năng
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        btnPanel.setBackground(new Color(245, 245, 245));

        JButton btnView = new JButton("Chi tiết");
        btnView.setBackground(new Color(59, 190, 210));
        btnView.setForeground(Color.BLACK);
        btnView.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDelete = new JButton("Xóa");
        btnDelete.setBackground(new Color(255, 80, 80));
        btnDelete.setForeground(Color.BLACK);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Sự kiện Xem Chi Tiết
        btnView.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            // Mở lại cái bảng Danh sách Size/Màu siêu bự hôm trước
            ProductDetailDialog detailDialog = new ProductDetailDialog(parentFrame, productId, name, price, null, null);
            detailDialog.setVisible(true);
        });

        // Sự kiện Xóa Sản Phẩm (Xóa mềm)
        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "⚠️ Bạn có chắc chắn muốn vô hiệu hóa mẫu giày này?\n(" + name + ")",
                    "Xác nhận vô hiệu hóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (productBUS.xoaSanPham(productId)) {
                        JOptionPane.showMessageDialog(this, "Đã vô hiệu hóa sản phẩm thành công!");
                        loadProducts(txtSearch.getText()); // Tải lại giao diện lưới
                    } else {
                        JOptionPane.showMessageDialog(this, "Lỗi: Không thể xóa sản phẩm lúc này!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi thao tác CSDL: " + ex.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnPanel.add(btnView);
        btnPanel.add(btnDelete);
        infoPanel.add(btnPanel, BorderLayout.SOUTH);

        card.add(infoPanel, BorderLayout.CENTER);
        return card;
    }
}