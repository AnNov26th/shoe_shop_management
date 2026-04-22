package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.pbl_3project.util.TimeDisplayPanel;

public class AdminForm extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JButton selectedMenuButton;

    // Các mã màu hiện đại
    private final Color MENU_INACTIVE = Color.WHITE;
    private final Color MENU_ACTIVE = new Color(34, 177, 76); // Xanh lá nổi bật
    private final Color MENU_HOVER = new Color(240, 245, 249); // Xám xanh rất nhạt khi di chuột
    private final Color TEXT_INACTIVE = new Color(50, 50, 50); // Đen mềm mại
    private final Color TEXT_ACTIVE = Color.WHITE;
    private final Color APP_BACKGROUND = new Color(240, 244, 247); // Nền tổng thể

    public AdminForm() {
        setTitle("Hệ thống Quản lý Cửa hàng Giày dép - [ADMIN]");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(APP_BACKGROUND); // Set màu nền cho toàn app

        initComponents();
    }

    private void initComponents() {
        TimeDisplayPanel timePanel = new TimeDisplayPanel();
        add(timePanel, BorderLayout.NORTH);

        // ==========================================
        // 1. SIDEBAR (THANH MENU BÊN TRÁI)
        // ==========================================
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(260, getHeight()));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setLayout(new BorderLayout());
        // Tạo đường viền mảnh ngăn cách sidebar và main content
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));

        // Tiêu đề Sidebar
        JLabel lblAdmin = new JLabel("Shoe Shop T&T", SwingConstants.CENTER);
        lblAdmin.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblAdmin.setForeground(new Color(34, 177, 76)); // Chữ xanh lá cho đồng bộ
        lblAdmin.setBorder(new EmptyBorder(25, 10, 25, 10));
        lblAdmin.setBackground(Color.WHITE);
        lblAdmin.setOpaque(true);
        sidebarPanel.add(lblAdmin, BorderLayout.NORTH);

        // Khung chứa các nút Menu
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        // Thêm padding hai bên để nút active có khoảng trống bo tròn mượt mà
        menuPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Tạo các nút menu
        JButton btnThongKe = createMenuButton("Báo cáo & Thống kê");
        JButton btnSanPham = createMenuButton("Quản lý Sản phẩm");
        JButton btnKhoHang = createMenuButton("Quản lý Kho hàng");
        JButton btnDonHang = createMenuButton("Quản lý Đơn hàng");
        JButton btnKhuyenMai = createMenuButton("Quản lý Khuyến mãi");
        JButton btnNhanVien = createMenuButton("Quản lý Nhân sự");
        JButton btnKhachHang = createMenuButton("Quản lý Khách hàng");
        JButton btnLogout = createMenuButton("Đăng xuất");
        btnLogout.setForeground(Color.RED);

        // Đẩy các nút vào và cách nhau 1 khoảng nhỏ (5px) cho thoáng
        menuPanel.add(btnThongKe);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnSanPham);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnKhoHang);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnDonHang);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnKhuyenMai);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnNhanVien);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnKhachHang);

        menuPanel.add(Box.createVerticalGlue()); // Ép nút Đăng xuất xuống đáy
        menuPanel.add(btnLogout);

        sidebarPanel.add(menuPanel, BorderLayout.CENTER);

        // ==========================================
        // 2. MAIN CONTENT (KHUNG NỘI DUNG BÊN PHẢI ĐƯỢC BO TRÒN)
        // ==========================================
        cardLayout = new CardLayout();

        // Khởi tạo mainContentPanel với khả năng tự vẽ nền bo góc
        mainContentPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Bật khử răng cưa để góc bo tròn mịn màng, không bị rỗ
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                // Vẽ nền màu trắng bo góc 25px
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        mainContentPanel.setOpaque(false); // Trong suốt để hiển thị nền bo góc tự vẽ
        // Thêm khoảng cách thụt lùi bên trong để các panel con không đè lên góc bo tròn
        mainContentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Add các panel chức năng (Giữ nguyên của bạn)
        mainContentPanel.add(new ProductManagementPanel(), "SanPham");
        mainContentPanel.add(new DashboardPanel(), "ThongKe");
        mainContentPanel.add(new EmployeeManagementPanel(), "NhanVien");
        mainContentPanel.add(new CustomerManagementPanel(), "KhachHang");

        // Bọc mainContentPanel vào một vùng đệm (padding) để cách viền JFrame
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(APP_BACKGROUND);
        contentWrapper.setBorder(new EmptyBorder(20, 20, 20, 20)); // Margin xung quanh khu vực nội dung
        contentWrapper.add(mainContentPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. RÁP VÀO FRAME CHÍNH & GẮN SỰ KIỆN
        // ==========================================
        add(sidebarPanel, BorderLayout.WEST);
        add(contentWrapper, BorderLayout.CENTER); // Add Wrapper thay vì mainContentPanel trực tiếp

        btnThongKe.addActionListener(e -> {
            selectMenuButton(btnThongKe);
            cardLayout.show(mainContentPanel, "ThongKe");
        });
        btnSanPham.addActionListener(e -> {
            selectMenuButton(btnSanPham);
            cardLayout.show(mainContentPanel, "SanPham");
        });
        btnKhoHang.addActionListener(e -> {
            selectMenuButton(btnKhoHang);
            cardLayout.show(mainContentPanel, "KhoHang");
        });
        btnDonHang.addActionListener(e -> {
            selectMenuButton(btnDonHang);
            cardLayout.show(mainContentPanel, "DonHang");
        });
        btnKhuyenMai.addActionListener(e -> {
            selectMenuButton(btnKhuyenMai);
            cardLayout.show(mainContentPanel, "KhuyenMai");
        });
        btnNhanVien.addActionListener(e -> {
            selectMenuButton(btnNhanVien);
            cardLayout.show(mainContentPanel, "NhanVien");
        });
        btnKhachHang.addActionListener(e -> {
            selectMenuButton(btnKhachHang);
            cardLayout.show(mainContentPanel, "KhachHang");
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                // Nếu class LoginForm của bạn đã sẵn sàng thì mở cmt dòng dưới:
                // new LoginForm().setVisible(true);
            }
        });

        // Kích hoạt nút đầu tiên mặc định
        selectMenuButton(btnThongKe);
        cardLayout.show(mainContentPanel, "ThongKe");
    }

    /**
     * Xử lý chuyển đổi màu sắc khi một menu được chọn
     */
    private void selectMenuButton(JButton selectedButton) {
        JButton oldSelected = selectedMenuButton;

        if (oldSelected != null && !oldSelected.equals(selectedButton)) {
            oldSelected.setBackground(MENU_INACTIVE);
            oldSelected.setForeground(TEXT_INACTIVE);
        }

        selectedButton.setBackground(MENU_ACTIVE);
        selectedButton.setForeground(TEXT_ACTIVE);
        selectedMenuButton = selectedButton;

        // Cập nhật lại giao diện (để vẽ lại phần nền bo góc của nút)
        if (oldSelected != null)
            oldSelected.repaint();
        selectedButton.repaint();
    }

    /**
     * Tạo Custom Button bo góc tròn mềm mại cho Menu
     */
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Trạng thái được chọn (Xanh lá) hoặc Hover (Xám nhạt) hoặc Mặc định (Trắng)
                g2.setColor(getBackground());
                // Vẽ khung bo tròn 18px
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                g2.dispose();
                super.paintComponent(g); // Vẽ text lên trên nền
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Font chữ hiện đại
        btn.setForeground(TEXT_INACTIVE);
        btn.setBackground(MENU_INACTIVE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // Tắt hết các thuộc tính vẽ mặc định của Java Swing để Custom vẽ
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Hiệu ứng Hover mượt mà (Đổi sang xám xanh rất nhạt nếu chưa được chọn)
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!btn.equals(selectedMenuButton)) {
                    btn.setBackground(MENU_HOVER);
                    btn.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!btn.equals(selectedMenuButton)) {
                    btn.setBackground(MENU_INACTIVE);
                    btn.repaint();
                }
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        try {
            // Sử dụng LookAndFeel hệ thống để font chữ đẹp hơn
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new AdminForm().setVisible(true));
    }
}