package com.school.violationsystem.ui.panels.students;

import com.school.violationsystem.model.Student;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

final class StudentListPanel extends JPanel {
    private static final Color COLOR_SURFACE = Color.decode("#F8F9FC");
    private static final Color COLOR_TEXT = Color.decode("#141238");
    private static final Color COLOR_MUTED = Color.decode("#5A587A");

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
        super(new BorderLayout(16, 16));
        setBackground(COLOR_SURFACE);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(COLOR_TEXT);
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backButton.setFocusPainted(false);
        backButton.addActionListener(event -> onBack.run());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_SURFACE);
        header.add(backButton, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JTable studentTable = new JTable(tableModel);
        studentTable.setFillsViewportHeight(true);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
                + (middleName == null || middleName.isBlank() ? "" : " " + middleName)
                + " " + student.getLastName();
    }
}
