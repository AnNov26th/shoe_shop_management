package com.pbl_3project.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.pbl_3project.bus.CartBUS;
import com.pbl_3project.dao.CartDAO;
import com.pbl_3project.dao.ProductDAO;
import com.pbl_3project.dao.ReviewDAO;
import com.pbl_3project.dto.CartItem;

public class ProductDetailDialog extends JDialog {
    private int productId;
    private String productName;
    private double basePrice;
    private int currentUserId;
    private String userRole;
    private JTable tableVariants;
    private JLabel lblLargeImage;
    private JSpinner spinQty;
    private ProductDAO productDAO = new ProductDAO();
    private ReviewDAO reviewDAO = new ReviewDAO();
    private CartDAO cartDAO = new CartDAO();
    private CartBUS cartBUS;
    private Runnable onSuccess;
    private static final Color BG_COLOR = new Color(248, 250, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color ACCENT = new Color(238, 77, 45);
    private static final Color TEXT_MAIN = new Color(15, 23, 42);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);

    public ProductDetailDialog(JFrame parent, int productId, String productName, double basePrice,
            String role, Integer userId, CartBUS cartBUS, Runnable onSuccess) {
        super(parent, "Chi Tiết Sản Phẩm: " + productName, true);
        this.productId = productId;
        this.productName = productName;
        this.basePrice = basePrice;
        this.userRole = role != null ? role.toUpperCase() : "ADMIN";
        this.currentUserId = (userId != null) ? userId : -1;
        this.cartBUS = cartBUS;
        this.onSuccess = onSuccess;
        setSize(1000, 750);
        setMinimumSize(new Dimension(800, 600));
        setResizable(true);
        setLocationRelativeTo(parent);
        initComponents();
        loadVariants();
    }

