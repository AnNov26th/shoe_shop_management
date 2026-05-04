package com.pbl_3project.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class ChartComponent extends JPanel {
    public enum ChartType { BAR, PIE }
    private final ChartType type;
    private Map<String, Double> data;
    private final String title;
    private int hoverIndex = -1;
    private double animationProgress = 0.0;
    private Timer animationTimer;
    
    private final Color[] colors = {
        new Color(59, 130, 246), new Color(16, 185, 129), 
        new Color(245, 158, 11), new Color(239, 68, 68), 
        new Color(139, 92, 246), new Color(236, 72, 153),
        new Color(20, 184, 166), new Color(100, 116, 139)
    };

    public ChartComponent(ChartType type, String title, Map<String, Double> data) {
        this.type = type;
        this.title = title;
        this.data = data;
        setOpaque(false);
        setPreferredSize(new Dimension(300, 300));
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                checkHover(e.getPoint());
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverIndex = -1;
                repaint();
            }
        });
    }

    public void setData(Map<String, Double> data) {
        this.data = data;
        startAnimation();
    }

    private void startAnimation() {
        animationProgress = 0.0;
        if (animationTimer != null) animationTimer.stop();
        animationTimer = new Timer(20, e -> {
            animationProgress += 0.05;
            if (animationProgress >= 1.0) {
                animationProgress = 1.0;
                animationTimer.stop();
            }
            repaint();
        });
        animationTimer.start();
    }

    private void checkHover(Point p) {
        if (data == null || data.isEmpty()) return;
        int oldHover = hoverIndex;
        
        if (type == ChartType.PIE) {
            hoverIndex = getPieIndexAt(p);
        } else {
            hoverIndex = getBarIndexAt(p);
        }
        
        if (oldHover != hoverIndex) repaint();
    }

    private int getPieIndexAt(Point p) {
        int w = getWidth(), h = getHeight();
        int size = Math.min(w, h) - 120;
        int x = (w - size) / 2, y = (h - size) / 2 + 10;
        
        double centerX = x + size / 2.0;
        double centerY = y + size / 2.0;
        double dist = p.distance(centerX, centerY);
        
        if (dist > size / 2.0) return -1;
        
        double angle = Math.toDegrees(Math.atan2(centerY - p.y, p.x - centerX));
        if (angle < 0) angle += 360;
        
        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        double startAngle = 0;
        int i = 0;
        for (Double val : data.values()) {
            double slice = (val / total) * 360;
            if (angle >= startAngle && angle < startAngle + slice) return i;
            startAngle += slice;
            i++;
        }
        return -1;
    }

    private int getBarIndexAt(Point p) {
        int w = getWidth(), h = getHeight();
        int margin = 60;
        int chartW = w - 100;
        int barW = chartW / data.size() - 10;
        int x = margin + 25;
        
        int i = 0;
        for (Double val : data.values()) {
            Rectangle rect = new Rectangle(x, margin, barW, h - 2 * margin);
            if (p.x >= x && p.x <= x + barW) return i;
            x += barW + 10;
            i++;
        }
        return -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setColor(new Color(30, 41, 59));
        drawCenteredString(g2, title, w / 2, 25);

        if (type == ChartType.PIE) {
            drawPieChart(g2, w, h);
        } else {
            drawBarChart(g2, w, h);
        }
        g2.dispose();
    }

    private void drawPieChart(Graphics2D g2, int w, int h) {
        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        int size = Math.min(w, h) - 120;
        int x = (w - size) / 2, y = (h - size) / 2 + 10;

        double startAngle = 0;
        int i = 0;
        String tooltipText = null;
        
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double angle = (entry.getValue() / total) * 360 * animationProgress;
            g2.setColor(colors[i % colors.length]);
            
            if (i == hoverIndex) {
                g2.setColor(g2.getColor().brighter());
                // Pop out effect
                double midAngle = Math.toRadians(startAngle + angle / 2.0);
                int ox = (int) (Math.cos(midAngle) * 8);
                int oy = (int) (-Math.sin(midAngle) * 8);
                g2.fillArc(x + ox, y + oy, size, size, (int) startAngle, (int) angle + 1);
                
                double percent = (entry.getValue() / total) * 100;
                tooltipText = String.format("%s: %.1f%%", entry.getKey(), percent);
            } else {
                g2.fillArc(x, y, size, size, (int) startAngle, (int) angle + 1);
            }
            
            startAngle += (entry.getValue() / total) * 360;
            i++;
        }
        
        // Donut hole for modern look
        g2.setColor(Color.WHITE);
        int holeSize = size / 2;
        g2.fillOval(x + (size - holeSize) / 2, y + (size - holeSize) / 2, holeSize, holeSize);

        if (tooltipText != null) {
            drawTooltip(g2, tooltipText);
        }
    }

    private void drawBarChart(Graphics2D g2, int w, int h) {
        double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        int margin = 60;
        int chartH = h - 120;
        int chartW = w - 100;
        int barW = chartW / data.size() - 10;

        int x = margin + 25;
        int i = 0;
        String tooltipText = null;
        
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            int barH = (int) ((entry.getValue() / max) * chartH * animationProgress);
            Color baseColor = colors[i % colors.length];
            
            if (i == hoverIndex) {
                g2.setColor(baseColor.brighter());
                tooltipText = entry.getKey() + ": " + entry.getValue().intValue();
            } else {
                g2.setColor(baseColor);
            }
            
            g2.fillRoundRect(x, h - margin - barH, barW, barH, 8, 8);
            
            // X-Axis labels
            g2.setColor(new Color(100, 116, 139));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String key = entry.getKey();
            if (key.length() > 8) key = key.substring(0, 6) + "..";
            drawCenteredString(g2, key, x + barW / 2, h - margin + 15);

            x += barW + 10;
            i++;
        }
        
        if (tooltipText != null) drawTooltip(g2, tooltipText);
    }

    private void drawTooltip(Graphics2D g2, String text) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(text);
        int th = fm.getHeight();
        
        int tx = (getWidth() - tw) / 2;
        int ty = getHeight() - 20;
        
        g2.setColor(new Color(15, 23, 42, 220));
        g2.fillRoundRect(tx - 10, ty - th - 5, tw + 20, th + 10, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawString(text, tx, ty - 5);
    }

    private void drawCenteredString(Graphics2D g2, String text, int x, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x - fm.stringWidth(text) / 2, y);
    }
}
