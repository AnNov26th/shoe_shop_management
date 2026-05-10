package com.pbl_3project;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.pbl_3project.bus.CartMonitor;
import com.pbl_3project.gui.LoginForm;
import com.pbl_3project.util.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        CartMonitor.startMonitoring();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
