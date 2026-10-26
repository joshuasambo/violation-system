package com.school.violationsystem.service;

import com.school.violationsystem.dao.UserDAO;
import com.school.violationsystem.model.User;
import com.school.violationsystem.util.PasswordUtil;
import java.sql.SQLException;
import java.util.Arrays;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public boolean isInitialSetupRequired() throws SQLException {
        return userDAO.hasNoUsers();
    }

    public User login(String username, char[] password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.length == 0) {
            return null;
        }

        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            return null;
        }

        if (!user.isActive()) {
            return null;
        }

        if (PasswordUtil.matches(password, user.getPasswordHash())) {
            return user;
        }

        if (PasswordUtil.matchesLegacySha256(password, user.getPasswordHash())) {
            userDAO.updatePasswordHash(user.getUserId(), PasswordUtil.hash(password));
            return user;
        }

        return null;
    }

    public User createInitialAdministrator(String fullName, String username, String email, char[] password)
            throws SQLException {
        validateRegistration(fullName, username, email, password);
        try {
            return userDAO.createInitialAdministrator(
                    fullName.trim(), username.trim(), email.trim(), PasswordUtil.hash(password));
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public User createTeacherAccount(User administrator, String fullName, String username, String email,
                                     char[] password) throws SQLException {
        if (administrator == null || !administrator.isActive()
                || !"Administrator".equals(administrator.getRoleName())) {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
            throw new SecurityException("Only an active administrator can create teacher accounts.");
        }

        validateRegistration(fullName, username, email, password);
        try {
            return userDAO.createTeacherAccount(
                    administrator.getUserId(), fullName.trim(), username.trim(), email.trim(),
                    PasswordUtil.hash(password));
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void validateRegistration(String fullName, String username, String email, char[] password) {
        if (fullName == null || fullName.isBlank()
                || username == null || username.isBlank()
                || email == null || email.isBlank()
                || password == null || password.length < 12) {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
            throw new IllegalArgumentException(
                    "Enter a name, username, email, and a password with at least 12 characters.");
        }
    }
}
