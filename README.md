# Shoe Shop Management System - Hướng Dẫn Cài Đặt (Setup Guide)

Chào mừng bạn đến với project Shoe Shop Management! Dưới đây là hướng dẫn chi tiết từng bước để cấu hình SQL Server, thay đổi chuỗi kết nối (connection string) và chạy project sau khi bạn clone (tải) từ GitHub về máy.

---

## 🛠️ Yêu Cầu Hệ Thống
Trước khi bắt đầu, hãy chắc chắn máy của bạn đã cài đặt:
- **Java JDK 21** trở lên.
- **Maven** (3.8+).
- **SQL Server 2022** (phiên bản Express hoặc Developer đều được).
- **SQL Server Management Studio (SSMS)**.
- IDE lập trình Java (IntelliJ IDEA, Eclipse, hoặc VS Code).

---

## Bước 1: Cấu Hình SQL Server Configuration Manager (Rất Quan Trọng)

Để Java có thể kết nối được với SQL Server qua JDBC, bạn **bắt buộc** phải bật giao thức TCP/IP.

1. Nhấn nút Windows (Start Menu), tìm kiếm và mở **SQL Server 2022 Configuration Manager**.
2. Ở menu bên trái, mở rộng mục **SQL Server Network Configuration** > Chọn **Protocols for SQLEXPRESS** (hoặc tên Instance mà bạn đã cài).
3. Ở khung bên phải, tìm giao thức **TCP/IP**. Nếu trạng thái đang là `Disabled`, hãy click chuột phải và chọn **Enable**.
4. Tiếp tục **click chuột phải vào TCP/IP** > Chọn **Properties**.
5. Chuyển sang tab **IP Addresses**:
   - Cuộn xuống dưới cùng đến phần **IPAll**.
   - Ở ô **TCP Dynamic Ports**, hãy **xóa trắng** (không để số 0).
   - Ở ô **TCP Port**, nhập vào **1433**.
   - Nhấn **Apply** và **OK**.
6. Ở menu bên trái, chọn lại mục **SQL Server Services**.
7. Click chuột phải vào **SQL Server (SQLEXPRESS)** và chọn **Restart** để khởi động lại dịch vụ máy chủ và áp dụng cài đặt TCP/IP mới.

*(Lưu ý bổ sung: Nếu bạn kết nối không được, hãy đảm bảo service **SQL Server Browser** cũng đang ở trạng thái `Running`)*.

---

## Bước 2: Khởi Tạo Database Bằng SSMS

1. Mở **SQL Server Management Studio (SSMS)**.
2. Kết nối (Connect) vào Server Name của bạn (Thường là `localhost\SQLEXPRESS` hoặc `.`).
   - *Mẹo: Nếu Java báo lỗi đăng nhập ở bước sau, hãy chắc chắn server của bạn đã bật chế độ **SQL Server and Windows Authentication mode** trong Server Properties > Security, và tài khoản `sa` đã được Enable.*
3. Mở file script SQL: 
   - Đi tới `File` > `Open` > `File...`
   - Tìm đến thư mục chứa project vừa clone về, chọn file: `database/ShopGiayDB_script_4-5-2026.sql`.
4. Nhấn nút **Execute** (hoặc phím `F5`) để chạy toàn bộ file script. 
5. Sau khi báo chạy thành công, ở mục Object Explorer bên trái, chuột phải vào thư mục **Databases** > chọn **Refresh**, bạn sẽ thấy database `ShopGiayDB` xuất hiện với đầy đủ các bảng.

---

## Bước 3: Cấu Hình Connection String Trong Code Java

Bây giờ bạn cần trỏ code Java vào đúng database của bạn.

1. Mở project trong IDE (IntelliJ, Eclipse, VS Code).
2. Mở file: `src/main/java/com/pbl_3project/util/DatabaseConnection.java`.
3. Tìm đến các hằng số sau và sửa đổi cho phù hợp với SQL Server của máy bạn:

```java
public class DatabaseConnection {
    // 1. Nếu server không phải máy bạn, hãy đổi IP. Nếu là máy cá nhân, giữ nguyên "localhost"
    private static final String SERVER_NAME = "localhost";
    
    // 2. Tên Instance SQL Server của bạn. 
    // Nếu bạn cài bản Developer mặc định, có thể để trống: ""
    // Nếu bạn cài bản Express mặc định, giữ nguyên "SQLEXPRESS"
    private static final String INSTANCE_NAME = "SQLEXPRESS";
    
    private static final String DATABASE_NAME = "ShopGiayDB";
    
    // 3. Tên đăng nhập SQL Server (thường dùng tài khoản sa)
    private static final String USERNAME = "sa";
    
    // 4. Mật khẩu của tài khoản sa trên máy bạn
    private static final String PASSWORD = "123456"; 
    
    // ...
}
```

---

## Bước 4: Chạy Project

1. Nếu bạn dùng IDE, hãy đợi IDE tự động load và tải các thư viện Maven (pom.xml). 
2. Có thể gõ lệnh `mvn clean compile` để đảm bảo code build thành công và dọn dẹp các cache cũ.
3. Chạy file `Main.java` (nằm ở `src/main/java/com/pbl_3project/Main.java`).
4. Giao diện Đăng nhập của ứng dụng quản lý cửa hàng giày sẽ hiện lên. Chúc bạn code vui vẻ!

---
*Mọi thắc mắc về cấu trúc chi tiết của Class hoặc cấu trúc chi tiết của các Bảng trong Database, vui lòng tham khảo 2 file `CLASS_STRUCTURE.md` và `DATABASE_GUIDE.md`.*
