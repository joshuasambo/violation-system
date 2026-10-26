package com.school.violationsystem.ui;

import com.school.violationsystem.model.User;
import com.school.violationsystem.service.AuthService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.AbstractBorder;

public class TeacherRegistrationDialog extends JDialog {
    private static final Logger LOGGER = Logger.getLogger(TeacherRegistrationDialog.class.getName());

    // Color Palette Constants
    private static final Color COLOR_PRIMARY = Color.decode("#282385"); // Royal Blue
    private static final Color COLOR_SURFACE = Color.decode("#F8F9FC"); // Off-White / Slate
    private static final Color COLOR_TEXT = Color.decode("#141238");    // Deep Navy
    private static final Color COLOR_MUTED = Color.decode("#5A587A");   // Muted Text
    private static final Color COLOR_WHITE = Color.white;

    private final User administrator;
    private final AuthService authService = new AuthService();
    private final AccountRegistrationPanel registrationPanel = new AccountRegistrationPanel();
    private final JButton createButton = new JButton("Create Teacher Account");
    private final JLabel statusLabel = new JLabel("Only an administrator can create teacher accounts.");

    public TeacherRegistrationDialog(JFrame owner, User administrator) {
        super(owner, "Create Teacher Account", true);
        this.administrator = administrator;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(580, 400);
        setResizable(false);
        setLocationRelativeTo(owner);

        // Root Background Panel
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(COLOR_SURFACE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Style Form Panel Surface
        registrationPanel.setBackground(COLOR_SURFERENCE_SAFE(COLOR_SURFACE));

        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_SURFACE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel titleLabel = new JLabel("Teacher Account Registration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        mainContent.add(headerPanel, BorderLayout.NORTH);
        mainContent.add(registrationPanel, BorderLayout.CENTER);

        // --- Footer Section with Rounded Primary Button ---
        JPanel footer = new JPanel(new BorderLayout(12, 10));
        footer.setBackground(COLOR_SURFACE);
        footer.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(COLOR_MUTED);
        footer.add(statusLabel, BorderLayout.CENTER);

        // Customize Create Button with Rounded Corners
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        createButton.setForeground(COLOR_WHITE);
        createButton.setBackground(COLOR_PRIMARY);
        createButton.setFocusPainted(false);
        createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createButton.setPreferredSize(new Dimension(0, 40));
        createButton.setBorder(new RoundedBorder(8, COLOR_PRIMARY));
        
        footer.add(createButton, BorderLayout.SOUTH);
        mainContent.add(footer, BorderLayout.SOUTH);

        add(mainContent);

        createButton.addActionListener(event -> createTeacher());
        getRootPane().setDefaultButton(createButton);
    }

    private void createTeacher() {
        final AccountRegistrationPanel.RegistrationData data;
        try {
            data = registrationPanel.getRegistrationData();
        } catch (IllegalArgumentException e) {
            statusLabel.setForeground(Color.decode("#D93838"));
            statusLabel.setText(e.getMessage());
            return;
        }

        createButton.setEnabled(false);
        statusLabel.setForeground(COLOR_MUTED);
        statusLabel.setText("Creating teacher account...");

        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws SQLException {
                return authService.createTeacherAccount(
                        administrator, data.fullName(), data.username(), data.email(), data.password());
            }

            @Override
            protected void done() {
                createButton.setEnabled(true);
                registrationPanel.setFormEnabled(true);
                try {
                    get();
                    JOptionPane.showMessageDialog(
                            TeacherRegistrationDialog.this,
                            "Teacher account created successfully.",
                            "Account Created",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.SEVERE, "Teacher account creation was interrupted.", e);
                    showError("Account creation was interrupted. Please try again.");
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    LOGGER.log(Level.SEVERE, "Teacher account creation failed.", cause);
                    if (cause instanceof SecurityException) {
                        showError("Your administrator permission is no longer active.");
                    } else if (cause instanceof SQLException && "23000".equals(((SQLException) cause).getSQLState())) {
                        showError("That username or email is already in use.");
                    } else {
                        showError("Could not create the teacher account. Check the database and try again.");
                    }
                }
            }
        }.execute();
        registrationPanel.setFormEnabled(false);
    }

    private void showError(String message) {
        statusLabel.setForeground(Color.decode("#D93838"));
        statusLabel.setText(message);
        JOptionPane.showMessageDialog(this, message, "Account Error", JOptionPane.ERROR_MESSAGE);
    }

    private Color COLOR_SURFERENCE_SAFE(Color fallback) {
        return fallback;
    }

    /**
     * Custom Border utility for drawing smooth rounded edges on Swing components.
     */
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 12, 6, 12);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 12;
            insets.top = insets.bottom = 6;
            return insets;
        }
    }
}