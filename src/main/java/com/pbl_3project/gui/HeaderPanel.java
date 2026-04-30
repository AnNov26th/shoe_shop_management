package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class HeaderPanel extends JPanel {
    private JLabel lblTime;
    private Timer updateTimer;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss | dd/MM/yyyy");

    private int userId;
    private int roleId;

    public HeaderPanel(int userId, int roleId) {
        this.userId = userId;
        this.roleId = roleId;

        setLayout(new BorderLayout());
        setBackground(new Color(59, 190, 210));
        setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        lblTime = new JLabel();
        lblTime.setForeground(Color.WHITE);
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTime.setHorizontalAlignment(SwingConstants.LEFT);
        add(lblTime, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);

        JButton btnProfile = new JButton("Hồ sơ cá nhân") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnProfile.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnProfile.setForeground(Color.WHITE);
        btnProfile.setContentAreaFilled(false);
        btnProfile.setFocusPainted(false);
        btnProfile.setBorderPainted(false);
        btnProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProfile.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        btnProfile.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            ProfileDialog dialog = new ProfileDialog(parentWindow, this.userId, this.roleId);
            dialog.setVisible(true);
        });

        rightPanel.add(btnProfile);
        add(rightPanel, BorderLayout.EAST);

        updateTime();
        startTimer();
    }

    private void updateTime() {
        String currentTime = LocalDateTime.now().format(TIME_FORMAT);
        lblTime.setText(currentTime);
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
