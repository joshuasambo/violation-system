package com.school.violationsystem.ui.panels.students;

import com.school.violationsystem.model.Student;
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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

final class StudentListPanel extends JPanel {
    private static final Color COLOR_PRIMARY = Color.decode("#282385");
    private static final Color COLOR_SURFACE = Color.decode("#F8F9FC");
    private static final Color COLOR_TEXT = Color.decode("#141238");
    private static final Color COLOR_MUTED = Color.decode("#5A587A");
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_BORDER = Color.decode("#E2E5EE");
    private static final Color COLOR_SELECTION = Color.decode("#EAEBFA");
    private static final Color COLOR_HOVER_BG = Color.decode("#F0F2FA");

    private final JLabel titleLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] {"Student Number", "Name", "Enrollment Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    StudentListPanel(Runnable onBack) {
        // Matching BorderLayout gaps and padding with SelectionPanel
        super(new BorderLayout(16, 24));
        setBackground(COLOR_SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        // Header Section with absolute center title alignment
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(COLOR_SURFACE);
        GridBagConstraints gbc = new GridBagConstraints();

        RoundedButton backButton = new RoundedButton("← Back", COLOR_WHITE, COLOR_HOVER_BG, COLOR_PRIMARY, COLOR_BORDER, 12);
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(event -> onBack.run());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        header.add(backButton, gbc);

        // Font size matched to 24px (same as SelectionPanel title)
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 16, 0, 16);
        header.add(titleLabel, gbc);

        add(header, BorderLayout.NORTH);

        // Table Styling & Customization
        JTable studentTable = new JTable(tableModel);
        studentTable.setFillsViewportHeight(true);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentTable.setForeground(COLOR_TEXT);
        studentTable.setSelectionBackground(COLOR_SELECTION);
        studentTable.setSelectionForeground(COLOR_TEXT);
        studentTable.setGridColor(COLOR_BORDER);
        studentTable.setRowHeight(40); // Slightly taller rows for an airy, modern feel
        studentTable.setShowVerticalLines(false);

        // Table Header Styling
        JTableHeader tableHeader = studentTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(COLOR_PRIMARY);
        tableHeader.setForeground(COLOR_WHITE);
        tableHeader.setPreferredSize(new Dimension(tableHeader.getPreferredSize().width, 42));
        tableHeader.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        // Custom Padding Cell Renderer
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? COLOR_WHITE : Color.decode("#FAFAFC"));
                }
                return this;
            }
        };

        for (int i = 0; i < studentTable.getColumnCount(); i++) {
            studentTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.getViewport().setBackground(COLOR_WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder()
        ));
        RoundedScrollBarUI.install(scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        // Footer Status Label
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(COLOR_MUTED);
        add(statusLabel, BorderLayout.SOUTH);
    }

    void setTitle(String title) {
        titleLabel.setText(title);
    }

    void showLoading() {
        tableModel.setRowCount(0);
        statusLabel.setText("Loading students...");
    }

    void showError(String message) {
        tableModel.setRowCount(0);
        statusLabel.setText(message);
    }

    void showStudents(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student student : students) {
            tableModel.addRow(new Object[] {
                    student.getSchoolStudentNumber(),
                    fullName(student),
                    student.getEnrollmentStatus()
            });
        }
        statusLabel.setText(students.isEmpty() ? "No students are enrolled in this section." : " ");
    }

    private String fullName(Student student) {
        String middleName = student.getMiddleName();
        return student.getFirstName()
                + (middleName == null || middleName.isBlank()
                        ? ""
                        : " " + middleName.trim().substring(0, 1).toUpperCase(Locale.ROOT) + ".")
                + " " + student.getLastName();
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
}