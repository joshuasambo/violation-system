package com.school.violationsystem.service;

import com.school.violationsystem.dao.UserDAO;
import com.school.violationsystem.model.User;
import com.school.violationsystem.util.PasswordUtil;
import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) throws SQLException {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null;
        }

        if (!user.isActive()) {
            return null;
        }

        if (PasswordUtil.matches(password, user.getPasswordHash())) {
            return user;
        }

        return null;
    }
}
