package com.school.violationsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/violation_system";

    private static final String USER = "violation_app";

    // Replace this placeholder with the password you created in MySQL.
    private static final String PASSWORD = "joshuasambo.1234512345";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
