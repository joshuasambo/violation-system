package com.school.violationsystem.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties PROPERTIES = loadProperties();

    private DatabaseConfig() {
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load database configuration.", e);
        }

        Path localConfig = Path.of("config.properties");
        if (Files.exists(localConfig)) {
            try (InputStream input = Files.newInputStream(localConfig)) {
                properties.load(input);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load database configuration.", e);
            }
        }

        return properties;
    }

    public static String getUrl() {
        return getSetting("DB_URL", "db.url", "jdbc:mysql://localhost:3306/violation_system");
    }

    public static String getUser() {
        return getSetting("DB_USER", "db.user", "violation_app");
    }

    public static String getPassword() {
        return getSetting("DB_PASSWORD", "db.password", "");
    }

    private static String getSetting(String environmentVariable, String property, String defaultValue) {
        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.isEmpty()) {
            return environmentValue;
        }
        return PROPERTIES.getProperty(property, defaultValue);
    }
}
