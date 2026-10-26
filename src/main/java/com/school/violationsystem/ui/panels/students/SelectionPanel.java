package com.school.violationsystem.ui.panels.students;

import com.school.violationsystem.ui.components.RoundedScrollBarUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

final class SelectionPanel<T> extends JPanel {
    private static final Color COLOR_PRIMARY = Color.decode("#282385");
    private static final Color COLOR_SURFACE = Color.decode("#F8F9FC");
    private static final Color COLOR_TEXT = Color.decode("#141238");
    private static final Color COLOR_MUTED = Color.decode("#5A587A");
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_BORDER = Color.decode("#E2E5EE");
    private static final Color COLOR_HOVER_BG = Color.decode("#F0F2FA");

    private final JLabel titleLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JPanel optionsPanel = new JPanel();
    private final Runnable onBack;
    private int optionRows;
    private int optionColumns;

    SelectionPanel(String title, Runnable onBack) {
        super(new BorderLayout(16, 24));
        this.onBack = onBack;
        setBackground(COLOR_SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        // Header Section with absolute center title alignment
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(COLOR_SURFACE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;

        if (onBack != null) {
            RoundedButton backButton = new RoundedButton("← Back", COLOR_WHITE, COLOR_HOVER_BG, COLOR_PRIMARY, COLOR_BORDER, 12);
            backButton.setPreferredSize(new Dimension(100, 40));
            backButton.addActionListener(event -> onBack.run());
            header.add(backButton, gbc);
        } else {
            JPanel placeholder = new JPanel();
            placeholder.setPreferredSize(new Dimension(100, 40));
            placeholder.setOpaque(false);
            header.add(placeholder, gbc);
        }

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 16, 0, 16);
        header.add(titleLabel, gbc);

        add(header, BorderLayout.NORTH);
        setTitle(title);

        // Center the grid vertically and horizontally inside the viewport container
        optionsPanel.setBackground(COLOR_SURFACE);
        JPanel centeringWrapper = new JPanel(new GridBagLayout());
        centeringWrapper.setBackground(COLOR_SURFACE);
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0;
        centerGbc.gridy = 0;
        centerGbc.anchor = GridBagConstraints.CENTER;
        centeringWrapper.add(optionsPanel, centerGbc);

        JScrollPane scrollPane = new JScrollPane(centeringWrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_SURFACE);
        scrollPane.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                updateOptionCardSizes(scrollPane);
            }
        });
        RoundedScrollBarUI.install(scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        // Footer Status/Feedback Label
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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

        if (options.size() == 4) {
            optionRows = 2;
            optionColumns = 2;
            optionsPanel.setLayout(new GridLayout(optionRows, optionColumns, 28, 28));
        } else if (options.size() == 3) {
            optionRows = 1;
            optionColumns = 3;
            optionsPanel.setLayout(new GridLayout(optionRows, optionColumns, 28, 28));
        } else {
            optionColumns = 3;
            optionRows = (options.size() + optionColumns - 1) / optionColumns;
            optionsPanel.setLayout(new GridLayout(0, optionColumns, 24, 24));
        }

        for (T option : options) {
            String rawLabel = labelProvider.apply(option);
            RoundedCardButton optionButton = new RoundedCardButton(
                formatOptionText(rawLabel, 16),
                COLOR_WHITE,
                COLOR_PRIMARY,
                COLOR_PRIMARY,
                COLOR_WHITE,
                COLOR_BORDER,
                16
            );
            optionButton.putClientProperty("selection.rawLabel", rawLabel);
            optionButton.addActionListener(event -> onSelect.accept(option));
            optionsPanel.add(optionButton);
        }

        statusLabel.setText(options.isEmpty() ? emptyMessage : " ");
        SwingUtilities.invokeLater(() -> updateOptionCardSizes((JScrollPane) SwingUtilities.getAncestorOfClass(
                JScrollPane.class, optionsPanel)));
        revalidate();
        repaint();
    }

    private void updateOptionCardSizes(JScrollPane scrollPane) {
        if (scrollPane == null || optionRows == 0 || optionColumns == 0) {
            return;
        }

        Dimension viewportSize = scrollPane.getViewport().getExtentSize();
        if (viewportSize.width <= 0 || viewportSize.height <= 0) {
            return;
        }

        int horizontalGap = optionColumns > 1 ? 28 : 0;
        int verticalGap = optionRows > 1 ? 28 : 0;
        int cardWidth = clamp((viewportSize.width - 48 - horizontalGap * (optionColumns - 1))
                / optionColumns, 140, 240);
        int cardHeight = clamp((viewportSize.height - 48 - verticalGap * (optionRows - 1))
                / optionRows, 104, 172);
        int fontSize = clamp(Math.min(cardWidth / 10, cardHeight / 6), 11, 14);

        for (java.awt.Component component : optionsPanel.getComponents()) {
            if (component instanceof RoundedCardButton button) {
                String rawLabel = (String) button.getClientProperty("selection.rawLabel");
                button.setPreferredSize(new Dimension(cardWidth, cardHeight));
                button.setText(formatOptionText(rawLabel, fontSize));
            }
        }

        optionsPanel.revalidate();
    }

    private String formatOptionText(String rawLabel, int fontSize) {
        if (rawLabel.contains("(") && rawLabel.contains(")")) {
            int lastOpen = rawLabel.lastIndexOf('(');
            String namePart = rawLabel.substring(0, lastOpen).trim();
            String yearPart = rawLabel.substring(lastOpen + 1, rawLabel.length() - 1).trim();
            int detailFontSize = Math.max(10, fontSize - 2);
            return "<html><div style='text-align: center;'>"
                    + "<span style='font-size: " + fontSize + "px; font-weight: bold;'>"
                    + namePart + "</span><br/><br/>"
                    + "<span style='font-size: " + detailFontSize
                    + "px; font-weight: normal; opacity: 0.8;'>(" + yearPart + ")</span>"
                    + "</div></html>";
        }
        return "<html><div style='text-align: center; font-size: " + fontSize
                + "px; font-weight: bold;'>" + rawLabel + "</div></html>";
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static class RoundedButton extends JButton {
        private Color bg;
        private final Color hoverBg;
        private final Color borderCol;
        private final int radius;

        public RoundedButton(String text, Color bg, Color hoverBg, Color textFg, Color borderCol, int radius) {
            super(text);
            this.bg = bg;
            this.hoverBg = hoverBg;
            this.borderCol = borderCol;
            this.radius = radius;

            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(textFg);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    RoundedButton.this.bg = hoverBg;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    RoundedButton.this.bg = bg;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            if (borderCol != null) {
                g2.setColor(borderCol);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedCardButton extends JButton {
        private Color currentBg;
        private Color currentFg;
        private final Color normalBg;
        private final Color normalFg;
        private final Color hoverBg;
        private final Color hoverFg;
        private final Color borderCol;
        private final int radius;

        public RoundedCardButton(String text, Color normalBg, Color normalFg, Color hoverBg, Color hoverFg, Color borderCol, int radius) {
            super(text);
            this.normalBg = normalBg;
            this.normalFg = normalFg;
            this.hoverBg = hoverBg;
            this.hoverFg = hoverFg;
            this.borderCol = borderCol;
            this.radius = radius;
            this.currentBg = normalBg;
            this.currentFg = normalFg;

            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    currentBg = hoverBg;
                    currentFg = hoverFg;
                    setForeground(currentFg);
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    currentBg = normalBg;
                    currentFg = normalFg;
                    setForeground(currentFg);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(currentBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(currentBg == hoverBg ? hoverBg : borderCol);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}