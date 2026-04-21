# 📚 Database & DAO Setup Guide

## 🔄 Kiến trúc Database Schema

Database `ShopGiayDB` được thiết kế theo 5 nhóm chính:

### 1. **Nhóm Người Dùng & Phân Quyền**
- `Role` - Các vai trò (Admin, Manager, Staff, Customer)
- `[User]` - Tài khoản người dùng
- `Customer_Profile` - Hồ sơ chi tiết khách hàng
- `Address_Book` - Danh bạ địa chỉ giao hàng

### 2. **Nhóm Danh Mục & Sản Phẩm**
- `Brand` - Thương hiệu giày
- `Category` - Danh mục sản phẩm (hỗ trợ phân cấp)
- `Product` - Sản phẩm
- `Product_Image` - Hình ảnh sản phẩm
- `Product_Variant` - Các biến thể sản phẩm (size, color)

### 3. **Nhóm Quản Lý Kho**
- `Supplier` - Nhà cung cấp
- `Purchase_Order` - Đơn mua hàng
- `Purchase_Order_Item` - Chi tiết đơn mua
- `Inventory_Transaction` - Nhật ký giao dịch kho

### 4. **Nhóm Giỏ Hàng & Khuyến Mãi**
- `Cart` - Giỏ hàng
- `Cart_Item` - Sản phẩm trong giỏ
- `Promotion` - Khuyến mãi/Giảm giá

### 5. **Nhóm Đơn Hàng & Hậu Mãi**
- `[Order]` - Đơn hàng
- `Order_Detail` - Chi tiết đơn hàng
- `Payment` - Thanh toán
- `Shipping` - Vận chuyển
- `Return_Request` - Yêu cầu đổi trả
- `Review` - Đánh giá sản phẩm

---

## ⚙️ Hướng Dẫn Cài Đặt

### **Bước 1: Chuẩn Bị**

✔️ Yêu cầu:
- SQL Server Express (hoặc phiên bản cao hơn)
- SQL Server Management Studio
- Maven 3.8+
- Java JDK 21

### **Bước 2: Tạo Database**

**Cách 1: Chạy script SQL (Khuyến nghị)**

```sql
-- Mở SQL Server Management Studio
-- File > Open > database/init_database.sql
-- Ctrl + A chọn tất cả
-- F5 hoặc Execute
```

**Cách 2: Dùng sqlcmd**

```powershell
sqlcmd -S localhost\SQLEXPRESS -U sa -P 123456 -i database/init_database.sql
```

### **Bước 3: Verify Database**

```sql
-- Chạy trong SQL Server Management Studio
USE ShopGiayDB;
GO

-- Xem tất cả tables
SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dbo';

-- Xem dữ liệu mẫu
SELECT COUNT(*) FROM Role;
SELECT COUNT(*) FROM Brand;
SELECT COUNT(*) FROM Category;
SELECT COUNT(*) FROM Supplier;
```

Kết quả mong đợi:
- Role: 4 record
- Brand: 4 record
- Category: 6 record
- Supplier: 3 record

---

## 🗂️ Các DAO Classes

### **UserDAO** - Quản lý người dùng
```java
// Xác thực người dùng
User user = userDAO.authenticateUser("email@gmail.com", "password");

// Đăng ký tài khoản mới
userDAO.registerUser("newemail@gmail.com", "password", "0912345678", Role.CUSTOMER);

// Lấy người dùng theo ID hoặc Email
User user = userDAO.findById(1);
User user = userDAO.findByEmail("email@gmail.com");

// Cập nhật mật khẩu
userDAO.updatePassword(userId, "newPassword");

// Lấy danh sách tất cả người dùng
List<User> users = userDAO.findAll();
```

### **CategoryDAO** - Quản lý danh mục
```java
// Lấy tất cả danh mục
List<Category> categories = categoryDAO.findAll();

// Lấy danh mục cha (PARENT)
List<Category> parentCategories = categoryDAO.findParentCategories();

// Lấy danh mục con
List<Category> subcategories = categoryDAO.findSubcategoriesByParentId(parentId);

// Tìm theo slug
Category cat = categoryDAO.findBySlug("giay-nam");

// Thêm danh mục mới
int catId = categoryDAO.save(newCategory);
```

### **ProductDAO** - Quản lý sản phẩm
```java
// Lấy tất cả sản phẩm
List<Product> products = productDAO.findAll();

// Lấy sản phẩm theo danh mục
List<Product> products = productDAO.findByCategory(categoryId);

// Tìm kiếm sản phẩm
List<Product> results = productDAO.searchByName("Nike");

// Lấy các biến thể (size, color) của sản phẩm
List<ProductVariant> variants = productDAO.findVariantsByProductId(productId);

// Kiểm tra tồn kho
int stock = productDAO.getVariantStock(variantId);

// Cập nhật tồn kho
productDAO.updateVariantStock(variantId, newQuantity);

// Thêm biến thể sản phẩm
int variantId = productDAO.saveVariant(newVariant);
```

