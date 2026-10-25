package com.school.violationsystem.ui.panels;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class UserManagementPanel extends JPanel {

    public UserManagementPanel() {
        setLayout(new java.awt.BorderLayout());

        JTable table = new JTable(new Object[][]{}, new String[] {"User ID", "Name", "Role", "Status"});
        add(new JScrollPane(table), java.awt.BorderLayout.CENTER);
    }
}
