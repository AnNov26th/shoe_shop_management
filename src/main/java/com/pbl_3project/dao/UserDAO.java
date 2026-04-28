package com.pbl_3project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;

import com.pbl_3project.util.DatabaseConnection;

public class UserDAO {

    /**
     * Hàm kiểm tra đăng nhập
     * 
     * @return > 0 (role_id) nếu thành công
     * @return 0 nếu sai tài khoản / mật khẩu
     * @return -1 nếu tài khoản bị Banned
     */
    public int checkLogin(String email, String password) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int result = 0; // Mặc định là 0 (Sai thông tin)

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null)
                throw new SQLException("Không thể kết nối DB!");

            String sql = "SELECT role_id, status FROM [User] WHERE email = ? AND password_hash = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                if ("Banned".equalsIgnoreCase(status)) {
                    result = -1; // Tài khoản bị khóa
                } else {
                    result = rs.getInt("role_id"); // Trả về mã quyền (1, 2, 3, 4)
                }
            }
        } finally {
            // Đóng kết nối
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }

        return result;
    }

    public javax.swing.table.DefaultTableModel getAllUsers() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String[] columnNames = { "ID", "Tên nhân viên", "Email", "Số điện thoại", "Quyền", "Trạng thái" };
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columnNames, 0);

        try {
            conn = DatabaseConnection.getConnection();

            // Dùng LEFT JOIN để kết nối bảng User và Customer_Profile
            String sql = "SELECT u.id, cp.full_name, u.email, u.phone, r.name AS role_name, u.status " +
                    "FROM [User] u " +
                    "JOIN Role r ON u.role_id = r.id " +
                    "LEFT JOIN Customer_Profile cp ON u.id = cp.user_id " +
                    " WHERE u.role_id IN (1, 2, 3)";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                // Nếu full_name null (do chưa có profile), thì gán chuỗi rỗng để bảng không bị
                // lỗi chữ "null"
                String fullName = rs.getString("full_name");
                if (fullName == null)
                    fullName = "Chưa cập nhật";

                Object[] row = {
                        rs.getInt("id"),
                        fullName, // Lấy tên từ bảng Customer_Profile
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("role_name"),
                        rs.getString("status")
                };
                tableModel.addRow(row);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return tableModel;
    }

    /**
     * Hàm thêm một nhân viên mới vào Database
     */
    public boolean addUser(String name, String email, String password, String phone, int roleId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtUser = null;
        PreparedStatement pstmtProfile = null;
        ResultSet rsKeys = null;
        boolean isSuccess = false;

        try {
            conn = DatabaseConnection.getConnection();
            // Tắt auto-commit để thực hiện Transaction (đảm bảo insert cả 2 bảng cùng thành
            // công hoặc cùng hủy)
            conn.setAutoCommit(false);

            // 1. INSERT VÀO BẢNG [User] TRƯỚC
            String sqlUser = "INSERT INTO [User] (role_id, email, password_hash, phone, status) VALUES (?, ?, ?, ?, 'Active')";
            // Lệnh RETURN_GENERATED_KEYS chính là SCOPE_IDENTITY() trong Java
            pstmtUser = conn.prepareStatement(sqlUser, java.sql.Statement.RETURN_GENERATED_KEYS);
            pstmtUser.setInt(1, roleId);
            pstmtUser.setString(2, email);
            pstmtUser.setString(3, password);
            pstmtUser.setString(4, phone);

            int rowsAffected = pstmtUser.executeUpdate();

            if (rowsAffected > 0) {
                // 2. LẤY CÁI ID VỪA ĐƯỢC TẠO TỰ ĐỘNG
                rsKeys = pstmtUser.getGeneratedKeys();
                int newUserId = -1;
                if (rsKeys.next()) {
                    newUserId = rsKeys.getInt(1);
                }

                // 3. INSERT TÊN VÀO BẢNG Customer_Profile BẰNG CÁI ID VỪA LẤY
                if (newUserId != -1) {
                    String sqlProfile = "INSERT INTO Customer_Profile (user_id, full_name) VALUES (?, ?)";
                    pstmtProfile = conn.prepareStatement(sqlProfile);
                    pstmtProfile.setInt(1, newUserId);
                    pstmtProfile.setNString(2, name); // setNString để hỗ trợ tiếng Việt Unicode
                    pstmtProfile.executeUpdate();
                }

                // Xác nhận lưu vào Database
                conn.commit();
                isSuccess = true;
            }
        } catch (SQLException e) {
            // Nếu có lỗi ở 1 trong 2 bảng, hoàn tác (rollback) lại toàn bộ để tránh rác dữ
            // liệu
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (rsKeys != null)
                rsKeys.close();
            if (pstmtProfile != null)
                pstmtProfile.close();
            if (pstmtUser != null)
                pstmtUser.close();
            // Trả lại trạng thái auto-commit bình thường cho connection
            if (conn != null)
                conn.setAutoCommit(true);
            DatabaseConnection.closeConnection(conn);
        }

        return isSuccess;
    }

    public DefaultTableModel searchUsers(String keyword) throws SQLException {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rs = null;

        String[] columnNames = { "ID", "Họ Tên", "Email", "SĐT", "Quyền", "Trạng thái" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            conn = DatabaseConnection.getConnection();
            // ĐÃ THÊM: role_id IN (1, 2, 3)
            String sql = "SELECT id, full_name, email, phone, role_id, status FROM [User] " +
                    "WHERE role_id IN (1, 2, 3) ";

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql += "AND (full_name LIKE ? OR email LIKE ? OR phone LIKE ?) ";
            }
            sql += "ORDER BY id DESC";

            pstmt = conn.prepareStatement(sql);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                String roleName = rs.getInt("role_id") == 1 ? "Admin"
                        : (rs.getInt("role_id") == 2 ? "Manager" : "Staff");
                String name = rs.getString("full_name") != null ? rs.getString("full_name") : "Chưa cập nhật";

                model.addRow(new Object[] {
                        rs.getInt("id"), name, rs.getString("email"),
                        rs.getString("phone"), roleName, rs.getString("status")
                });
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return model;
    }

    public DefaultTableModel searchCustomers(String keyword) throws SQLException {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rs = null;

        // Bảng khách hàng có thêm cột Điểm Thưởng
        String[] columnNames = { "ID", "Họ Tên", "Email", "SĐT", "Điểm thưởng", "Trạng thái" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT u.id, c.full_name, u.email, u.phone, c.reward_points, u.status " +
                    "FROM [User] u JOIN Customer_Profile c ON u.id = c.user_id " +
                    "WHERE u.role_id = 4 ";

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql += "AND (c.full_name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?) ";
            }
            sql += "ORDER BY u.id DESC";

            pstmt = conn.prepareStatement(sql);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"), rs.getString("full_name"), rs.getString("email"),
                        rs.getString("phone"), rs.getInt("reward_points"), rs.getString("status")
                });
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return model;
    }

    public boolean deleteUser(int id) throws SQLException {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();

            // Chuyển trạng thái sang Inactive thay vì xóa hẳn
            String sql = "UPDATE [User] SET status = 'Inactive' WHERE id = ? AND role_id IN (2, 3)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }

    public int getUserIdByEmail(String email) throws SQLException {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rs = null;
        int userId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT id FROM [User] WHERE email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("id"); // Tóm gọn ID của nhân viên
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return userId;
    }

    public java.util.Map<String, String> getCustomerProfile(int customerId) throws SQLException {
        java.util.Map<String, String> profile = new java.util.HashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT u.email, u.password_hash, u.phone, cp.full_name, cp.dob, cp.shoe_size_preference, ab.full_address "
                    +
                    "FROM [User] u " +
                    "LEFT JOIN Customer_Profile cp ON u.id = cp.user_id " +
                    "LEFT JOIN Address_Book ab ON u.id = ab.user_id AND ab.is_default = 1 " +
                    "WHERE u.id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                profile.put("email", rs.getString("email"));
                profile.put("password", rs.getString("password_hash")); // In a real app, don't return hash, but here we
                                                                        // might just display it or empty
                profile.put("phone", rs.getString("phone"));

                String fullName = rs.getString("full_name");
                if (fullName != null && !fullName.trim().isEmpty()) {
                    int lastSpace = fullName.lastIndexOf(" ");
                    if (lastSpace != -1) {
                        profile.put("firstName", fullName.substring(0, lastSpace));
                        profile.put("lastName", fullName.substring(lastSpace + 1));
                    } else {
                        profile.put("firstName", "");
                        profile.put("lastName", fullName);
                    }
                } else {
                    profile.put("firstName", "");
                    profile.put("lastName", "");
                }

                java.sql.Date dob = rs.getDate("dob");
                profile.put("dob", dob != null ? dob.toString() : "");

                java.math.BigDecimal shoeSize = rs.getBigDecimal("shoe_size_preference");
                profile.put("shoeSize", shoeSize != null ? shoeSize.toString() : "");

                profile.put("address", rs.getString("full_address"));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return profile;
    }

    public boolean updateCustomerProfile(int customerId, java.util.Map<String, String> profile) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtUser = null;
        PreparedStatement pstmtProfile = null;
        PreparedStatement pstmtProfileCheck = null;
        PreparedStatement pstmtAddress = null;
        PreparedStatement pstmtAddressCheck = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Update User table
            String sqlUser;
            if (profile.get("password") != null && !profile.get("password").isEmpty()) {
                sqlUser = "UPDATE [User] SET email = ?, phone = ?, password_hash = ? WHERE id = ?";
                pstmtUser = conn.prepareStatement(sqlUser);
                pstmtUser.setString(1, profile.get("email"));
                pstmtUser.setString(2, profile.get("phone"));
                pstmtUser.setString(3, profile.get("password"));
                pstmtUser.setInt(4, customerId);
            } else {
                sqlUser = "UPDATE [User] SET email = ?, phone = ? WHERE id = ?";
                pstmtUser = conn.prepareStatement(sqlUser);
                pstmtUser.setString(1, profile.get("email"));
                pstmtUser.setString(2, profile.get("phone"));
                pstmtUser.setInt(3, customerId);
            }
            pstmtUser.executeUpdate();

            // 2. Update Customer_Profile
            String fullName = (profile.get("firstName") + " " + profile.get("lastName")).trim();

            pstmtProfileCheck = conn.prepareStatement("SELECT user_id FROM Customer_Profile WHERE user_id = ?");
            pstmtProfileCheck.setInt(1, customerId);
            rs = pstmtProfileCheck.executeQuery();

            if (rs.next()) {
                // Update
                String sqlProfile = "UPDATE Customer_Profile SET full_name = ?, dob = ?, shoe_size_preference = ? WHERE user_id = ?";
                pstmtProfile = conn.prepareStatement(sqlProfile);
                pstmtProfile.setNString(1, fullName);
                if (profile.get("dob") != null && !profile.get("dob").isEmpty()) {
                    pstmtProfile.setDate(2, java.sql.Date.valueOf(profile.get("dob")));
                } else {
                    pstmtProfile.setNull(2, java.sql.Types.DATE);
                }
                if (profile.get("shoeSize") != null && !profile.get("shoeSize").isEmpty()) {
                    pstmtProfile.setBigDecimal(3, new java.math.BigDecimal(profile.get("shoeSize")));
                } else {
                    pstmtProfile.setNull(3, java.sql.Types.DECIMAL);
                }
                pstmtProfile.setInt(4, customerId);
                pstmtProfile.executeUpdate();
            } else {
                // Insert
                String sqlProfile = "INSERT INTO Customer_Profile (user_id, full_name, dob, shoe_size_preference) VALUES (?, ?, ?, ?)";
                pstmtProfile = conn.prepareStatement(sqlProfile);
                pstmtProfile.setInt(1, customerId);
                pstmtProfile.setNString(2, fullName);
                if (profile.get("dob") != null && !profile.get("dob").isEmpty()) {
                    pstmtProfile.setDate(3, java.sql.Date.valueOf(profile.get("dob")));
                } else {
                    pstmtProfile.setNull(3, java.sql.Types.DATE);
                }
                if (profile.get("shoeSize") != null && !profile.get("shoeSize").isEmpty()) {
                    pstmtProfile.setBigDecimal(4, new java.math.BigDecimal(profile.get("shoeSize")));
                } else {
                    pstmtProfile.setNull(4, java.sql.Types.DECIMAL);
                }
                pstmtProfile.executeUpdate();
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }

            // 3. Update Address_Book
            if (profile.get("address") != null && !profile.get("address").isEmpty()) {
                pstmtAddressCheck = conn
                        .prepareStatement("SELECT id FROM Address_Book WHERE user_id = ? AND is_default = 1");
                pstmtAddressCheck.setInt(1, customerId);
                rs = pstmtAddressCheck.executeQuery();

                if (rs.next()) {
                    // Update
                    int addressId = rs.getInt("id");
                    String sqlAddress = "UPDATE Address_Book SET full_address = ?, receiver_name = ?, phone = ? WHERE id = ?";
                    pstmtAddress = conn.prepareStatement(sqlAddress);
                    pstmtAddress.setNString(1, profile.get("address"));
                    pstmtAddress.setNString(2, fullName);
                    pstmtAddress.setString(3, profile.get("phone"));
                    pstmtAddress.setInt(4, addressId);
                    pstmtAddress.executeUpdate();
                } else {
                    // Insert
                    String sqlAddress = "INSERT INTO Address_Book (user_id, full_address, receiver_name, phone, is_default) VALUES (?, ?, ?, ?, 1)";
                    pstmtAddress = conn.prepareStatement(sqlAddress);
                    pstmtAddress.setInt(1, customerId);
                    pstmtAddress.setNString(2, profile.get("address"));
                    pstmtAddress.setNString(3, fullName);
                    pstmtAddress.setString(4, profile.get("phone"));
                    pstmtAddress.executeUpdate();
                }
            }

            conn.commit();
            success = true;
        } catch (Exception e) {
            if (conn != null)
                conn.rollback();
            throw new SQLException(e);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmtUser != null)
                pstmtUser.close();
            if (pstmtProfile != null)
                pstmtProfile.close();
            if (pstmtProfileCheck != null)
                pstmtProfileCheck.close();
            if (pstmtAddress != null)
                pstmtAddress.close();
            if (pstmtAddressCheck != null)
                pstmtAddressCheck.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
        return success;
    }

    public java.util.Map<String, String> findCustomerByPhone(String phone) throws SQLException {
        java.util.Map<String, String> customer = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT u.id, cp.full_name, u.email FROM [User] u " +
                         "JOIN Customer_Profile cp ON u.id = cp.user_id " +
                         "WHERE u.phone = ? AND u.role_id = 4";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, phone);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                customer = new java.util.HashMap<>();
                customer.put("id", String.valueOf(rs.getInt("id")));
                customer.put("name", rs.getString("full_name"));
                customer.put("email", rs.getString("email"));
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return customer;
    }

    public int createQuickCustomer(String name, String phone, String email, String password) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtUser = null;
        PreparedStatement pstmtProfile = null;
        ResultSet rsKeys = null;
        int newUserId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlUser = "INSERT INTO [User] (role_id, email, password_hash, phone, status) VALUES (4, ?, ?, ?, 'Active')";
            pstmtUser = conn.prepareStatement(sqlUser, java.sql.Statement.RETURN_GENERATED_KEYS);
            pstmtUser.setString(1, (email != null && !email.isEmpty()) ? email : "guest_" + phone + "@shoes.com");
            pstmtUser.setString(2, (password != null && !password.isEmpty()) ? password : "password123");
            pstmtUser.setString(3, phone);

            int rowsAffected = pstmtUser.executeUpdate();
            if (rowsAffected > 0) {
                rsKeys = pstmtUser.getGeneratedKeys();
                if (rsKeys.next()) {
                    newUserId = rsKeys.getInt(1);
                }

                if (newUserId != -1) {
                    String sqlProfile = "INSERT INTO Customer_Profile (user_id, full_name) VALUES (?, ?)";
                    pstmtProfile = conn.prepareStatement(sqlProfile);
                    pstmtProfile.setInt(1, newUserId);
                    pstmtProfile.setNString(2, name);
                    pstmtProfile.executeUpdate();
                }
                conn.commit();
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (rsKeys != null) rsKeys.close();
            if (pstmtProfile != null) pstmtProfile.close();
            if (pstmtUser != null) pstmtUser.close();
            if (conn != null) conn.setAutoCommit(true);
            DatabaseConnection.closeConnection(conn);
        }
        return newUserId;
    }
}