### **OrderDAO** - Quản lý đơn hàng
```java
// Lấy tất cả đơn hàng
List<Order> orders = orderDAO.findAll();

// Lấy đơn hàng theo khách hàng
List<Order> orders = orderDAO.findByCustomer(customerId);

// Lấy đơn hàng theo trạng thái
List<Order> orders = orderDAO.findByStatus(OrderStatus.PENDING);

// Tìm theomã đơn hàng
Order order = orderDAO.findByOrderCode("ORD20260401000001");

// Tạo đơn hàng mới
int orderId = orderDAO.save(newOrder);

// Thêm sản phẩm vào đơn hàng
orderDAO.addOrderItem(orderId, variantId, quantity, unitPrice);

// Cập nhật trạng thái đơn hàng
orderDAO.updateOrderStatus(orderId, OrderStatus.CONFIRMED);

// Xóa sản phẩm khỏi đơn hàng
orderDAO.removeOrderItem(orderItemId);
```

### **DiscountDAO** - Quản lý giảm giá
```java
// Lấy tất cả giảm giá đang hoạt động
List<Discount> discounts = discountDAO.findAll();

// Lấy tất cả giảm giá (kể cả hết hạn)
List<Discount> allDiscounts = discountDAO.findAllIncludeExpired();

// Tìm giảm giá theo mã
Discount discount = discountDAO.findByCode("SUMMER2024");

// Kiểm tra giảm giá còn hiệu lực
boolean isValid = discountDAO.isDiscountValid("SUMMER2024");

// Tăng lượt sử dụng
discountDAO.incrementUsage(discountId);

// Thêm giảm giá mới
int discountId = discountDAO.save(newDiscount);

// Cập nhật giảm giá
discountDAO.update(discount);

// Xóa giảm giá
discountDAO.delete(discountId);
```

---

## 🧪 Chạy DAO Test

### **Phương pháp 1: PowerShell Script**

```powershell
cd f:\CNTT\shoe_shop_management
powershell -ExecutionPolicy Bypass -File setup_database.ps1
```

### **Phương pháp 2: Maven**

```bash
mvn clean compile

# Chạy DAOTest
mvn exec:java -Dexec.mainClass="com.pbl_3project.DAOTest"
```

### **Phương pháp 3: IDE (VS Code/IntelliJ)**

1. Mở file `DAOTest.java`
2. Nhấn Ctrl+F5 hoặc Run > Run

---

## 🔍 Kết Quả Test Mong Đợi

```
========== DATABASE DAO TEST ==========

🔗 TEST 1: Database Connection
✓ Database connected successfully!

👤 TEST 2: User DAO
✓ User registered: SUCCESS
✓ User found: testuser@gmail.com (Role: CUSTOMER)
✓ User authenticated successfully!
✓ Total users in database: 5

📂 TEST 3: Category DAO
✓ Total categories: 6
✓ Parent categories: 3
✓ Subcategories of 'Giày Nam': 2

🛍️  TEST 4: Product DAO
✓ Total products: 0
✓ Search 'Nike': 0 results

🎟️  TEST 5: Discount DAO
✓ Total active discounts: 0
✓ Total discounts (all): 0
✓ New discount created: ID 1 (TEST1234567890)
✓ Discount found: TEST1234567890 (PERCENT)
✓ Discount valid: YES

========== ALL TESTS COMPLETED ==========
```

---

## ⚡ Xử Lý Sự Cố

### **Vấn đề: "Connection refused"**
```
✗ java.sql.DriverManager.getConnection() - Connection refused
```

**Giải pháp:**
- Kiểm tra SQL Server đang chạy
- Kiểm tra tên server: `localhost\SQLEXPRESS`
- Kiểm tra user/password: `sa` / `123456`

### **Vấn đề: "Database does not exist"**
```
✗ [DBNETLIB][ConnectionOpen (Connect())].
```

**Giải pháp:**
- Chạy lại script `init_database.sql`

### **Vấn đề: "Cannot allocate memory"**
```
java.lang.OutOfMemoryError
```

**Giải pháp:**
```bash
# Tăng heap size
set MAVEN_OPTS=-Xmx2048m
mvn exec:java -Dexec.mainClass="com.pbl_3project.DAOTest"
```

---

## 📋 Database Connection String

```
JDBC URL: jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=ShopGiayDB;encrypt=true;trustServerCertificate=true;
Username: sa
Password: 123456
```

---

## 🚀 Bước Tiếp

✅ Database khởi tạo xong  
✅ DAOs đã implement  
⏭️ Tiếp theo: Tạo Service layer để xử lý business logic
