package com.school.violationsystem.ui.panels;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class SectionsPanel extends JPanel {

    public SectionsPanel() {
        setLayout(new java.awt.BorderLayout());

        JTable table = new JTable(new Object[][]{}, new String[] {"Grade Level", "Section", "Academic Year"});
        add(new JScrollPane(table), java.awt.BorderLayout.CENTER);
    }
}
