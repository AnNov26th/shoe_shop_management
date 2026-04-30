package com.pbl_3project.gui;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.pbl_3project.bus.OrderBUS;

public class ReturnManagementPanel extends JPanel {
    private static final Color BG_CONTENT = new Color(248, 250, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color ACCENT = new Color(34, 197, 94);
    private static final Color BTN_BLUE = new Color(56, 189, 248);
    private static final Color BTN_DANGER = new Color(239, 68, 68);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT_H = new Color(15, 23, 42);
    private static final Color TEXT_S = new Color(100, 116, 139);
    private static final Color HEADER_BG = new Color(241, 245, 249);
    private static final Color WARNING = new Color(245, 158, 11);

    private JTable tableRequests;
    private DefaultTableModel modelRequests;
    private JTable tableDetails;
    private DefaultTableModel modelDetails;
    private JLabel lblDetailTitle;
    private final OrderBUS orderBUS = new OrderBUS();

    public ReturnManagementPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_CONTENT);
        initComponents();
        loadRequests();
    }

    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(20, 24, 20, 24)));
        
        JLabel lblTitle = new JLabel("XỬ LÝ YÊU CẦU ĐỔI / TRẢ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_H);
        
        JButton btnRefresh = createRoundButton("Làm mới", BTN_BLUE);
        btnRefresh.addActionListener(e -> loadRequests());
        
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] colsRequests = { "ID", "Mã Đơn", "Khách Hàng", "SĐT", "Loại", "Lý do", "Chi tiết", "Ngày Đặt" };
        modelRequests = new DefaultTableModel(colsRequests, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableRequests = buildStyledTable(modelRequests);
        tableRequests.getColumnModel().removeColumn(tableRequests.getColumnModel().getColumn(0));
        
        JScrollPane scrollRequests = buildScrollPane(tableRequests);
        JPanel panelRequests = new JPanel(new BorderLayout());
        panelRequests.setBackground(WHITE);
        panelRequests.setBorder(new EmptyBorder(15, 20, 0, 20));
        panelRequests.add(buildSectionTitle("Danh sách yêu cầu đang chờ"), BorderLayout.NORTH);
        
        JPanel requestsCard = new JPanel(new BorderLayout());
        requestsCard.setBorder(new LineBorder(BORDER, 1, true));
        requestsCard.add(scrollRequests, BorderLayout.CENTER);
        panelRequests.add(requestsCard, BorderLayout.CENTER);

        String[] colsDetail = { "Sản phẩm", "SKU", "Size", "Màu", "SL", "Đơn giá", "Thành tiền" };
        modelDetails = new DefaultTableModel(colsDetail, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableDetails = buildStyledTable(modelDetails);
        JScrollPane scrollDetails = buildScrollPane(tableDetails);

        JPanel detailHeader = new JPanel(new BorderLayout());
        detailHeader.setBackground(HEADER_BG);
        detailHeader.setBorder(new EmptyBorder(10, 16, 10, 16));
        
        JPanel headerLeft = new JPanel(new GridLayout(0, 1));
        headerLeft.setOpaque(false);
        lblDetailTitle = new JLabel("← Chọn một yêu cầu để xem chi tiết & xử lý");
        lblDetailTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDetailTitle.setForeground(TEXT_S);
        headerLeft.add(lblDetailTitle);
        
        lblReturnInfo = new JLabel("");
        lblReturnInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblReturnInfo.setForeground(new Color(100, 116, 139));
        headerLeft.add(lblReturnInfo);

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBar.setOpaque(false);
        btnExchangeSelect = createRoundButton("Chọn SP Đổi", BTN_BLUE);
        btnExchangeSelect.setVisible(false);
        JButton btnAccept = createRoundButton("Chấp nhận Đổi/Trả", ACCENT);
        JButton btnReject = createRoundButton("Từ chối", BTN_DANGER);
        
        actionBar.add(btnExchangeSelect);
        actionBar.add(btnReject);
        actionBar.add(btnAccept);
        detailHeader.add(headerLeft, BorderLayout.WEST);
        detailHeader.add(actionBar, BorderLayout.EAST);

        JPanel panelDetails = new JPanel(new BorderLayout());
        panelDetails.setBackground(WHITE);
        panelDetails.setBorder(new EmptyBorder(0, 20, 15, 20));
        panelDetails.add(buildSectionTitle("Chi tiết sản phẩm cần xử lý"), BorderLayout.NORTH);
        
        JPanel detailCard = new JPanel(new BorderLayout());
        detailCard.setBorder(new LineBorder(BORDER, 1, true));
        detailCard.add(detailHeader, BorderLayout.NORTH);
        detailCard.add(scrollDetails, BorderLayout.CENTER);
        panelDetails.add(detailCard, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelRequests, panelDetails);
        split.setDividerLocation(280);
        split.setDividerSize(8);
        split.setBorder(null);
        split.setOpaque(false);
        add(split, BorderLayout.CENTER);

        tableRequests.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableRequests.getSelectedRow();
                if (row >= 0) {
                    int modelRow = tableRequests.convertRowIndexToModel(row);
                    int orderId = (int) modelRequests.getValueAt(modelRow, 0);
                    String type = (String) modelRequests.getValueAt(modelRow, 4);
                    String reason = (String) modelRequests.getValueAt(modelRow, 5);
                    String details = (String) modelRequests.getValueAt(modelRow, 6);
                    
                    lblReturnInfo.setText(String.format("<html><b>Loại:</b> %s | <b>Lý do:</b> %s<br><b>Chi tiết:</b> %s</html>", type, reason, details));
                    btnExchangeSelect.setVisible(type.equals("Đổi hàng"));
                    loadOrderDetail(orderId);
                }
            }
        });

        btnExchangeSelect.addActionListener(e -> {
            int row = tableRequests.getSelectedRow();
            if (row < 0) return;
            int modelRow = tableRequests.convertRowIndexToModel(row);
            int orderId = (int) modelRequests.getValueAt(modelRow, 0);
            
            String info = JOptionPane.showInputDialog(this, "Nhập thông tin sản phẩm đổi (Tên, Size, Màu):", "Chọn sản phẩm đổi", JOptionPane.QUESTION_MESSAGE);
            if (info != null && !info.trim().isEmpty()) {
                try {
                    if (orderBUS.updateExchangeInfo(orderId, info)) {
                        JOptionPane.showMessageDialog(this, "Đã lưu thông tin sản phẩm đổi!");
                        lblReturnInfo.setText(lblReturnInfo.getText().replace("</html>", "") + " | <font color='blue'>SP Đổi: " + info + "</font></html>");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
                }
            }
        });

        btnAccept.addActionListener(e -> handleAction(true));
        btnReject.addActionListener(e -> handleAction(false));
    }

    private JLabel lblReturnInfo;
    private JButton btnExchangeSelect;

    private void loadRequests() {
        try {
            DefaultTableModel data = orderBUS.getReturnRequests();
            modelRequests.setRowCount(0);
            for (int i = 0; i < data.getRowCount(); i++) {
                modelRequests.addRow(new Object[] {
                    data.getValueAt(i, 0), data.getValueAt(i, 1), data.getValueAt(i, 2),
                    data.getValueAt(i, 3), data.getValueAt(i, 4), data.getValueAt(i, 5),
                    data.getValueAt(i, 6), data.getValueAt(i, 7)
                });
            }
            modelDetails.setRowCount(0);
            lblDetailTitle.setText("← Chọn một yêu cầu để xem chi tiết & xử lý");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadOrderDetail(int orderId) {
        try {
            DefaultTableModel data = orderBUS.getOrderDetails(orderId);
            modelDetails.setRowCount(0);
            for (int i = 0; i < data.getRowCount(); i++) {
                modelDetails.addRow(new Object[] {
                    data.getValueAt(i, 0), data.getValueAt(i, 1), data.getValueAt(i, 2),
                    data.getValueAt(i, 3), data.getValueAt(i, 4), data.getValueAt(i, 5), data.getValueAt(i, 6)
                });
            }
            lblDetailTitle.setText("Chi tiết yêu cầu cho Đơn #" + orderId);
            lblDetailTitle.setForeground(TEXT_H);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleAction(boolean accept) {
        int row = tableRequests.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu!");
            return;
        }
        int modelRow = tableRequests.convertRowIndexToModel(row);
        int orderId = (int) modelRequests.getValueAt(modelRow, 0);
        
        String msg = accept ? "Xác nhận CHẤP NHẬN đổi trả cho đơn này?\nSản phẩm sẽ được hoàn lại kho." 
                           : "Xác nhận TỪ CHỐI yêu cầu này?\nĐơn hàng sẽ quay lại trạng thái Hoàn thành.";
        
        if (JOptionPane.showConfirmDialog(this, msg, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if (orderBUS.handleReturnRequest(orderId, accept)) {
                    JOptionPane.showMessageDialog(this, "Đã xử lý yêu cầu thành công!");
                    loadRequests();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(220, 252, 231));
        table.setSelectionForeground(TEXT_H);
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(HEADER_BG);
        th.setPreferredSize(new Dimension(0, 40));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        
        return table;
    }

    private Color getStatusColor(String status) {
        if (status == null) return TEXT_S;
        if (status.contains("Đổi hàng")) return BTN_BLUE;
        if (status.contains("Trả hàng")) return WARNING;
        return TEXT_S;
    }

    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            if (!isSelected) {
                Color bg = getStatusColor(value != null ? value.toString() : "");
                lbl.setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 30));
                lbl.setForeground(bg.darker());
            }
            lbl.setOpaque(true);
            return lbl;
        }
    }

    private JScrollPane buildScrollPane(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(WHITE);
        return scroll;
    }

    private JPanel buildSectionTitle(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 0, 5, 0));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_S);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    private JButton createRoundButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.darker() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        return btn;
    }
}
