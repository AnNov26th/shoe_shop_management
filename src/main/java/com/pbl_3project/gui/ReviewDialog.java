package com.pbl_3project.gui;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        setSize(600, 700);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        JPanel mainScrollPanel = new JPanel();
        mainScrollPanel.setLayout(new BoxLayout(mainScrollPanel, BoxLayout.Y_AXIS));
        JPanel shipPanel = new JPanel(new BorderLayout());
        shipPanel.setBorder(BorderFactory.createTitledBorder("Đánh giá Đơn vị vận chuyển"));
        JPanel shipTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        shipTop.add(new JLabel("Đánh giá (1-5 sao):"));
        shippingRating = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        shipTop.add(shippingRating);
        shipPanel.add(shipTop, BorderLayout.NORTH);
        shippingComment = new JTextArea(3, 20);
        shipPanel.add(new JScrollPane(shippingComment), BorderLayout.CENTER);
        mainScrollPanel.add(shipPanel);
        mainScrollPanel.add(Box.createVerticalStrut(10));
        productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        productsPanel.setBorder(BorderFactory.createTitledBorder("Đánh giá Sản phẩm"));
        mainScrollPanel.add(productsPanel);
        add(new JScrollPane(mainScrollPanel), BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSubmit = new JButton("Gửi đánh giá");
        btnSubmit.setBackground(new Color(34, 197, 94));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.addActionListener(e -> handleSubmit());
        JButton btnCancel = new JButton("Bỏ qua");
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
                ProductReviewPanel p = new ProductReviewPanel(productName, sku);
                productReviewPanels.add(p);
                productsPanel.add(p);
            }
            productsPanel.revalidate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void handleSubmit() {
        try {
            reviewDAO.addShippingReview(userId, orderId, (Integer) shippingRating.getSelectedItem(), shippingComment.getText());
            for (ProductReviewPanel p : productReviewPanels) {
                int productId = findProductIdBySku(p.sku); 
                if (productId != -1) {
                    reviewDAO.addProductReview(userId, productId, orderId, p.getRating(), p.getComment());
                }
            }
            JOptionPane.showMessageDialog(this, "Cảm ơn bạn đã đánh giá!");
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi gửi đánh giá: " + e.getMessage());
        }
    }
    private int findProductIdBySku(String sku) {
        try (java.sql.Connection conn = com.pbl_3project.util.DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement("SELECT product_id FROM Product_Variant WHERE sku_code = ?")) {
            pstmt.setString(1, sku);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("product_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    class ProductReviewPanel extends JPanel {
        String sku;
        JComboBox<Integer> rating;
        JTextArea comment;
        ProductReviewPanel(String name, String sku) {
            this.sku = sku;
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(5, 5, 5, 5));
            add(new JLabel(name + " (" + sku + ")"), BorderLayout.NORTH);
            JPanel mid = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rating = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
            mid.add(new JLabel("Rating:"));
            mid.add(rating);
            add(mid, BorderLayout.WEST);
            comment = new JTextArea(2, 20);
            add(new JScrollPane(comment), BorderLayout.CENTER);
        }
        int getRating() { return (Integer) rating.getSelectedItem(); }
        String getComment() { return comment.getText(); }
    }
}
