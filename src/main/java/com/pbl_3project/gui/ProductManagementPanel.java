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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import com.pbl_3project.bus.ProductBUS;
public class ProductManagementPanel extends JPanel {
    private static final Color BG_CONTENT = new Color(248, 250, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color TEXT_MAIN = new Color(15, 23, 42);
    private static final Color TEXT_SUB = new Color(100, 116, 139);
    private static final Color BTN_PRIMARY = new Color(56, 189, 248); 
    private static final Color BTN_SUCCESS = new Color(74, 222, 128); 
    private static final Color BTN_DANGER = new Color(248, 113, 113); 
    private static final Color BTN_SECONDARY = new Color(148, 163, 184); 
    private JPanel productGrid;
    private ProductBUS productBUS = new ProductBUS();
    private JTextField txtSearch;
    public ProductManagementPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_CONTENT);
        initComponents();
        loadProducts("");
    }
    private void initComponents() {
        JPanel toolBarWrapper = new JPanel(new BorderLayout());
        toolBarWrapper.setBackground(CARD_BG);
        toolBarWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(15, 25, 15, 25)));
        JPanel leftTools = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftTools.setOpaque(false);
        JLabel lblSearch = new JLabel("Tìm sản phẩm: ");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSearch.setForeground(TEXT_MAIN);
        txtSearch = new JTextField(25);
        txtSearch.setPreferredSize(new Dimension(250, 38));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(0, 15, 0, 15)));
        JButton btnSearch = createButton("Tìm kiếm", BTN_PRIMARY, Color.WHITE);
        JButton btnRefresh = createButton("Làm mới", BTN_SECONDARY, Color.WHITE);
        leftTools.add(lblSearch);
        leftTools.add(txtSearch);
        leftTools.add(btnSearch);
        leftTools.add(btnRefresh);
        JPanel rightTools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightTools.setOpaque(false);
        JButton btnAdd = createButton("Thêm SP mới", BTN_SUCCESS, Color.WHITE);
        rightTools.add(btnAdd);
        toolBarWrapper.add(leftTools, BorderLayout.WEST);
        toolBarWrapper.add(rightTools, BorderLayout.EAST);
        productGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        productGrid.setBackground(BG_CONTENT);
        productGrid.setBorder(new EmptyBorder(20, 25, 20, 25));
        JScrollPane scrollPane = new JScrollPane(productGrid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CONTENT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); 
        add(toolBarWrapper, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadProducts("");
        });
        btnSearch.addActionListener(e -> loadProducts(txtSearch.getText()));
        txtSearch.addActionListener(e -> loadProducts(txtSearch.getText()));
        btnAdd.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            AddProductDialog dialog = new AddProductDialog(parentFrame, () -> loadProducts(txtSearch.getText()));
            dialog.setVisible(true);
        });
    }
    private JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(bgColor.darker());
                } else {
                    g2.setColor(bgColor);
                }
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
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        return btn;
    }
    private void loadProducts(String keyword) {
        try {
            productGrid.removeAll();
            DefaultTableModel model = productBUS.searchProductsAdmin(keyword);
            if (model.getRowCount() == 0) {
                JLabel empty = new JLabel("Không tìm thấy sản phẩm nào.", SwingConstants.CENTER);
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                empty.setForeground(TEXT_SUB);
                productGrid.add(empty);
            } else {
                for (int i = 0; i < model.getRowCount(); i++) {
                    int id = Integer.parseInt(model.getValueAt(i, 0).toString());
                    String name = model.getValueAt(i, 1).toString();
                    double price = Double.parseDouble(model.getValueAt(i, 2).toString());
                    String sampleColor = model.getValueAt(i, 3).toString();
                    String totalStock = model.getValueAt(i, 4).toString();
                    productGrid.add(createAdminProductCard(id, name, price, sampleColor, totalStock));
                }
            }
            productGrid.revalidate();
            productGrid.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải sản phẩm: " + e.getMessage());
        }
    }
    private JPanel createAdminProductCard(int productId, String name, double price, String sampleColor, String stock) {
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
        shadow.setPreferredSize(new Dimension(200, 310));
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
        imgPanel.setPreferredSize(new Dimension(0, 170));
        JLabel lblImage = new JLabel("", SwingConstants.CENTER);
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
                }
            } else {
                lblImage.setText("👟");
                lblImage.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            }
        } catch (Exception e) {
            lblImage.setText("Lỗi ảnh");
        }
        imgPanel.add(lblImage, BorderLayout.CENTER);
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 12, 12, 12));
        JLabel lblName = new JLabel("<html><div style='text-align:center'>" + name + "</div></html>");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(TEXT_MAIN);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblStock = new JLabel("Kho: " + stock + " đôi", SwingConstants.CENTER);
        lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStock.setForeground(TEXT_SUB);
        lblStock.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblPrice = new JLabel(String.format("%,.0f VNĐ", price), SwingConstants.CENTER);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPrice.setForeground(new Color(239, 68, 68)); 
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        btnPanel.setOpaque(false);
        JButton btnView = new JButton("Chi tiết") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, BTN_PRIMARY, getWidth(), 0, new Color(99, 210, 255)));
                } else {
                    g2.setColor(new Color(56, 189, 248, 200));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnView.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnView.setForeground(new Color(10, 18, 40));
        btnView.setContentAreaFilled(false);
        btnView.setBorderPainted(false);
        btnView.setFocusPainted(false);
        btnView.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JButton btnDelete = new JButton("Xóa") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(BTN_DANGER.darker());
                } else {
                    g2.setColor(BTN_DANGER);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setContentAreaFilled(false);
        btnDelete.setBorderPainted(false);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnView.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            ProductDetailDialog detailDialog = new ProductDetailDialog(parentFrame, productId, name, price, "ADMIN",
                    null, null, null);
            detailDialog.setVisible(true);
        });
        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn vô hiệu hóa mẫu giày này?\n(" + name + ")",
                    "Xác nhận vô hiệu hóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (productBUS.xoaSanPham(productId)) {
                        JOptionPane.showMessageDialog(this, "Đã vô hiệu hóa sản phẩm thành công!");
                        loadProducts(txtSearch.getText());
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
        infoPanel.add(lblName);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(lblStock);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(lblPrice);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(btnPanel);
        card.add(imgPanel, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);
        shadow.add(card, BorderLayout.CENTER);
        return shadow;
    }
}