    private void initComponents() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BG_COLOR);
        mainContent.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel infoSection = new JPanel(new BorderLayout(20, 20));
        infoSection.setBackground(BG_COLOR);

        RoundedPanel imageCard = new RoundedPanel(25, CARD_BG);
        imageCard.setLayout(new BorderLayout());
        imageCard.setPreferredSize(new Dimension(400, 400));
        lblLargeImage = new JLabel("Chọn một phân loại để xem ảnh", SwingConstants.CENTER);
        lblLargeImage.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        imageCard.add(lblLargeImage, BorderLayout.CENTER);
        infoSection.add(imageCard, BorderLayout.WEST);

        JPanel detailsContainer = new JPanel(new BorderLayout(0, 20));
        detailsContainer.setOpaque(false);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        JLabel lblName = new JLabel(productName.toUpperCase());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblName.setForeground(TEXT_MAIN);
        JLabel lblPrice = new JLabel(String.format("Giá: %,.0f VNĐ", basePrice));
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblPrice.setForeground(new Color(238, 77, 45));
        headerPanel.add(lblName);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(lblPrice);
        detailsContainer.add(headerPanel, BorderLayout.NORTH);

        RoundedPanel tableCard = new RoundedPanel(20, CARD_BG);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        tableVariants = new JTable();
        tableVariants.setRowHeight(38);
        tableVariants.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableVariants.setShowGrid(false);
        tableVariants.setIntercellSpacing(new Dimension(0, 0));
        tableVariants.setSelectionBackground(new Color(255, 240, 235));
        tableVariants.setSelectionForeground(TEXT_MAIN);
        JTableHeader th = tableVariants.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(new Color(248, 250, 252));
        th.setPreferredSize(new Dimension(0, 40));
        JScrollPane scrollVariants = new JScrollPane(tableVariants);
        scrollVariants.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollVariants.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollVariants, BorderLayout.CENTER);
        detailsContainer.add(tableCard, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);
        if ("POS".equals(userRole) || "CUSTOMER".equals(userRole)) {
            JLabel lblQty = new JLabel("Số lượng:");
            lblQty.setFont(new Font("Segoe UI", Font.BOLD, 14));
            spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
            spinQty.setPreferredSize(new Dimension(70, 38));
            spinQty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            JButton btnSubmit = createActionButton();
            actionPanel.add(lblQty);
            actionPanel.add(spinQty);
            actionPanel.add(btnSubmit);
        } else {
            JButton btnClose = createStyledButton("Đóng Cửa Sổ", new Color(148, 163, 184), Color.WHITE);
            btnClose.addActionListener(e -> dispose());
            actionPanel.add(btnClose);
        }
        detailsContainer.add(actionPanel, BorderLayout.SOUTH);
        infoSection.add(detailsContainer, BorderLayout.CENTER);

        mainContent.add(infoSection);
        mainContent.add(Box.createVerticalStrut(30));

        JPanel reviewSection = new JPanel(new BorderLayout(0, 15));
        reviewSection.setBackground(BG_COLOR);
        JLabel lblReviewTitle = new JLabel("ĐÁNH GIÁ SẢN PHẨM");
        lblReviewTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblReviewTitle.setForeground(TEXT_MAIN);
        reviewSection.add(lblReviewTitle, BorderLayout.NORTH);

        JPanel reviewsList = new JPanel();
        reviewsList.setLayout(new BoxLayout(reviewsList, BoxLayout.Y_AXIS));
        reviewsList.setBackground(BG_COLOR);

        loadReviews(reviewsList);

        if (reviewsList.getComponentCount() == 0) {
            JLabel lblNoReview = new JLabel("Chưa có đánh giá nào cho sản phẩm này.", SwingConstants.CENTER);
            lblNoReview.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblNoReview.setForeground(new Color(100, 116, 139));
            lblNoReview.setBorder(new EmptyBorder(30, 0, 30, 0));
            reviewSection.add(lblNoReview, BorderLayout.CENTER);
        } else {
            reviewSection.add(reviewsList, BorderLayout.CENTER);
        }

        mainContent.add(reviewSection);

        JScrollPane mainScroll = new JScrollPane(mainContent);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(20);
        setContentPane(mainScroll);

        tableVariants.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateProductImage();
            }
        });
    }

    private void loadReviews(JPanel container) {
        try {
            DefaultTableModel revModel = reviewDAO.getReviewsByProductId(productId);
            for (int i = 0; i < revModel.getRowCount(); i++) {
                String uName = (String) revModel.getValueAt(i, 0);
                int stars = (Integer) revModel.getValueAt(i, 1);
                String variantInfo = (String) revModel.getValueAt(i, 2);
                String comment = (String) revModel.getValueAt(i, 3);
                String imgUrl = (String) revModel.getValueAt(i, 4);
                java.sql.Date date = (java.sql.Date) revModel.getValueAt(i, 5);

                RoundedPanel revCard = new RoundedPanel(15, CARD_BG);
                revCard.setLayout(new BorderLayout(15, 10));
                revCard.setBorder(new EmptyBorder(15, 20, 15, 20));

                JPanel revTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                revTop.setOpaque(false);
                JLabel lblAvatar = new JLabel("👤");
                lblAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 24));
                JLabel lblUser = new JLabel("<html><b>" + uName + "</b><br><span style='color:gray;font-size:9px'>"
                        + date.toString() + "</span></html>");
                lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                revTop.add(lblAvatar);
                revTop.add(lblUser);

                StringBuilder sb = new StringBuilder();
                for (int s = 0; s < 5; s++) {
                    sb.append(s < stars ? "⭐" : "☆");
                }
                JLabel lblStars = new JLabel(sb.toString());
                lblStars.setForeground(new Color(245, 158, 11));
                revTop.add(Box.createHorizontalStrut(20));
                revTop.add(lblStars);

                if (variantInfo != null && !variantInfo.isEmpty()) {
                    JLabel lblRevVar = new JLabel("Phân loại: " + variantInfo);
                    lblRevVar.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                    lblRevVar.setForeground(new Color(150, 150, 150));
                    revTop.add(Box.createHorizontalStrut(15));
                    revTop.add(lblRevVar);
                }

                revCard.add(revTop, BorderLayout.NORTH);

                JPanel revContent = new JPanel(new BorderLayout(10, 10));
                revContent.setOpaque(false);
                JTextArea txtComment = new JTextArea(comment);
                txtComment.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                txtComment.setLineWrap(true);
                txtComment.setWrapStyleWord(true);
                txtComment.setEditable(false);
                txtComment.setOpaque(false);
                revContent.add(txtComment, BorderLayout.CENTER);

                if (imgUrl != null && !imgUrl.isEmpty()) {
                    File f = new File(imgUrl);
                    if (f.exists()) {
                        try {
                            Image img = ImageIO.read(f).getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                            JLabel lblRevImg = new JLabel(new ImageIcon(img));
                            lblRevImg.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
                            revContent.add(lblRevImg, BorderLayout.SOUTH);
                        } catch (Exception ex) {

                        }
                    }
                }
                revCard.add(revContent, BorderLayout.CENTER);

                container.add(revCard);
                container.add(Box.createVerticalStrut(15));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JButton createActionButton() {
        String text = userRole.equals("POS") ? "Thêm Vào Giỏ POS" : "Thêm Vào Giỏ Online";
        Color color = userRole.equals("POS") ? new Color(16, 185, 129) : ACCENT;
        JButton btn = createStyledButton(text, color, Color.WHITE);
        btn.addActionListener(e -> {
            if (userRole.equals("POS"))
                handlePOSAction();
            else
                handleCustomerAction();
        });
        return btn;
    }

    private void updateProductImage() {
        int row = tableVariants.getSelectedRow();
        if (row == -1)
            return;
        String colorName = tableVariants.getValueAt(row, 2).toString();
        String colorEn = "";
        if (colorName.contains("(") && colorName.contains(")")) {
            colorEn = colorName.substring(colorName.lastIndexOf("(") + 1, colorName.lastIndexOf(")")).trim();
        }
        try {
            String fileName = productName + " - " + colorEn + ".png";
            File file = new File("F:\\CNTT\\shoe_shop_management\\src\\main\\resources\\images\\" + fileName);
            if (file.exists()) {
                BufferedImage img = ImageIO.read(file);
                Image scaled = img.getScaledInstance(lblLargeImage.getWidth() - 40, -1, Image.SCALE_SMOOTH);
                lblLargeImage.setIcon(new ImageIcon(scaled));
                lblLargeImage.setText("");
            } else {
                lblLargeImage.setIcon(null);
                lblLargeImage.setText("Không tìm thấy ảnh: " + fileName);
            }
        } catch (Exception ex) {
            lblLargeImage.setText("Lỗi tải ảnh");
        }
    }

    private void handleCustomerAction() {
        int row = tableVariants.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Size & Màu sắc!");
            return;
        }
        String sku = tableVariants.getValueAt(row, 0).toString();
        int qty = (int) spinQty.getValue();
        int stock = Integer.parseInt(tableVariants.getValueAt(row, 3).toString());
        if (qty > stock) {
            JOptionPane.showMessageDialog(this, "Số lượng không được vượt quá số lượng tồn kho!");
            return;
        }
        try {
            if (cartDAO.addToCartOnline(currentUserId, sku, qty)) {
                if (cartBUS != null) {
                    String size = tableVariants.getValueAt(row, 1).toString();
                    String color = tableVariants.getValueAt(row, 2).toString();
                    cartBUS.addItem(new CartItem(sku, productName, size, color, basePrice, qty, stock));
                }
                JOptionPane.showMessageDialog(this, "Đã thêm vào giỏ hàng thành công!");
                if (onSuccess != null)
                    onSuccess.run();
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void handlePOSAction() {
        int row = tableVariants.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Size & Màu sắc!");
            return;
        }
        String sku = tableVariants.getValueAt(row, 0).toString();
        int qty = (int) spinQty.getValue();
        int stock = Integer.parseInt(tableVariants.getValueAt(row, 3).toString());
        if (qty > stock) {
            JOptionPane.showMessageDialog(this, "Số lượng không được vượt quá tồn kho!");
            return;
        }
        if (cartBUS != null) {
            String size = tableVariants.getValueAt(row, 1).toString();
            String color = tableVariants.getValueAt(row, 2).toString();
            try {
                CartItem newItem = new CartItem(sku, productName, size, color, basePrice, qty, stock);
                newItem.setExpiresAt(java.time.LocalDateTime.now()
                        .plusMinutes(com.pbl_3project.util.ConfigUtils.getReserveDurationMinutes()));
                cartBUS.addItem(newItem);
                JOptionPane.showMessageDialog(this, "Đã thêm vào giỏ hàng POS thành công!");
                if (onSuccess != null)
                    onSuccess.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private void loadVariants() {
        try {
            DefaultTableModel model = productDAO.getVariantsByProductId(productId);
            tableVariants.setModel(model);
            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
            center.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < tableVariants.getColumnCount(); i++) {
                tableVariants.getColumnModel().getColumn(i).setCellRenderer(center);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 6, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
