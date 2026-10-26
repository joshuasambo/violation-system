package com.school.violationsystem.ui.dialogs;

import com.school.violationsystem.model.StudentViolationReport;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ViolationDetailsDialog extends JDialog {
    private static final Color COLOR_PRIMARY = Color.decode("#282385");
    private static final Color COLOR_SURFACE = Color.decode("#F8F9FC");
    private static final Color COLOR_TEXT = Color.decode("#141238");
    private static final Color COLOR_MUTED = Color.decode("#5A587A");
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_BORDER = Color.decode("#E2E5EE");
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    public ViolationDetailsDialog(Frame parent, StudentViolationReport report) {
        super(parent, "Violation Report Details - Case #" + report.caseId(), true);
        // Increased window dimensions
        setSize(580, 560);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(COLOR_SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Header
        JLabel titleLabel = new JLabel("Violation Report Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_TEXT);
        root.add(titleLabel, BorderLayout.NORTH);

        // Content Panel (Form style)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(COLOR_WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addDetailRow(contentPanel, gbc, row++, "Case ID:", "#" + report.caseId());
        addDetailRow(contentPanel, gbc, row++, "Incident Date:", report.incidentDate() != null ? DATE_FORMAT.format(report.incidentDate()) : "—");
        addDetailRow(contentPanel, gbc, row++, "Violation:", valueOrFallback(report.violationName(), "—"));
        
        // Description Section configured with 5 rows and a larger preferred height
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel descTitle = new JLabel("Description:");
        descTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        descTitle.setForeground(COLOR_MUTED);
        contentPanel.add(descTitle, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        
        // Initialized JTextArea with 5 rows to fit ~5 vertical lines of text
        JTextArea descArea = new JTextArea(valueOrFallback(report.incidentDescription(), "—"), 5, 20);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setForeground(COLOR_TEXT);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.setPreferredSize(new Dimension(350, 130)); // Accommodates ~5 lines comfortably
        descScrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));
        descScrollPane.setBackground(COLOR_WHITE);
        descScrollPane.getViewport().setBackground(COLOR_WHITE);
        contentPanel.add(descScrollPane, gbc);
        row++;

        // Reset constraints for remaining rows
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addDetailRow(contentPanel, gbc, row++, "Submitted At:", report.submittedAt() != null ? DATE_TIME_FORMAT.format(report.submittedAt()) : "—");
        addDetailRow(contentPanel, gbc, row++, "Report Status:", valueOrFallback(report.reportStatus(), "").replace('_', ' '));
        addDetailRow(contentPanel, gbc, row++, "Review Outcome:", valueOrFallback(report.reviewOutcome(), "").replace('_', ' '));

        root.add(contentPanel, BorderLayout.CENTER);

        // Footer / Close Button
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeButton.setBackground(COLOR_PRIMARY);
        closeButton.setForeground(COLOR_WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());

        JPanel buttonWrapper = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(closeButton);
        footer.add(buttonWrapper, BorderLayout.EAST);

        root.add(footer, BorderLayout.SOUTH);
        add(root, BorderLayout.CENTER);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, String valueText) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COLOR_MUTED);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        JLabel val = new JLabel(valueText);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(COLOR_TEXT);
        panel.add(val, gbc);
    }

    private static String valueOrFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}