# Shoe Shop Management System - Class Structure

## 📦 Package: com.pbl_3project.enums
Các enum định nghĩa hằng số cho hệ thống:

- **Role**: ADMIN, EMPLOYEE, CUSTOMER
- **OrderStatus**: PENDING, CONFIRMED, PACKING, SHIPPING, COMPLETED, CANCELLED
- **PaymentMethod**: COD, BANK_TRANSFER, MOMO
- **PaymentStatus**: UNPAID, PAID, REFUNDED
- **DiscountType**: PERCENT, FIXED
- **StockAdjustmentType**: DAMAGED, LOST, MANUAL_ADJUST
- **ReturnStatus**: PENDING, APPROVED, REJECTED
- **PromotionType**: FLASH_SALE, BUY_1_GET_1, CATEGORY_DISCOUNT
- **Gender**: MALE, FEMALE, UNISEX
- **PaymentProvider**: MOMO, VNPAY, STRIPE, NONE

## 🏛️ Package: com.pbl_3project.entity
Các entity/model classes đại diện cho dữ liệu:

### User Hierarchy
- **User**: Lớp cơ sở cho tất cả người dùng
  - `id`, `username`, `fullName`, `email`, `phone`, `passwordHash`, `role`, `isActive`
- **Customer** (extends User): Khách hàng
  - `addresses`, `orderIds`, `cart`, `shoeSize`, `loyaltyPoints`, `membershipLevel`
- **Employee** (extends User): Nhân viên
  - `commissionRate`, `totalSales`, `ordersProcessed`
- **Admin** (extends User): Quản trị viên
  - `department`, `position`, `createdEmployeeCount`

### Product & Inventory
- **Category**: Danh mục sản phẩm
- **Product**: Sản phẩm giày
- **ProductVariant**: Biến thể sản phẩm (kích thước, màu sắc)
- **Supplier**: Nhà cung cấp
- **ImportReceipt**: Phiếu nhập hàng
- **ImportItem**: Mục hàng trong phiếu nhập
- **StockAdjustment**: Điều chỉnh tồn kho
- **StockLog**: Lịch sử thay đổi tồn kho

### Order & Shopping
- **Cart**: Giỏ hàng của khách
- **CartItem**: Mục hàng trong giỏ
- **Order**: Đơn hàng
- **OrderItem**: Mục sản phẩm trong đơn hàng
- **ReturnRequest**: Yêu cầu đổi trả

### Marketing & Reviews
- **Discount**: Mã giảm giá
- **Promotion**: Chương trình khuyến mãi
- **Review**: Đánh giá sản phẩm từ khách
- **Address**: Địa chỉ giao hàng
- **Permission**: Quyền hạn của nhân viên

## 📊 Package: com.pbl_3project.dao
DAO (Data Access Object) - Interface để tương tác với database:

- **IGenericDAO<T,K>**: Interface cơ sở cho tất cả DAO
  - `findById()`, `findAll()`, `save()`, `update()`, `delete()`, `count()`
- **IUserDAO**: DAO cho User
  - `findByUsername()`, `findByEmail()`, `existsByUsername()`, `existsByEmail()`
- **IProductDAO**: DAO cho Product
  - `findByName()`, `findByCategory()`, `findByPriceRange()`, `findActive()`
- **IOrderDAO**: DAO cho Order
  - `findByCustomerId()`, `findByStatus()`, `findByDateRange()`, `getTotalRevenue()`
- **ICategoryDAO**: DAO cho Category
  - `findByName()`, `findActive()`, `existsByName()`
- **IDiscountDAO**: DAO cho Discount
  - `findByCode()`, `findActive()`, `findExpired()`, `findAvailable()`

## 🔧 Package: com.pbl_3project.service
Service layer - Xử lý business logic:

- **IAuthService**: Xác thực người dùng
  - `login()`, `logout()`, `register()`, `changePassword()`, `verifyPassword()`
- **IProductService**: Quản lý sản phẩm
  - `getAllProducts()`, `searchProduct()`, `checkStock()`, `decreaseStock()`, `increaseStock()`
- **IOrderService**: Quản lý đơn hàng
  - `createOrder()`, `addItemToOrder()`, `applyDiscount()`, `updateOrderStatus()`, `calculateOrderTotal()`
- **ICustomerService**: Quản lý khách hàng
  - `getCustomerId()`, `addToCart()`, `clearCart()`, `createReview()`, `addLoyaltyPoints()`, `redeemPoints()`

## 🛠️ Package: com.pbl_3project.util
Các utility classes hỗ trợ:

- **IDGenerator**: Tạo ID duy nhất cho các entity
  - `generateUserId()`, `generateProductId()`, `generateOrderId()`, `generateSKU()`
- **PasswordUtil**: Xử lý mật khẩu
  - `hashPassword()`, `verifyPassword()`, `generateRandomPassword()`, `isStrongPassword()`
- **DateUtil**: Xử lý ngày tháng
  - `formatDate()`, `formatStandard()`, `daysBetween()`, `isToday()`
- **ValidationUtil**: Validate dữ liệu
  - `isValidEmail()`, `isValidPhone()`, `isValidUsername()`, `isValidPrice()`, `isValidRating()`

## 🎯 Cách sử dụng:

### Tạo người dùng mới:
```java
User user = new User("USR001", "john_doe", "John Doe", "john@email.com", "0123456789", 
                     PasswordUtil.hashPassword("password123"), Role.CUSTOMER);
```

### Tạo sản phẩm:
```java
Product product = new Product("PRD001", "Nike Air Max", "CAT001", 2500000);
ProductVariant variant = new ProductVariant("SKU001", "42", "Đen", 50, 2500000);
product.addVariant(variant);
```

### Tạo đơn hàng:
```java
Order order = new Order("ORD001", "CUST001", OrderStatus.PENDING, PaymentMethod.COD);
order.addItem(variant, 2);
order.calculateTotal();
```

### Quản lý giỏ hàng:
```java
Customer customer = ... // Lấy từ database
customer.addToCart(variant, 3);
double total = customer.getCart().calculateTotal();
```

## 📝 Notes:
- Tất cả entity đều implement Serializable cho phép lưu trữ
- Các entity có `equals()` và `hashCode()` để dùng trong collection
- Validation diễn ra ở constructor của mỗi entity
- Service layer sẽ được implement để tương tác với DAO và business logic
