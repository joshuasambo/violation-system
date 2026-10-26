package com.school.violationsystem.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class DashboardPanel extends JPanel {

    // Color Palette Constants matching your theme
    private static final Color COLOR_PRIMARY = Color.decode("#282385"); // Royal Blue
    private static final Color COLOR_ACCENT = Color.decode("#F5A623");  // Flame Gold
    private static final Color COLOR_SURFACE = Color.decode("#F8F9FC"); // Off-White / Slate
    private static final Color COLOR_TEXT = Color.decode("#141238");    // Deep Navy
    private static final Color COLOR_MUTED = Color.decode("#5A587A");   // Muted Text
    private static final Color COLOR_WHITE = Color.white;

    public DashboardPanel() {
        setBackground(COLOR_SURFACE);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Section Title Header ---
        JLabel sectionTitle = new JLabel("Dashboard Overview");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(COLOR_TEXT);
        add(sectionTitle, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 16, 0);
        JLabel sectionSubtitle = new JLabel("Summary of recent activity and system metrics.");
        sectionSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sectionSubtitle.setForeground(COLOR_MUTED);
        add(sectionSubtitle, gbc);

        // --- Metric Cards Grid (2 rows, 2 or 3 columns) ---
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        cardsGrid.setBackground(COLOR_SURFACE);

        // Add Metric Cards
        cardsGrid.add(createMetricCard("Total Violations", "148", "Active records logged this semester", COLOR_PRIMARY));
        cardsGrid.add(createMetricCard("Pending Reviews", "12", "Requires immediate attention", COLOR_ACCENT));
        cardsGrid.add(createMetricCard("Enrolled Students", "1,240", "Total registered database users", COLOR_PRIMARY));
        cardsGrid.add(createMetricCard("Resolved Cases", "136", "Successfully closed violations", Color.decode("#20703B"))); // Forest Green

        add(cardsGrid, gbc);
    }

    /**
     * Helper method to build a styled metric summary card.
     */
    private JPanel createMetricCard(String title, String value, String description, Color accentColor) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(COLOR_WHITE);
        
        // Clean card border with a subtle left accent strip or frame
        Border outerBorder = BorderFactory.createLineBorder(Color.decode("#E2E5EE"), 1);
        Border innerBorder = BorderFactory.createEmptyBorder(16, 20, 16, 20);
        card.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Card Title
        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(COLOR_MUTED);
        card.add(titleLabel, gbc);

        // Card Main Value / Number
        gbc.gridy = 1;
        gbc.insets = new Insets(6, 0, 4, 0);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);
        card.add(valueLabel, gbc);

        // Card Description
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(COLOR_MUTED);
        card.add(descLabel, gbc);

        return card;
    }
}