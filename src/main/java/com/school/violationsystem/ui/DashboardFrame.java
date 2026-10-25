package com.school.violationsystem.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("Violation System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout());
        content.add(new JLabel("Dashboard", javax.swing.SwingConstants.CENTER), BorderLayout.CENTER);
        add(content);
    }
}
