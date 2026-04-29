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
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.pbl_3project.bus.CartBUS;
import com.pbl_3project.dao.CartDAO;
import com.pbl_3project.dao.ProductDAO;
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
    private CartDAO cartDAO = new CartDAO();
    private CartBUS cartBUS;
    private Runnable onSuccess;
    private static final Color BG_COLOR = new Color(248, 250, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color ACCENT = new Color(56, 189, 248);
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
        setSize(1000, 700); 
        setMinimumSize(new Dimension(800, 600));
        setResizable(true); 
        setLocationRelativeTo(parent);
        initComponents();
        loadVariants();
    }
    private void initComponents() {
        JPanel mainContainer = new JPanel(new BorderLayout(20, 20));
        mainContainer.setBackground(BG_COLOR);
        mainContainer.setBorder(new EmptyBorder(25, 25, 25, 25));
        setContentPane(mainContainer);
        RoundedPanel imageCard = new RoundedPanel(25, CARD_BG);
        imageCard.setLayout(new BorderLayout());
        imageCard.setPreferredSize(new Dimension(450, 0));
        lblLargeImage = new JLabel("", SwingConstants.CENTER);
        lblLargeImage.setText("Chọn một phân loại để xem ảnh");
        lblLargeImage.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        imageCard.add(lblLargeImage, BorderLayout.CENTER);
        mainContainer.add(imageCard, BorderLayout.WEST);
        JPanel infoContainer = new JPanel(new BorderLayout(0, 20));
        infoContainer.setOpaque(false);
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        JLabel lblName = new JLabel(productName.toUpperCase());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblName.setForeground(TEXT_MAIN);
        JLabel lblPrice = new JLabel(String.format("Giá niêm yết: %,.0f VNĐ", basePrice));
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblPrice.setForeground(new Color(239, 68, 68)); 
        headerPanel.add(lblName);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblPrice);
        infoContainer.add(headerPanel, BorderLayout.NORTH);
        RoundedPanel tableCard = new RoundedPanel(20, CARD_BG);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        tableVariants = new JTable();
        tableVariants.setRowHeight(38);
        tableVariants.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableVariants.setShowGrid(false);
        tableVariants.setIntercellSpacing(new Dimension(0, 0));
        tableVariants.setSelectionBackground(new Color(239, 246, 255));
        tableVariants.setSelectionForeground(TEXT_MAIN);
        JTableHeader th = tableVariants.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(new Color(248, 250, 252));
        th.setPreferredSize(new Dimension(0, 40));
        JScrollPane scrollPane = new JScrollPane(tableVariants);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        infoContainer.add(tableCard, BorderLayout.CENTER);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);
        if (!"ADMIN".equals(userRole)) {
            JLabel lblQty = new JLabel("Số lượng:");
            lblQty.setFont(new Font("Segoe UI", Font.BOLD, 14));
            spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
            spinQty.setPreferredSize(new Dimension(70, 38));
            JButton btnSubmit = createActionButton();
            actionPanel.add(lblQty);
            actionPanel.add(spinQty);
            actionPanel.add(btnSubmit);
        } else {
            JButton btnClose = createStyledButton("Đóng Cửa Sổ", new Color(148, 163, 184));
            btnClose.addActionListener(e -> dispose());
            actionPanel.add(btnClose);
        }
        infoContainer.add(actionPanel, BorderLayout.SOUTH);
        mainContainer.add(infoContainer, BorderLayout.CENTER);
        tableVariants.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateProductImage();
            }
        });
    }
    private JButton createActionButton() {
        String text = userRole.equals("STAFF") ? "Giữ Chỗ POS (5 Phút)" : "Thêm Vào Giỏ Online";
        Color color = userRole.equals("STAFF") ? new Color(74, 222, 128) : ACCENT;
        JButton btn = createStyledButton(text, color);
        btn.addActionListener(e -> {
            if (userRole.equals("STAFF"))
                handleStaffAction();
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
        try {
            if (cartDAO.addToCartOnline(currentUserId, sku, qty)) {
                if (cartBUS != null) {
                    String size = tableVariants.getValueAt(row, 1).toString();
                    String color = tableVariants.getValueAt(row, 2).toString();
                    int stock = Integer.parseInt(tableVariants.getValueAt(row, 3).toString());
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
    private void handleStaffAction() {
        int row = tableVariants.getSelectedRow();
        if (row == -1)
            return;
        String sku = tableVariants.getValueAt(row, 0).toString();
        try {
            if (cartDAO.holdItemForPOS(currentUserId, sku, (int) spinQty.getValue())) {
                JOptionPane.showMessageDialog(this, "Đã giữ chỗ thành công!");
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
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
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(220, 42));
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
            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 6, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}