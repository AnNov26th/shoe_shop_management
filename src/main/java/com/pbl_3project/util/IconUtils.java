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

    /**
     * Tải icon với kích thước cụ thể
     * 
     * @param iconType Loại icon cần tải
     * @param width    Chiều rộng
     * @param height   Chiều cao
     * @return ImageIcon đã resize
     */
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

    /**
     * Tải icon với kích thước mặc định cho button menu (24x24)
     */
    public static ImageIcon loadMenuIcon(IconType iconType) {
        return loadIcon(iconType, 24, 24);
    }

    /**
     * Tải icon nhỏ (16x16)
     */
    public static ImageIcon loadSmallIcon(IconType iconType) {
        return loadIcon(iconType, 16, 16);
    }

    /**
     * Tải icon lớn (32x32)
     */
    public static ImageIcon loadLargeIcon(IconType iconType) {
        return loadIcon(iconType, 32, 32);
    }
}
