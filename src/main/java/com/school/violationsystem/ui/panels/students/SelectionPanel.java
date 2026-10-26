package com.school.violationsystem.ui.panels.students;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

final class SelectionPanel<T> extends JPanel {
    private static final Color COLOR_PRIMARY = Color.decode("#282385");
    private static final Color COLOR_SURFACE = Color.decode("#F8F9FC");
    private static final Color COLOR_TEXT = Color.decode("#141238");
    private static final Color COLOR_MUTED = Color.decode("#5A587A");
    private static final Color COLOR_WHITE = Color.WHITE;

    private final JLabel titleLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JPanel optionsPanel = new JPanel(new GridLayout(0, 3, 16, 16));
    private final Runnable onBack;

    SelectionPanel(String title, Runnable onBack) {
        super(new BorderLayout(16, 16));
        this.onBack = onBack;
        setBackground(COLOR_SURFACE);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(COLOR_TEXT);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_SURFACE);
        if (onBack != null) {
            JButton backButton = new JButton("Back");
            backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            backButton.setFocusPainted(false);
            backButton.addActionListener(event -> onBack.run());
            header.add(backButton, BorderLayout.WEST);
        }
        header.add(titleLabel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        setTitle(title);

        optionsPanel.setBackground(COLOR_SURFACE);
        JScrollPane scrollPane = new JScrollPane(optionsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_SURFACE);
        add(scrollPane, BorderLayout.CENTER);

        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(COLOR_MUTED);
        add(statusLabel, BorderLayout.SOUTH);
    }

    void setTitle(String title) {
        titleLabel.setText(title);
    }

    void showLoading(String message) {
        optionsPanel.removeAll();
        statusLabel.setText(message);
        revalidate();
        repaint();
    }

    void showError(String message) {
        optionsPanel.removeAll();
        statusLabel.setText(message);
        revalidate();
        repaint();
    }

    void showOptions(
            List<T> options,
            Function<T, String> labelProvider,
            Consumer<T> onSelect,
            String emptyMessage) {
        optionsPanel.removeAll();
        for (T option : options) {
            JButton optionButton = new JButton(labelProvider.apply(option));
            optionButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
            optionButton.setForeground(COLOR_PRIMARY);
            optionButton.setBackground(COLOR_WHITE);
            optionButton.setFocusPainted(false);
            optionButton.setOpaque(true);
            optionButton.setPreferredSize(new Dimension(180, 76));
            optionButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.decode("#E2E5EE")),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            optionButton.addActionListener(event -> onSelect.accept(option));
            optionsPanel.add(optionButton);
        }

        statusLabel.setText(options.isEmpty() ? emptyMessage : " ");
        revalidate();
        repaint();
    }

}
