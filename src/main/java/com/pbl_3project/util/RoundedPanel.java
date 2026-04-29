package com.pbl_3project.util;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
public class RoundedPanel extends JPanel {
    private int cornerRadius;
    public RoundedPanel(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        setOpaque(false); 
    }
    public RoundedPanel() {
        this(20); 
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        super.paintComponent(g);
    }
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}
