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
        String sql = """
                SELECT vt.violation_type_id, vt.handbook_section_id, hs.section_name,
                       vc.category_id, vc.category_name, vt.violation_name,
                       vt.description, vt.is_active
                FROM violation_types vt
                JOIN handbook_sections hs ON hs.handbook_section_id = vt.handbook_section_id
                JOIN violation_categories vc ON vc.category_id = hs.category_id
                """;
        List<ViolationType> violations = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ViolationType violationType = new ViolationType();
                violationType.setViolationTypeId(resultSet.getInt("violation_type_id"));
                violationType.setHandbookSectionId(resultSet.getInt("handbook_section_id"));
                violationType.setSeverityCategoryId(resultSet.getInt("category_id"));
                violationType.setHandbookSectionName(resultSet.getString("section_name"));
                violationType.setSeverityCategoryName(resultSet.getString("category_name"));
                violationType.setViolationName(resultSet.getString("violation_name"));
                violationType.setDescription(resultSet.getString("description"));
                violationType.setActive(resultSet.getBoolean("is_active"));
                violations.add(violationType);
            }
        }

        return violations;
    }
}
