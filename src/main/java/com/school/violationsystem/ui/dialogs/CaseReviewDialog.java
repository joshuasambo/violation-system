package com.school.violationsystem.ui.dialogs;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class CaseReviewDialog extends JDialog {

    public CaseReviewDialog() {
        setTitle("Case Review");
        setSize(650, 500);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Case review details will be added here.", javax.swing.SwingConstants.CENTER), BorderLayout.CENTER);
        add(panel);
    }
}
