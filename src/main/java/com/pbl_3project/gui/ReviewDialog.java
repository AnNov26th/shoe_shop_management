package com.pbl_3project.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.pbl_3project.dao.ReviewDAO;
import com.pbl_3project.bus.OrderBUS;

public class ReviewDialog extends JDialog {
    private int userId;
    private int orderId;
    private OrderBUS orderBUS = new OrderBUS();
    private ReviewDAO reviewDAO = new ReviewDAO();

    private JPanel productsPanel;
    private JComboBox<Integer> shippingRating;
    private JTextArea shippingComment;
    private List<ProductReviewPanel> productReviewPanels = new ArrayList<>();

    public ReviewDialog(Frame parent, int userId, int orderId) {
        super(parent, "Đánh giá đơn hàng #" + orderId, true);
        this.userId = userId;
        this.orderId = orderId;
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        setSize(750, 800);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Đánh giá sản phẩm & Dịch vụ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(15, 23, 42));
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainScrollPanel = new JPanel();
        mainScrollPanel.setLayout(new BoxLayout(mainScrollPanel, BoxLayout.Y_AXIS));
        mainScrollPanel.setBackground(new Color(248, 250, 252));
        mainScrollPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel shipPanel = new JPanel(new BorderLayout(10, 10));
        shipPanel.setBackground(Color.WHITE);
        shipPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel shipTitle = new JLabel("Đánh giá Đơn vị vận chuyển");
        shipTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        shipPanel.add(shipTitle, BorderLayout.NORTH);

        JPanel shipTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        shipTop.setBackground(Color.WHITE);
        shipTop.add(new JLabel("Mức độ hài lòng:"));
        shippingRating = new JComboBox<>(new Integer[] { 5, 4, 3, 2, 1 });
        shippingRating.setRenderer(new StarRenderer());
        shippingRating.setSelectedIndex(0);
        shipTop.add(shippingRating);

