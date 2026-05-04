package com.pbl_3project.gui;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.File;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import com.pbl_3project.dto.CartItem;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.awt.Desktop;

public class InvoicePrinter extends JDialog {
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private String maHD;
    private List<CartItem> danhSachSP;
    private double tongTien;
    private double subtotal;
    private double discountAmount;
    private String staffName;
    private String paymentMethod;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private boolean isPrintSuccess = false;
    private java.sql.Timestamp orderAt;
    private java.sql.Timestamp paymentAt;
    private java.sql.Timestamp deliveredAt;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public boolean checkPrintSuccess() {
        return isPrintSuccess;
    }

    public void setTimestamps(java.sql.Timestamp orderAt, java.sql.Timestamp paymentAt,
            java.sql.Timestamp deliveredAt) {
        this.orderAt = orderAt;
        this.paymentAt = paymentAt;
        this.deliveredAt = deliveredAt;

        getContentPane().removeAll();
        initComponents();
        revalidate();
        repaint();
    }

    public InvoicePrinter(Frame parent, String maHD, List<CartItem> ds, double subtotal, double discountAmount, double tong, String staffName,
            String paymentMethod) {
        super(parent, "Xuất hóa đơn", true);
        this.maHD = maHD;
        this.danhSachSP = ds;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.tongTien = tong;
        this.staffName = staffName;
        this.paymentMethod = paymentMethod;
        this.customerName = "Khách vãng lai";
        this.customerPhone = "";
        this.customerAddress = "";

        setSize(500, 800);
        setLocationRelativeTo(parent);
        initComponents();
    }

    public void setCustomerInfo(String name, String phone, String address) {
        this.customerName = name;
        this.customerPhone = phone;
        this.customerAddress = address;

        getContentPane().removeAll();
        initComponents();
        revalidate();
        repaint();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setBackground(Color.WHITE);

        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; padding: 20px; color: #333;'>");
        html.append("<div style='text-align: center; margin-bottom: 20px;'>");
        html.append("<h2 style='color: #2c3e50; margin: 0;'>CỬA HÀNG GIÀY T&T SHOES</h2>");
        html.append(
                "<p style='margin: 5px 0; color: #7f8c8d;'>Địa chỉ: 54 Nguyễn Lương Bằng, phường Liên Chiểu, TP. Đà Nẵng</p>");
        html.append("<p style='margin: 5px 0; color: #7f8c8d;'>Điện thoại: 0359 287 193</p>");
        html.append("</div>");
        html.append("<hr style='border: 1px solid #ecf0f1; margin: 15px 0;'>");

        html.append("<div style='margin-bottom: 20px;'>");
        html.append("<b>Mã HĐ:</b> ").append(maHD).append("<br>");

        String oTime = (orderAt != null) ? orderAt.toLocalDateTime().format(dtf) : LocalDateTime.now().format(dtf);
        html.append("<b>Ngày đặt:</b> ").append(oTime).append("<br>");

        if (paymentAt != null) {
            html.append("<b>Thanh toán:</b> ").append(paymentAt.toLocalDateTime().format(dtf));
            if (paymentMethod != null)
                html.append(" (").append(paymentMethod).append(")");
            html.append("<br>");
        } else if (paymentMethod != null && !paymentMethod.isEmpty()) {
            html.append("<b>Hình thức TT:</b> ").append(paymentMethod).append("<br>");
        }

        if (deliveredAt != null) {
            html.append("<b>Ngày nhận:</b> ").append(deliveredAt.toLocalDateTime().format(dtf)).append("<br>");
        }

        if (staffName != null && !staffName.isEmpty()) {
            html.append("<b>Nhân viên:</b> ").append(staffName).append("<br>");
        }

        html.append("<hr style='border: 1px dashed #ecf0f1; margin: 10px 0;'>");
        html.append("<b>Khách hàng:</b> ").append(customerName != null ? customerName : "Khách vãng lai")
                .append("<br>");
        if (customerPhone != null && !customerPhone.isEmpty()) {
            html.append("<b>SĐT:</b> ").append(customerPhone).append("<br>");
        }
        if (customerAddress != null && !customerAddress.isEmpty()) {
            html.append("<b>Địa chỉ nhận hàng:</b> ").append(customerAddress).append("<br>");
        }
        html.append("</div>");

        html.append("<table width='100%' style='border-collapse: collapse; margin-bottom: 20px;'>");
        html.append("<tr style='background-color: #f8f9fa;'>");
        html.append("<th style='padding: 10px; border-bottom: 2px solid #dee2e6; text-align: left;'>Sản phẩm</th>");
        html.append("<th style='padding: 10px; border-bottom: 2px solid #dee2e6; text-align: center;'>SL</th>");
        html.append("<th style='padding: 10px; border-bottom: 2px solid #dee2e6; text-align: right;'>Đơn giá</th>");
        html.append("<th style='padding: 10px; border-bottom: 2px solid #dee2e6; text-align: right;'>Thành tiền</th>");
        html.append("</tr>");

        for (CartItem item : danhSachSP) {
            html.append("<tr>");
            html.append("<td style='padding: 10px; border-bottom: 1px solid #ecf0f1;'>").append(item.getName())
                    .append("<br><small style='color: #95a5a6;'>Size: ")
                    .append(item.getSize() != null ? item.getSize() : "N/A").append(" | Màu: ")
                    .append(item.getColor() != null ? item.getColor() : "N/A").append("</small></td>");
            html.append("<td style='padding: 10px; border-bottom: 1px solid #ecf0f1; text-align: center;'>")
                    .append(item.getQuantity()).append("</td>");
            html.append("<td style='padding: 10px; border-bottom: 1px solid #ecf0f1; text-align: right;'>")
                    .append(df.format(item.getPrice())).append("</td>");
            html.append("<td style='padding: 10px; border-bottom: 1px solid #ecf0f1; text-align: right;'>")
                    .append(df.format(item.getPrice() * item.getQuantity())).append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<div style='text-align: right; margin-bottom: 30px;'>");
        if (discountAmount > 0) {
            html.append("<p style='margin: 5px 0; font-size: 14px;'>Tạm tính: ").append(df.format(subtotal)).append("</p>");
            html.append("<p style='margin: 5px 0; font-size: 14px; color: #27ae60;'>Giảm giá: -").append(df.format(discountAmount)).append("</p>");
        }
        html.append("<p style='margin: 10px 0; font-size: 20px; color: #e74c3c;'><b>TỔNG TIỀN: ").append(df.format(tongTien)).append("</b></p>");
        html.append("</div>");

        html.append("<hr style='border: 1px dashed #bdc3c7; margin: 15px 0;'>");
        html.append("<div style='text-align: center; color: #7f8c8d; font-style: italic;'>");
        html.append("Cảm ơn Quý khách đã mua sắm tại T&T Shoes.<br>Hẹn gặp lại!");
        html.append("</div>");
        html.append("</body></html>");

        editorPane.setText(html.toString());

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnPrint = new JButton("In Hóa Đơn (PDF)") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = new Color(41, 128, 185);
                Color c2 = new Color(31, 97, 141);
                if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, c1.brighter(), 0, getHeight(), c1));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setContentAreaFilled(false);
        btnPrint.setBorderPainted(false);
        btnPrint.setFocusPainted(false);
        btnPrint.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrint.setPreferredSize(new Dimension(180, 40));

