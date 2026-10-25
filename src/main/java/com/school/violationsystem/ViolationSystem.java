package com.school.violationsystem;

import java.sql.Connection;
import java.sql.SQLException;

public class ViolationSystem {

    public static void main(String[] args) {
        System.out.println("Connecting to MySQL...");

        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Successfully connected to MySQL!");
            System.out.println("Database: " +
                    connection.getCatalog());

        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            System.out.println("Error: " + e.getMessage());
        }
    }
}