        shippingComment = new JTextArea(3, 20);
        shippingComment.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        shippingComment.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(8, 8, 8, 8)));
        shippingComment.setLineWrap(true);
        shippingComment.setWrapStyleWord(true);

        JPanel shipCenter = new JPanel(new BorderLayout());
        shipCenter.setBackground(Color.WHITE);
        shipCenter.add(shipTop, BorderLayout.NORTH);
        shipCenter.add(new JScrollPane(shippingComment), BorderLayout.CENTER);

        shipPanel.add(shipCenter, BorderLayout.CENTER);

        mainScrollPanel.add(shipPanel);
        mainScrollPanel.add(Box.createVerticalStrut(20));

        productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        productsPanel.setBackground(new Color(248, 250, 252));

        mainScrollPanel.add(productsPanel);

        JScrollPane scrollPane = new JScrollPane(mainScrollPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnSubmit = new JButton("Gửi đánh giá");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(238, 77, 45));
        btnSubmit.setForeground(Color.BLACK);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setBorder(new EmptyBorder(10, 25, 10, 25));
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.addActionListener(e -> handleSubmit());

        JButton btnCancel = new JButton("Trở lại");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(new Color(100, 116, 139));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(9, 20, 9, 20)));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSubmit);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        try {
            DefaultTableModel model = orderBUS.getOrderDetails(orderId);
            for (int i = 0; i < model.getRowCount(); i++) {
                String productName = (String) model.getValueAt(i, 0);
                String sku = (String) model.getValueAt(i, 1);
                String size = (String) model.getValueAt(i, 2);
                String color = (String) model.getValueAt(i, 3);
                String variantInfo = color + ", " + size;
                ProductReviewPanel p = new ProductReviewPanel(productName, sku, variantInfo);
                productReviewPanels.add(p);
                productsPanel.add(p);
                productsPanel.add(Box.createVerticalStrut(15));
            }
            productsPanel.revalidate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleSubmit() {
        try {
            reviewDAO.addShippingReview(userId, orderId, (Integer) shippingRating.getSelectedItem(),
                    shippingComment.getText());

            for (ProductReviewPanel p : productReviewPanels) {
                int productId = findProductIdBySku(p.sku);
                if (productId != -1) {
                    reviewDAO.addProductReview(userId, productId, orderId, p.getRating(), p.variantInfo, p.getComment(),
                            p.getImageUrl());
                }
            }

            JOptionPane.showMessageDialog(this, "Cảm ơn bạn đã đánh giá đơn hàng!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi gửi đánh giá: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private int findProductIdBySku(String sku) {
        try (java.sql.Connection conn = com.pbl_3project.util.DatabaseConnection.getConnection();
                java.sql.PreparedStatement pstmt = conn
                        .prepareStatement("SELECT product_id FROM Product_Variant WHERE sku_code = ?")) {
            pstmt.setString(1, sku);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt("product_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    class ProductReviewPanel extends JPanel {
        String sku;
        String variantInfo;
        JComboBox<Integer> rating;
        JTextArea comment;
        String imageUrl = null;
        JLabel lblImagePreview;

        ProductReviewPanel(String name, String sku, String variantInfo) {
            this.sku = sku;
            this.variantInfo = variantInfo;
            setLayout(new BorderLayout(10, 10));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                    new EmptyBorder(15, 15, 15, 15)));

            JPanel top = new JPanel(new BorderLayout());
            top.setBackground(Color.WHITE);
            JLabel lblName = new JLabel(name);
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
            top.add(lblName, BorderLayout.NORTH);
            JLabel lblVar = new JLabel("Phân loại hàng: " + variantInfo);
            lblVar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblVar.setForeground(new Color(100, 116, 139));
            top.add(lblVar, BorderLayout.SOUTH);

            add(top, BorderLayout.NORTH);

            JPanel mid = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            mid.setBackground(Color.WHITE);
            rating = new JComboBox<>(new Integer[] { 5, 4, 3, 2, 1 });
            rating.setRenderer(new StarRenderer());
            rating.setSelectedIndex(0);
            mid.add(new JLabel("Chất lượng sản phẩm:"));
            mid.add(rating);

            comment = new JTextArea(3, 30);
            comment.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            comment.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    new EmptyBorder(8, 8, 8, 8)));
            comment.setLineWrap(true);
            comment.setWrapStyleWord(true);

            JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
            centerPanel.setBackground(Color.WHITE);
            centerPanel.add(mid, BorderLayout.NORTH);
            centerPanel.add(new JScrollPane(comment), BorderLayout.CENTER);

            JPanel imgUploadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            imgUploadPanel.setBackground(Color.WHITE);
            JButton btnUpload = new JButton("📷 Thêm Hình ảnh") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(241, 245, 249));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btnUpload.setFocusPainted(false);
            btnUpload.setContentAreaFilled(false);
            btnUpload.setBorderPainted(false);
            btnUpload.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btnUpload.setBorder(new EmptyBorder(8, 15, 8, 15));
            btnUpload.setCursor(new Cursor(Cursor.HAND_CURSOR));

            lblImagePreview = new JLabel("");
            lblImagePreview.setPreferredSize(new Dimension(80, 80));
            lblImagePreview.setBorder(BorderFactory.createDashedBorder(new Color(203, 213, 225), 3, 3));
            lblImagePreview.setHorizontalAlignment(SwingConstants.CENTER);
            lblImagePreview.setVisible(false);

            btnUpload.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(
                        new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    this.imageUrl = selectedFile.getAbsolutePath();
                    try {
                        ImageIcon icon = new ImageIcon(this.imageUrl);
                        Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                        lblImagePreview.setIcon(new ImageIcon(img));
                        lblImagePreview.setText("");
                        lblImagePreview.setVisible(true);
                        revalidate();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            imgUploadPanel.add(btnUpload);
            imgUploadPanel.add(lblImagePreview);
            centerPanel.add(imgUploadPanel, BorderLayout.SOUTH);

            add(centerPanel, BorderLayout.CENTER);
        }

        int getRating() {
            return (Integer) rating.getSelectedItem();
        }

        String getComment() {
            return comment.getText();
        }

        String getImageUrl() {
            return imageUrl;
        }
    }

    class StarRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Integer) {
                int stars = (Integer) value;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < stars; i++)
                    sb.append("⭐");
                setText(sb.toString() + (stars == 5 ? " Tuyệt vời" : ""));
                setForeground(new Color(245, 158, 11));
                setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            }
            if (isSelected) {
                setBackground(new Color(255, 247, 237));
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }
}
