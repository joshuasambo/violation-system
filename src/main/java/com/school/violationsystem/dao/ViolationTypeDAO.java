package com.school.violationsystem.dao;

import com.school.violationsystem.database.DatabaseConnection;
import com.school.violationsystem.model.ViolationType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ViolationTypeDAO {

    public List<ViolationType> findAll() throws SQLException {
        String sql = "SELECT * FROM violation_types";
        List<ViolationType> violations = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ViolationType violationType = new ViolationType();
                violationType.setViolationTypeId(resultSet.getInt("violation_type_id"));
                violationType.setCategoryId(resultSet.getInt("category_id"));
                violationType.setViolationName(resultSet.getString("violation_name"));
                violationType.setDescription(resultSet.getString("description"));
                violationType.setActive(resultSet.getBoolean("is_active"));
                violations.add(violationType);
            }
        }

        return violations;
    }
}
