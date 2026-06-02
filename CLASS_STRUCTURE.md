# Shoe Shop Management System - Class Structure

## 📦 Package: com.pbl_3project.enums
Các enum định nghĩa hằng số cho hệ thống:

- **Role**: Quyền người dùng
- **OrderStatus**: Trạng thái đơn hàng
- **PaymentMethod**: Phương thức thanh toán
- **PaymentStatus**: Trạng thái thanh toán
- **DiscountType**: Loại giảm giá
- **StockAdjustmentType**: Loại điều chỉnh kho
- **ReturnStatus**: Trạng thái đổi trả
- **PromotionType**: Loại khuyến mãi
- **Gender**: Giới tính
- **PaymentProvider**: Nhà cung cấp dịch vụ thanh toán

## 🏛️ Package: com.pbl_3project.dto
Các Data Transfer Object (DTO) đại diện cho dữ liệu truyền tải:

- **CartItem**: Đại diện cho mục sản phẩm trong giỏ hàng.

## 📊 Package: com.pbl_3project.dao
DAO (Data Access Object) - Các class tương tác trực tiếp với Database:

- **UserDAO**: Quản lý truy xuất dữ liệu người dùng.
- **ProductDAO**: Quản lý truy xuất dữ liệu sản phẩm.
- **OrderDAO**: Quản lý truy xuất dữ liệu đơn hàng.
- **CartDAO**: Quản lý truy xuất dữ liệu giỏ hàng.
- **DiscountDAO**: Quản lý truy xuất mã giảm giá.
- **EmployeeDAO**: Quản lý truy xuất dữ liệu nhân viên.
- **ReviewDAO**: Quản lý truy xuất dữ liệu đánh giá.
- **DashboardDAO**: Truy xuất dữ liệu thống kê cho Dashboard.

## ⚙️ Package: com.pbl_3project.bus
BUS (Business Logic Layer) - Tầng xử lý logic nghiệp vụ:

- **UserBUS**: Xử lý logic nghiệp vụ của người dùng.
- **ProductBUS**: Xử lý logic nghiệp vụ của sản phẩm.
- **OrderBUS**: Xử lý logic nghiệp vụ của đơn hàng.
- **CartBUS**: Xử lý logic nghiệp vụ của giỏ hàng.
- **DiscountBUS**: Xử lý logic nghiệp vụ của mã giảm giá.
- **CustomerBUS**: Xử lý logic nghiệp vụ của khách hàng.
- **EmployeeBUS**: Xử lý logic nghiệp vụ của nhân viên.
- **CartMonitor**: Luồng chạy ngầm theo dõi giỏ hàng.

## 🖥️ Package: com.pbl_3project.gui
GUI (Graphical User Interface) - Tầng giao diện người dùng:

- Chứa các class giao diện người dùng tương tác.

## 🛠️ Package: com.pbl_3project.util
Các utility classes hỗ trợ:

- **DatabaseInitializer**: Khởi tạo cấu hình và kết nối database.

## 🎯 Cách sử dụng các tầng (Layer)
Tầng GUI sẽ gọi đến tầng BUS để xử lý nghiệp vụ. Tầng BUS sẽ gọi đến tầng DAO để truy xuất dữ liệu. Tầng DAO sẽ gọi trực tiếp xuống SQL Server Database. Dữ liệu được truyền tải giữa các tầng qua DTO, hoặc lấy trực tiếp từ ResultSet.
