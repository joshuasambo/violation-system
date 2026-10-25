package com.school.violationsystem.ui.panels;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class StudentsPanel extends JPanel {

    public StudentsPanel() {
        setLayout(new java.awt.BorderLayout());

        JTable table = new JTable(new Object[][]{}, new String[] {"Student ID", "Name", "Section", "Status"});
        add(new JScrollPane(table), java.awt.BorderLayout.CENTER);
    }
}
