package com.school.violationsystem.database;

import com.school.violationsystem.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        String url = DatabaseConfig.getUrl();
        String user = DatabaseConfig.getUser();
        String password = DatabaseConfig.getPassword();

        if (url.isBlank() || user.isBlank() || password.isEmpty()) {
            throw new SQLException(
                    "Database settings are incomplete. Configure DB_URL, DB_USER, and DB_PASSWORD.");
        }

        return DriverManager.getConnection(url, user, password);
    }
}
