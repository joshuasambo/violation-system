package com.school.violationsystem.ui.panels;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ViolationReportsPanel extends JPanel {

    public ViolationReportsPanel() {
        setLayout(new java.awt.BorderLayout());

        JTable table = new JTable(new Object[][]{}, new String[] {"Report ID", "Student", "Violation", "Status"});
        add(new JScrollPane(table), java.awt.BorderLayout.CENTER);
    }
}
