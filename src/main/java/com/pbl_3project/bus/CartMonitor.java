package com.pbl_3project.bus;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Timer;
import java.util.TimerTask;
import com.pbl_3project.util.DatabaseConnection;
public class CartMonitor {
    private static Timer timer;
    public static void startMonitoring() {
        if (timer != null)
            return;
        timer = new Timer(true); 
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Connection conn = DatabaseConnection.getConnection();
                    CallableStatement cstmt = conn.prepareCall("{call sp_ClearExpiredCart}");
                    cstmt.execute();
                    cstmt.close();
                    DatabaseConnection.closeConnection(conn);
                } catch (Exception e) {
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