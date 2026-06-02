# 📚 Database & DAO Setup Guide

## 🔄 Kiến trúc Database Schema

Database `ShopGiayDB` được thiết kế theo 5 nhóm chính:

### 1. **Nhóm Người Dùng & Phân Quyền**
- `Role`: Các vai trò trong hệ thống
- `User`: Tài khoản người dùng cơ bản
- `Customer_Profile`: Hồ sơ chi tiết của khách hàng
- `Address_Book`: Danh bạ địa chỉ giao hàng của người dùng
- `Profile_Update_Request`: Yêu cầu cập nhật hồ sơ từ người dùng

### 2. **Nhóm Danh Mục & Sản Phẩm**
- `Brand`: Thương hiệu sản phẩm
- `Category`: Danh mục sản phẩm 
- `Product`: Thông tin sản phẩm chính
- `Product_Image`: Hình ảnh chi tiết của sản phẩm
- `Product_Variant`: Các biến thể của sản phẩm (Kích cỡ, màu sắc)

### 3. **Nhóm Quản Lý Kho**
- `Supplier`: Nhà cung cấp hàng hóa
- `Purchase_Order`: Đơn nhập hàng từ nhà cung cấp
- `Purchase_Order_Item`: Chi tiết các sản phẩm trong đơn nhập
- `Inventory_Transaction`: Lịch sử giao dịch, biến động kho

### 4. **Nhóm Giỏ Hàng & Khuyến Mãi**
- `Cart`: Giỏ hàng của khách
- `Cart_Item`: Chi tiết từng sản phẩm trong giỏ
- `Promotion`: Chương trình khuyến mãi áp dụng

### 5. **Nhóm Đơn Hàng & Hậu Mãi**
- `Order`: Đơn hàng do khách đặt
- `Order_Detail`: Chi tiết từng sản phẩm trong đơn hàng
- `Payment`: Giao dịch thanh toán
- `Shipping`: Thông tin vận chuyển
- `Return_Request`: Yêu cầu đổi trả hàng hóa
- `Review`: Đánh giá chung
- `Product_Review`: Đánh giá chi tiết sản phẩm
- `Shipping_Review`: Đánh giá dịch vụ vận chuyển

---

## ⚙️ Hướng Dẫn Cài Đặt

### **Bước 1: Chuẩn Bị**

- Cài đặt SQL Server Express (hoặc phiên bản cao cấp hơn)
- Cài đặt SQL Server Management Studio (SSMS)
- Môi trường Java JDK 21 và Maven 3.8+

### **Bước 2: Tạo Database**

Mở SQL Server Management Studio và mở file script khởi tạo database tại `database/ShopGiayDB_script_4-5-2026.sql`. Sau đó Execute toàn bộ file script này để tạo các bảng dữ liệu.

Hoặc sử dụng sqlcmd:

```powershell
sqlcmd -S localhost\SQLEXPRESS -U sa -P 123456 -i "database/ShopGiayDB_script_4-5-2026.sql"
```

### **Bước 3: Verify Database**

Kiểm tra database trong SSMS:

```sql
USE ShopGiayDB;
GO

SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dbo';
```

---

## 🗂️ Hướng Dẫn Sử Dụng Các DAO Classes

Tầng DAO chịu trách nhiệm giao tiếp trực tiếp với database thông qua JDBC. Dưới đây là cách sử dụng cơ bản.

### **UserDAO**
Dùng để quản lý các nghiệp vụ liên quan đến người dùng:
- Xác thực người dùng qua Email và Password.
- Đăng ký tài khoản người dùng mới.
- Tìm kiếm người dùng dựa vào ID hoặc Email.
- Cập nhật thông tin mật khẩu.
- Lấy danh sách toàn bộ người dùng.

### **CategoryDAO**
Dùng để quản lý phân loại sản phẩm:
- Lấy toàn bộ danh sách các danh mục.
- Lọc danh mục theo danh mục cha hoặc danh mục con.
- Tìm danh mục dựa trên Slug.
- Thêm mới danh mục vào cơ sở dữ liệu.

### **ProductDAO**
Dùng để thao tác với sản phẩm:
- Truy xuất danh sách sản phẩm đầy đủ hoặc theo danh mục.
- Tìm kiếm sản phẩm theo tên.
- Lấy chi tiết các biến thể (size, color) của một sản phẩm.
- Kiểm tra số lượng tồn kho của một biến thể cụ thể.
- Cập nhật số lượng tồn kho.
- Thêm biến thể mới cho sản phẩm.

### **OrderDAO**
Dùng để quản lý đơn hàng:
- Xem toàn bộ đơn hàng trong hệ thống.
- Lấy danh sách đơn hàng theo khách hàng hoặc theo trạng thái hiện tại.
- Tìm kiếm đơn hàng qua mã đơn.
- Tạo đơn hàng mới và thêm chi tiết sản phẩm vào đơn.
- Cập nhật trạng thái xử lý của đơn.
- Xóa sản phẩm khỏi đơn hàng khi cần thiết.

### **DiscountDAO**
Dùng để quản lý mã giảm giá:
- Lấy danh sách các mã đang còn hiệu lực hoặc danh sách toàn bộ mã.
- Tìm chi tiết thông tin của một mã cụ thể.
- Kiểm tra tính hợp lệ của mã trước khi áp dụng.
- Tăng số lượt đã sử dụng của mã.
- Thêm mới, cập nhật hoặc xóa mã giảm giá.

---

## 📋 Cấu Hình Database Connection String

Chuỗi kết nối chuẩn trong project:

```
JDBC URL: jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=ShopGiayDB;encrypt=true;trustServerCertificate=true;
Username: sa
Password: 123456
```

Thay đổi thông tin này trong class cấu hình connection của hệ thống (như `DatabaseInitializer` hoặc file properties) cho phù hợp với môi trường thực tế.
