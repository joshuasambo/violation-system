package com.school.violationsystem.ui;

import com.school.violationsystem.model.User;
import com.school.violationsystem.service.AuthService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class LoginFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(LoginFrame.class.getName());

    // Color Palette Constants
    private static final Color COLOR_PRIMARY = Color.decode("#282385"); // Royal Blue
    private static final Color COLOR_ACCENT = Color.decode("#F5A623");  // Flame Gold
    private static final Color COLOR_SURFACE = Color.decode("#F8F9FC"); // Off-White / Slate
    private static final Color COLOR_TEXT = Color.decode("#141238");    // Deep Navy
    private static final Color COLOR_MUTED = Color.decode("#5A587A");   // Muted Text for Subtitles

    private final AuthService authService = new AuthService();
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JButton loginButton = new JButton("Sign In");
    private final JLabel statusLabel = new JLabel(" ");

    public LoginFrame() {
        var iconUrl = getClass().getResource("/images/school-logo.png");
        if (iconUrl == null) {
            throw new IllegalStateException("Could not find /images/school-logo.png");
        }
        setIconImage(new javax.swing.ImageIcon(iconUrl).getImage());

        setTitle("Violation System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 460);
        setResizable(false);
        setLocationRelativeTo(null);

        // Main Background Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_SURFACE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        // --- Header Section ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridBagLayout());
        headerPanel.setBackground(COLOR_SURFACE);

        GridBagConstraints gbcHeader = new GridBagConstraints();
        gbcHeader.gridx = 0;
        gbcHeader.gridy = 0;
        gbcHeader.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_PRIMARY);
        headerPanel.add(titleLabel, gbcHeader);

        gbcHeader.gridy = 1;
        gbcHeader.insets = new Insets(4, 0, 0, 0);
        JLabel subtitleLabel = new JLabel("Please sign in to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(COLOR_MUTED);
        headerPanel.add(subtitleLabel, gbcHeader);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Form Center Section ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_SURFACE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Username Label & Field
        gbc.gridy = 0;
        gbc.insets = new Insets(16, 0, 4, 0);
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(COLOR_TEXT);
        formPanel.add(userLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(0, 36));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D0D3D9")),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        formPanel.add(usernameField, gbc);

        // Password Label & Field
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(COLOR_TEXT);
        formPanel.add(passLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(0, 36));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D0D3D9")),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        formPanel.add(passwordField, gbc);

        // Login Button
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 12, 0);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(COLOR_PRIMARY);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(0, 40));
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        formPanel.add(loginButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // --- Status Footer Section ---
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(COLOR_SURFACE);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(COLOR_MUTED);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        footerPanel.add(statusLabel, BorderLayout.CENTER);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Action Listeners
        loginButton.addActionListener(event -> submitLogin());
        getRootPane().setDefaultButton(loginButton);
    }

    private void submitLogin() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();
        passwordField.setText("");

        if (username.isEmpty() || password.length == 0) {
            Arrays.fill(password, '\0');
            statusLabel.setForeground(Color.decode("#D93838")); // Error red tone
            statusLabel.setText("Enter both your username and password.");
            return;
        }

        loginButton.setEnabled(false);
        statusLabel.setForeground(COLOR_MUTED);
        statusLabel.setText("Signing in...");

        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws SQLException {
                try {
                    return authService.login(username, password);
                } finally {
                    Arrays.fill(password, '\0');
                }
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                try {
                    User user = get();
                    if (user == null) {
                        statusLabel.setForeground(Color.decode("#D93838"));
                        statusLabel.setText("Invalid username or password.");
                        passwordField.requestFocusInWindow();
                        return;
                    }

                    dispose();
                    new DashboardFrame(user).setVisible(true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.SEVERE, "Login was interrupted.", e);
                    showLoginError("Sign-in was interrupted. Please try again.");
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    LOGGER.log(Level.SEVERE, "Login failed.", cause);
                    if (cause instanceof SQLException) {
                        showLoginError("A database error interrupted sign-in. Check the database settings and try again.");
                    } else {
                        showLoginError("Sign-in failed because of an application error. Contact an administrator.");
                    }
                }
            }
        }.execute();
    }

    private void showLoginError(String message) {
        statusLabel.setForeground(Color.decode("#D93838"));
        statusLabel.setText(message);
        JOptionPane.showMessageDialog(this, message, "Sign-in Error", JOptionPane.ERROR_MESSAGE);
    }
}