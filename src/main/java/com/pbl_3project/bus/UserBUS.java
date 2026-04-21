package com.pbl_3project.bus;

import java.sql.SQLException;

import com.pbl_3project.dao.UserDAO;

public class UserBUS {
    private UserDAO userDAO;

    public UserBUS() {
        userDAO = new UserDAO(); // Gọi đệ tử DAO lên để chọc vào DB
    }

    // TẤT CẢ LOGIC NẰM Ở ĐÂY
    // Hàm này sẽ trả về các mã trạng thái (Code) để GUI biết đường mà báo lỗi
    public String xulyDangNhap(String email, String password) {
        // 1. Logic kiểm tra rỗng
        if (email.isEmpty() || password.isEmpty()) {
            return "EMPTY_FIELD";
        }

        // 2. Logic kiểm tra định dạng email (VD: phải có chữ @)
        if (!email.contains("@")) {
            return "INVALID_EMAIL";
        }

        // 3. Logic xử lý kết quả từ DB
        try {
            int result = userDAO.checkLogin(email, password);
            if (result == -1) {
                return "BANNED"; // Tài khoản bị khóa
            } else if (result > 0) {
                return "SUCCESS_" + result; // Thành công, nối kèm role_id (VD: SUCCESS_1)
            } else {
                return "WRONG_INFO"; // Sai mật khẩu
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "DB_ERROR"; // Lỗi kết nối
        }
    }
}