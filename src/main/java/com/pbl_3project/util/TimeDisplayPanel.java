package com.pbl_3project.util;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
public class TimeDisplayPanel extends JPanel {
    private JLabel lblTime;
    private Timer updateTimer;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss | dd/MM/yyyy");
    public TimeDisplayPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(59, 190, 210));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 15, 8, 15));
        lblTime = new JLabel();
        lblTime.setForeground(Color.WHITE);
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTime.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lblTime, BorderLayout.EAST);
        updateTime();
        startTimer();
    }
    private void updateTime() {
        String currentTime = LocalDateTime.now().format(TIME_FORMAT);
        lblTime.setText("" + currentTime);
    }
    private void startTimer() {
        updateTimer = new Timer(true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTime();
            }
        }, 1000, 1000); 
    }
    public void stopTimer() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
    }
    @Override
    public void finalize() throws Throwable {
        try {
            stopTimer();
        } finally {
            super.finalize();
        }
    }
}
