package com.school.violationsystem.ui;

import com.school.violationsystem.model.User;
import com.school.violationsystem.service.AuthService;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class FirstRunSetupFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(FirstRunSetupFrame.class.getName());

    private final AuthService authService = new AuthService();
    private final AccountRegistrationPanel registrationPanel = new AccountRegistrationPanel();
    private final JButton createButton = new JButton("Create Administrator");
    private final JLabel statusLabel = new JLabel("Create the first administrator account to get started.");

    public FirstRunSetupFrame() {
        setTitle("Violation System - First-Time Setup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 360);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel footer = new JPanel(new BorderLayout(12, 8));
        footer.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 20, 18, 20));
        footer.add(statusLabel, BorderLayout.CENTER);
        footer.add(createButton, BorderLayout.SOUTH);

        add(registrationPanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        createButton.addActionListener(event -> createAdministrator());
        getRootPane().setDefaultButton(createButton);
    }

    private void createAdministrator() {
        final AccountRegistrationPanel.RegistrationData data;
        try {
            data = registrationPanel.getRegistrationData();
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
            return;
        }

        setBusy(true);
        statusLabel.setText("Creating administrator account...");

        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws SQLException {
                return authService.createInitialAdministrator(
                        data.fullName(), data.username(), data.email(), data.password());
            }

            @Override
            protected void done() {
                setBusy(false);
                try {
                    get();
                    JOptionPane.showMessageDialog(
                            FirstRunSetupFrame.this,
                            "Administrator account created. You can now sign in.",
                            "Setup Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new LoginFrame().setVisible(true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.SEVERE, "Administrator setup was interrupted.", e);
                    showError("Account setup was interrupted. Please try again.");
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    LOGGER.log(Level.SEVERE, "Administrator account setup failed.", cause);
                    if (cause instanceof IllegalStateException) {
                        JOptionPane.showMessageDialog(
                                FirstRunSetupFrame.this,
                                "An account has already been created. Please sign in.",
                                "Setup Already Completed",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new LoginFrame().setVisible(true);
                    } else if (cause instanceof SQLException && "23000".equals(((SQLException) cause).getSQLState())) {
                        showError("That username or email is already in use.");
                    } else {
                        showError("Could not create the administrator account. Check the database and try again.");
                    }
                }
            }
        }.execute();
    }

    private void setBusy(boolean busy) {
        createButton.setEnabled(!busy);
        registrationPanel.setFormEnabled(!busy);
    }

    private void showError(String message) {
        statusLabel.setText(message);
        JOptionPane.showMessageDialog(this, message, "Setup Error", JOptionPane.ERROR_MESSAGE);
    }
}
