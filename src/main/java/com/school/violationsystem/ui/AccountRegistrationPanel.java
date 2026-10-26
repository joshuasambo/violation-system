package com.school.violationsystem.ui;

import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

final class AccountRegistrationPanel extends JPanel {
    private final JTextField fullNameField = new JTextField();
    private final JTextField usernameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField confirmPasswordField = new JPasswordField();

    AccountRegistrationPanel() {
        super(new GridLayout(5, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        add(new JLabel("Full name:"));
        add(fullNameField);
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Password (12+ characters):"));
        add(passwordField);
        add(new JLabel("Confirm password:"));
        add(confirmPasswordField);
    }

    void setFormEnabled(boolean enabled) {
        fullNameField.setEnabled(enabled);
        usernameField.setEnabled(enabled);
        emailField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        confirmPasswordField.setEnabled(enabled);
    }

    RegistrationData getRegistrationData() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        char[] password = passwordField.getPassword();
        char[] confirmation = confirmPasswordField.getPassword();
        passwordField.setText("");
        confirmPasswordField.setText("");

        try {
            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty()) {
                throw new IllegalArgumentException("Enter your full name, username, and email.");
            }
            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                throw new IllegalArgumentException("Enter a valid email address.");
            }
            if (password.length < 12) {
                throw new IllegalArgumentException("Password must be at least 12 characters.");
            }
            if (!Arrays.equals(password, confirmation)) {
                throw new IllegalArgumentException("The passwords do not match.");
            }
            return new RegistrationData(fullName, username, email, password);
        } catch (RuntimeException e) {
            Arrays.fill(password, '\0');
            throw e;
        } finally {
            Arrays.fill(confirmation, '\0');
        }
    }

    record RegistrationData(String fullName, String username, String email, char[] password) {
    }
}
