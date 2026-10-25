package com.school.violationsystem.ui.dialogs;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class ViolationReportDialog extends JDialog {

    public ViolationReportDialog() {
        setTitle("Violation Report");
        setSize(600, 500);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Report form will be added here.", javax.swing.SwingConstants.CENTER), BorderLayout.CENTER);
        add(panel);
    }
}
