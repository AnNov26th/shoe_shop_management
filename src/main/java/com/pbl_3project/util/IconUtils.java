package com.pbl_3project.util;

import java.awt.Image;
import javax.swing.ImageIcon;

public class IconUtils {
    private static final String ICON_PATH = "/icons/";

    public enum IconType {
        DASHBOARD("icon_dashboard.png.png"),
        SHOE("icon_shoe.png.png"),
        TAG("icon_tag.png.png"),
        TROLLEY("icon_trolley.png.png"),
        LOGOUT("icon_turnoff.png.png"),
        USER("icon_user.png.png"),
        WAREHOUSE("icon_warehouse.png.png");

        private final String filename;

        IconType(String filename) {
            this.filename = filename;
        }

        public String getFilename() {
            return filename;
        }
    }

    public static ImageIcon loadIcon(IconType iconType, int width, int height) {
        try {
            java.net.URL imgURL = IconUtils.class.getResource(ICON_PATH + iconType.getFilename());
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Không tìm thấy icon: " + iconType.getFilename());
        }
        return null;
    }

    public static ImageIcon loadMenuIcon(IconType iconType) {
        return loadIcon(iconType, 24, 24);
    }

    public static ImageIcon loadSmallIcon(IconType iconType) {
        return loadIcon(iconType, 16, 16);
    }

    public static ImageIcon loadLargeIcon(IconType iconType) {
        return loadIcon(iconType, 32, 32);
    }

    public static ImageIcon findProductImage(String productName, int width, int height) {
        try {
            // Try standard relative path first
            java.io.File dir = new java.io.File("src/main/resources/images");
            if (!dir.exists()) {
                // Fallback to absolute path from workspace if needed
                dir = new java.io.File("F:\\CNTT\\shoe_shop_management\\src\\main\\resources\\images");
            }
            
            if (dir.exists()) {
                java.io.File[] files = dir.listFiles((d, name) -> {
                    String lowerName = name.toLowerCase();
                    boolean isImage = lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg");
                    if (!isImage) return false;
                    
                    // Exact match (no color) or match with color separator " - "
                    return name.equals(productName + ".png") || 
                           name.equals(productName + ".jpg") ||
                           name.startsWith(productName + " - ");
                });
                if (files != null && files.length > 0) {
                    ImageIcon icon = new ImageIcon(files[0].getAbsolutePath());
                    if (icon.getIconWidth() > 0) {
                        java.awt.Image scaled = icon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
                        return new ImageIcon(scaled);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding image for: " + productName);
        }
        return null;
    }
}
