package com.pbl_3project.bus;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Timer;
import java.util.TimerTask;

import com.pbl_3project.util.DatabaseConnection;

public class CartMonitor {
    private static Timer timer;

    // Hàm này sẽ được gọi 1 lần duy nhất khi phần mềm mới bật lên
    public static void startMonitoring() {
        if (timer != null)
            return;

        timer = new Timer(true); // true = Luồng chạy ngầm (Daemon), tắt app là nó tự chết theo

        // Cài đặt lặp lại sau mỗi 30 giây (30000 ms)
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Connection conn = DatabaseConnection.getConnection();
                    // Kích hoạt máy dọn rác dưới Database
                    CallableStatement cstmt = conn.prepareCall("{call sp_ClearExpiredCart}");
                    cstmt.execute();

                    cstmt.close();
                    DatabaseConnection.closeConnection(conn);
                } catch (Exception e) {
                    // Chạy ngầm nên nếu có lỗi mạng giật lag xíu thì bỏ qua, 30s sau nó quét lại
                }
            }
        }, 0, 30000);
    }

    public static void stopMonitoring() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}