        JButton btnClose = new JButton("Đóng") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = new Color(231, 76, 60);
                Color c2 = new Color(192, 57, 43);
                if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, c1.brighter(), 0, getHeight(), c1));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setForeground(Color.WHITE);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setPreferredSize(new Dimension(100, 40));

        btnPrint.addActionListener(e -> {
            try {
                File dir = new File("src/main/resources/Hóa đơn");
                if (!dir.exists())
                    dir.mkdirs();

                File outputFile = new File(dir, "HoaDon_" + maHD + ".pdf");

                if (outputFile.exists()) {
                    String[] options = { "Mở xem", "In lại (Ghi đè)", "Hủy" };
                    int choice = JOptionPane.showOptionDialog(this,
                            "Hóa đơn " + maHD + " đã tồn tại. Bạn muốn làm gì?",
                            "Thông báo",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null, options, options[0]);

                    if (choice == 0) {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(outputFile);
                            this.dispose();
                        }
                        return;
                    } else if (choice == 2 || choice == -1) {
                        return;
                    }
                }
                PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
                attributes.add(new JobName("HoaDon_" + maHD, null));

                attributes.add(new Destination(outputFile.toURI()));

                PrintService pdfService = null;
                PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
                for (PrintService service : services) {
                    if (service.getName().toLowerCase().contains("pdf")) {
                        pdfService = service;
                        break;
                    }
                }

                boolean completed = editorPane.print(null, null, true, pdfService, attributes, true);
                if (completed) {
                    isPrintSuccess = true;
                    if (Desktop.isDesktopSupported()) {
                        try {
                            if (outputFile.exists()) {
                                Desktop.getDesktop().open(outputFile);
                            }
                        } catch (Exception ex) {
                            System.err.println("Không thể mở file hóa đơn: " + ex.getMessage());
                        }
                    }
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Bạn đã hủy lệnh in hóa đơn.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi in ấn: " + ex.getMessage());
            }
        });

        btnClose.addActionListener(e -> {
            isPrintSuccess = true;
            this.dispose();
        });

        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }
}