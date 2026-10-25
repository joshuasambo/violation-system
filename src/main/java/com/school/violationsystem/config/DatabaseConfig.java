package com.school.violationsystem.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application config.", e);
        }
    }

    public static String getUrl() {
        return properties.getProperty("db.url", "jdbc:mysql://localhost:3306/violation_system");
    }

    public static String getUser() {
        return properties.getProperty("db.user", "violation_app");
    }

    public static String getPassword() {
        return properties.getProperty("db.password", "");
    }
}
