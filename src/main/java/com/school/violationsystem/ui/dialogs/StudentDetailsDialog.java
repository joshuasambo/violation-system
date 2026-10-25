package com.school.violationsystem.ui.dialogs;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class StudentDetailsDialog extends JDialog {

    public StudentDetailsDialog() {
        setTitle("Student Details");
        setSize(500, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Student information will appear here.", javax.swing.SwingConstants.CENTER), BorderLayout.CENTER);
        add(panel);
    }
}
