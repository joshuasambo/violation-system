package com.school.violationsystem.dao;

import com.school.violationsystem.database.DatabaseConnection;
import com.school.violationsystem.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    public boolean hasNoUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getLong(1) == 0;
        }
    }

    public User findByUsername(String username) throws SQLException {
        String sql = """
                SELECT u.user_id, u.full_name, u.username, u.email, u.password_hash,
                       u.role_id, r.role_name, u.is_active, u.created_at
                FROM users u
                JOIN roles r ON r.role_id = u.role_id
                WHERE u.username = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setUserId(resultSet.getInt("user_id"));
                    user.setFullName(resultSet.getString("full_name"));
                    user.setUsername(resultSet.getString("username"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPasswordHash(resultSet.getString("password_hash"));
                    user.setRoleId(resultSet.getInt("role_id"));
                    user.setRoleName(resultSet.getString("role_name"));
                    user.setActive(resultSet.getBoolean("is_active"));
                    user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    return user;
                }
            }
        }

        return null;
    }

    public User createInitialAdministrator(String fullName, String username, String email, String passwordHash)
            throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int administratorRoleId = lockRole(connection, "Administrator");
                if (countUsers(connection) != 0) {
                    throw new IllegalStateException("Initial administrator setup has already been completed.");
                }

                User user = insertUser(connection, fullName, username, email, passwordHash, administratorRoleId,
                        "Administrator");
                connection.commit();
                return user;
            } catch (SQLException | RuntimeException e) {
                rollback(connection, e);
                throw e;
            }
        }
    }

    public User createTeacherAccount(int administratorUserId, String fullName, String username,
                                     String email, String passwordHash) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int administratorRoleId = lockRole(connection, "Administrator");
                if (!isActiveAdministrator(connection, administratorUserId, administratorRoleId)) {
                    throw new SecurityException("Only an active administrator can create teacher accounts.");
                }

                int teacherRoleId = findRoleId(connection, "Teacher");
                User user = insertUser(connection, fullName, username, email, passwordHash, teacherRoleId, "Teacher");
                connection.commit();
                return user;
            } catch (SQLException | RuntimeException e) {
                rollback(connection, e);
                throw e;
            }
        }
    }

    public void updatePasswordHash(int userId, String passwordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordHash);
            statement.setInt(2, userId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("The account password hash could not be upgraded.");
            }
        }
    }

    private int lockRole(Connection connection, String roleName) throws SQLException {
        String sql = "SELECT role_id FROM roles WHERE role_name = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Required role is missing: " + roleName);
                }
                return resultSet.getInt("role_id");
            }
        }
    }

    private int findRoleId(Connection connection, String roleName) throws SQLException {
        String sql = "SELECT role_id FROM roles WHERE role_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Required role is missing: " + roleName);
                }
                return resultSet.getInt("role_id");
            }
        }
    }

    private long countUsers(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users");
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    private boolean isActiveAdministrator(Connection connection, int userId, int administratorRoleId)
            throws SQLException {
        String sql = """
                SELECT user_id
                FROM users
                WHERE user_id = ? AND role_id = ? AND is_active = TRUE
                FOR UPDATE
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, administratorRoleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private User insertUser(Connection connection, String fullName, String username, String email,
                            String passwordHash, int roleId, String roleName) throws SQLException {
        String sql = """
                INSERT INTO users (full_name, username, email, password_hash, role_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, fullName);
            statement.setString(2, username);
            statement.setString(3, email);
            statement.setString(4, passwordHash);
            statement.setInt(5, roleId);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("User account was created without returning an account ID.");
                }
                User user = new User();
                user.setUserId(keys.getInt(1));
                user.setFullName(fullName);
                user.setUsername(username);
                user.setEmail(email);
                user.setRoleId(roleId);
                user.setRoleName(roleName);
                user.setActive(true);
                return user;
            }
        }
    }

    private void rollback(Connection connection, Exception failure) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            failure.addSuppressed(rollbackFailure);
        }
    }
}
