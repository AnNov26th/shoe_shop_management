package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.pbl_3project.bus.ProductBUS;

public class AddProductDialog extends JDialog {
    private ProductBUS productBUS;
    private Runnable onSuccess;

    // Map tĩnh Hãng và Danh mục khớp với Database
    private final String[] BRANDS = { "1 - Adidas", "2 - Asics", "3 - Birkenstock", "4 - Biti's", "5 - Converse",
            "6 - Crocs", "7 - New Balance", "8 - Nike", "9 - Puma", "10 - Vans", "11 - Balenciaga" };
    private final String[] CATEGORIES = { "6 - Sneaker", "7 - Running", "8 - Sandal & Clog", "9 - Skateboarding",
            "10 - Slide" };

    public AddProductDialog(JFrame parent, Runnable onSuccess) {
        super(parent, "Thêm Mẫu Giày Mới", true);
        this.productBUS = new ProductBUS();
        this.onSuccess = onSuccess;

        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 20));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(245, 245, 245));

        JTextField txtName = new JTextField();
        JTextField txtPrice = new JTextField();
        JComboBox<String> cbxBrand = new JComboBox<>(BRANDS);
        JComboBox<String> cbxCategory = new JComboBox<>(CATEGORIES);
        JComboBox<String> cbxGender = new JComboBox<>(new String[] { "Nam", "Nữ", "Unisex" });
        JTextField txtDesc = new JTextField();

        formPanel.add(new JLabel("Tên Sản Phẩm (*):"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Giá Bán Cơ Bản (VNĐ) (*):"));
        formPanel.add(txtPrice);
        formPanel.add(new JLabel("Hãng Sản Xuất:"));
        formPanel.add(cbxBrand);
        formPanel.add(new JLabel("Loại Giày:"));
        formPanel.add(cbxCategory);
        formPanel.add(new JLabel("Giới tính:"));
        formPanel.add(cbxGender);
        formPanel.add(new JLabel("Mô tả ngắn:"));
        formPanel.add(txtDesc);

        add(formPanel, BorderLayout.CENTER);

        JButton btnSave = new JButton("LƯU SẢN PHẨM");
        btnSave.setBackground(new Color(59, 190, 210));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFont(new Font("Arial", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(0, 45));

        btnSave.addActionListener(e -> {
            try {
                // Tách lấy ID từ chữ (Ví dụ: "8 - Nike" -> lấy số 8)
                int brandId = Integer.parseInt(cbxBrand.getSelectedItem().toString().split(" - ")[0]);
                int catId = Integer.parseInt(cbxCategory.getSelectedItem().toString().split(" - ")[0]);

                if (productBUS.themSanPhamMoi(brandId, catId, txtName.getText(), txtPrice.getText(),
                        cbxGender.getSelectedItem().toString(), txtDesc.getText())) {
                    JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
                    if (onSuccess != null)
                        onSuccess.run(); // Load lại bảng
                    dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });
        add(btnSave, BorderLayout.SOUTH);
    }
}