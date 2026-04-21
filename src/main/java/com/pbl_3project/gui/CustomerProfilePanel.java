package com.pbl_3project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class CustomerProfilePanel extends JPanel {

    private JTextField txtPhone, txtDob;
    private JComboBox<String> cbxShoeSize;

    public CustomerProfilePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(30, 50, 30, 50));
        initComponents();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(30, 30, 30, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("HỒ SƠ CỦA TÔI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Cambria", Font.BOLD, 24));
        lblTitle.setForeground(new Color(59, 190, 210));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;

        gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        txtPhone = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtPhone, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Ngày sinh (YYYY-MM-DD):"), gbc);
        txtDob = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtDob, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Size giày của bạn:"), gbc);
        String[] sizes = { "36", "37", "38", "39", "40", "41", "42", "43", "44", "45" };
        cbxShoeSize = new JComboBox<>(sizes);
        gbc.gridx = 1;
        formPanel.add(cbxShoeSize, gbc);

        JButton btnSave = new JButton("LƯU THÔNG TIN");
        btnSave.setBackground(new Color(255, 127, 102));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFont(new Font("Cambria", Font.BOLD, 14));
        btnSave.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Cập nhật hồ sơ thành công!");
        });

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(btnSave, gbc);

        add(formPanel, BorderLayout.CENTER);
    }
}