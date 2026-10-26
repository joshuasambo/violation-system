package com.school.violationsystem;

import com.school.violationsystem.config.DatabaseConfig;
import com.school.violationsystem.service.AuthService;
import com.school.violationsystem.ui.FirstRunSetupFrame;
import com.school.violationsystem.ui.LoginFrame;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

public class ViolationSystem {
    private static final Logger LOGGER = Logger.getLogger(ViolationSystem.class.getName());
    private static final String REPAIR_FLYWAY_CHECKSUMS_ARGUMENT = "--repair-flyway-checksums";

    public static void main(String[] args) {
        final boolean initialSetupRequired;
        try {
            initializeDatabase(Arrays.asList(args).contains(REPAIR_FLYWAY_CHECKSUMS_ARGUMENT));
            initialSetupRequired = new AuthService().isInitialSetupRequired();
        } catch (SQLException | FlywayException e) {
            LOGGER.log(Level.SEVERE, "Database startup or schema initialization failed.", e);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        null,
                        "Could not initialize the database. Make sure MySQL is running, the configured "
                                + "database exists, and the account has permission to create tables.",
                        "Database Setup Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
            return;
        }

        SwingUtilities.invokeLater(() -> {
            if (initialSetupRequired) {
                new FirstRunSetupFrame().setVisible(true);
            } else {
                new LoginFrame().setVisible(true);
            }
        });
    }

    private static void initializeDatabase(boolean repairFlywayChecksums) throws SQLException {
        String url = DatabaseConfig.getUrl();
        String user = DatabaseConfig.getUser();
        String password = DatabaseConfig.getPassword();

        if (url.isBlank() || user.isBlank() || password.isEmpty()) {
            throw new SQLException(
                    "Database settings are incomplete. Configure DB_URL, DB_USER, and DB_PASSWORD.");
        }

        Flyway flyway = Flyway.configure()
                .dataSource(url, user, password)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load();

        if (repairFlywayChecksums) {
            LOGGER.warning("Repairing Flyway migration checksums as explicitly requested.");
            flyway.repair();
        }

        flyway.migrate();
    }
}
