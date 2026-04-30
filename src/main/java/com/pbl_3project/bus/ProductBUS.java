package com.pbl_3project.bus;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import com.pbl_3project.dao.ProductDAO;
public class ProductBUS {
    private ProductDAO productDAO = new ProductDAO();
    public DefaultTableModel getAllProducts() throws SQLException {
        return productDAO.getAllProductVariants();
    }
    public DefaultTableModel getProductsForPOS() throws SQLException {
        return productDAO.getProductsForPOS();
    }
    public DefaultTableModel searchProductsForPOS(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getProductsForPOS();
        }
        return productDAO.searchProductsForPOS(keyword.trim());
    }
    public DefaultTableModel getProductsForShop(String gender) throws SQLException {
        return productDAO.getProductsForShop(gender);
    }
    public DefaultTableModel getBaseProducts(String gender) throws SQLException {
        return productDAO.getBaseProducts(gender);
    }
    public DefaultTableModel getVariantsByProductId(int productId) throws SQLException {
        return productDAO.getVariantsByProductId(productId);
    }
    public DefaultTableModel searchProductsAdmin(String keyword) throws SQLException {
        return productDAO.searchProductsAdmin(keyword);
    }
    public boolean xoaSanPham(int productId) throws SQLException {
        return productDAO.deleteProduct(productId);
    }
    public boolean themSanPhamMoi(int brandId, int categoryId, String name, String priceStr, String gender, String desc)
            throws Exception {
        if (name.isEmpty() || priceStr.isEmpty()) {
            throw new Exception("Tên sản phẩm và Giá không được để trống!");
        }
        double price;
        try {
            price = Double.parseDouble(priceStr.replaceAll("[^\\d.]", ""));
            if (price <= 0)
                throw new Exception();
        } catch (Exception e) {
            throw new Exception("Giá sản phẩm phải là con số hợp lệ và lớn hơn 0!");
        }
        return productDAO.addProduct(brandId, categoryId, name, price, gender, desc);
    }
}
