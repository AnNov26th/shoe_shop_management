package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.pbl_3project.bus.ProductBUS;

public class EditProductDialog extends JDialog {
    private ProductBUS productBUS;
    private Runnable onSuccess;
    private File selectedImageFile;
    private int productId;
    private final String[] BRANDS = { "1 - Adidas", "2 - Asics", "3 - Birkenstock", "4 - Biti's", "5 - Converse",
            "6 - Crocs", "7 - New Balance", "8 - Nike", "9 - Puma", "10 - Vans", "11 - Balenciaga" };
    private final String[] CATEGORIES = { "6 - Sneaker", "7 - Running", "8 - Sandal & Clog", "9 - Skateboarding",
            "10 - Slide" };

    private JTextField txtName;
    private JComboBox<String> cbxCategory;
    private JComboBox<String> cbxBrand;
    private JLabel lblImagePreview;
    private JComboBox<String> cbxGender;
    private JTextField txtPrice;
    private JTextArea txtDesc;
    private JSpinner spnQuantity;

    public EditProductDialog(JFrame parent, int productId, Runnable onSuccess) {
        super(parent, "Cập Nhật Sản Phẩm", true);
        this.productBUS = new ProductBUS();
        this.onSuccess = onSuccess;
        this.productId = productId;
        setSize(650, 750);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("CẬP NHẬT SẢN PHẨM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        txtName = createTextField();
        cbxCategory = createComboBox(CATEGORIES);
        cbxBrand = createComboBox(BRANDS);

        JPanel imagePanel = new JPanel(new BorderLayout(10, 0));
        imagePanel.setBackground(Color.WHITE);
        JButton btnChooseImage = createStyledButton("Chọn Ảnh Mới", new Color(52, 152, 219), Color.WHITE);
        btnChooseImage.setPreferredSize(new Dimension(130, 35));
        lblImagePreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblImagePreview.setPreferredSize(new Dimension(120, 120));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        imagePanel.add(btnChooseImage, BorderLayout.WEST);
        imagePanel.add(lblImagePreview, BorderLayout.CENTER);

        btnChooseImage.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fileChooser.getSelectedFile();
                ImageIcon icon = new ImageIcon(selectedImageFile.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                lblImagePreview.setIcon(new ImageIcon(img));
                lblImagePreview.setText("");
            }
        });

        cbxGender = createComboBox(new String[] { "Nam", "Nữ", "Unisex" });
        txtPrice = createTextField();

        spnQuantity = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        spnQuantity.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spnQuantity.setPreferredSize(new Dimension(250, 35));
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) spnQuantity.getEditor();
        spinnerEditor.getTextField().setBackground(Color.WHITE);
        spnQuantity.setEnabled(false); // Can't easily update total stock from here since it's variants based

        txtDesc = new JTextArea(4, 20);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        addFormField(formPanel, "Tên sản phẩm (*):", txtName, gbc, 0);
        addFormField(formPanel, "Danh mục (*):", cbxCategory, gbc, 1);
        addFormField(formPanel, "Hãng sản xuất (*):", cbxBrand, gbc, 2);

        gbc.fill = GridBagConstraints.BOTH;
        addFormField(formPanel, "Hình ảnh:", imagePanel, gbc, 3);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        addFormField(formPanel, "Giới tính:", cbxGender, gbc, 4);
        addFormField(formPanel, "Giá bán (VNĐ) (*):", txtPrice, gbc, 5);
        addFormField(formPanel, "Số lượng tồn kho:", spnQuantity, gbc, 6);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        addFormField(formPanel, "Mô tả chi tiết:", scrollDesc, gbc, 7);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(15, 30, 20, 30));

        JButton btnSave = createStyledButton("CẬP NHẬT", new Color(16, 185, 129), Color.WHITE);
        JButton btnCancel = createStyledButton("HỦY BỎ", new Color(239, 68, 68), Color.WHITE);

        btnSave.addActionListener(e -> {
            try {
                if (txtName.getText().trim().isEmpty() || txtPrice.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên sản phẩm và Giá bán!",
                            "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int brandId = Integer.parseInt(cbxBrand.getSelectedItem().toString().split(" - ")[0]);
                int catId = Integer.parseInt(cbxCategory.getSelectedItem().toString().split(" - ")[0]);

                if (productBUS.capNhatSanPham(productId, brandId, catId, txtName.getText(), txtPrice.getText(),
                        cbxGender.getSelectedItem().toString(), txtDesc.getText())) {
                    JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!");
                    if (onSuccess != null)
                        onSuccess.run();
                    dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dispose());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSave);
        add(bottomPanel, BorderLayout.SOUTH);
        
        loadProductData();
    }

    private void loadProductData() {
        try {
            Object[] data = productBUS.getProductDetails(productId);
            if (data != null) {
                int brandId = (Integer) data[0];
                int catId = (Integer) data[1];
                String name = (String) data[2];
                double price = (Double) data[3];
                String gender = (String) data[4];
                String desc = (String) data[5];

                txtName.setText(name);
                txtPrice.setText(String.format("%.0f", price));
                if (desc != null) txtDesc.setText(desc);

                for (int i = 0; i < cbxBrand.getItemCount(); i++) {
                    if (cbxBrand.getItemAt(i).startsWith(brandId + " - ")) {
                        cbxBrand.setSelectedIndex(i);
                        break;
                    }
                }
                for (int i = 0; i < cbxCategory.getItemCount(); i++) {
                    if (cbxCategory.getItemAt(i).startsWith(catId + " - ")) {
                        cbxCategory.setSelectedIndex(i);
                        break;
                    }
                }
                for (int i = 0; i < cbxGender.getItemCount(); i++) {
                    if (cbxGender.getItemAt(i).equalsIgnoreCase(gender)) {
                        cbxGender.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải thông tin: " + ex.getMessage());
        }
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(250, 35));
        textField.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return textField;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(250, 35));
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }

    private void addFormField(JPanel panel, String labelText, javax.swing.JComponent component, GridBagConstraints gbc,
            int gridy) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(70, 70, 70));

        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(component, gbc);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2.setColor(fg);
                g2.setFont(getFont());
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);
                
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
