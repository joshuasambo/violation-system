package com.school.violationsystem.dao;

import com.school.violationsystem.database.DatabaseConnection;
import com.school.violationsystem.model.IncidentCase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class IncidentCaseDAO {

    public List<IncidentCase> findAll() throws SQLException {
        String sql = "SELECT * FROM incident_cases";
        List<IncidentCase> cases = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                IncidentCase incidentCase = new IncidentCase();
                incidentCase.setCaseId(resultSet.getInt("case_id"));
                incidentCase.setStudentId(resultSet.getInt("student_id"));
                incidentCase.setViolationTypeId(resultSet.getInt("violation_type_id"));
                incidentCase.setAcademicYearId(resultSet.getInt("academic_year_id"));
                incidentCase.setIncidentDate(resultSet.getDate("incident_date").toLocalDate());
                Time incidentTime = resultSet.getTime("incident_time");
                incidentCase.setIncidentTime(incidentTime == null ? null : incidentTime.toLocalTime());
                incidentCase.setIncidentLocation(resultSet.getString("incident_location"));
                incidentCase.setCaseStatus(resultSet.getString("case_status"));
                incidentCase.setReviewOutcome(resultSet.getString("review_outcome"));
                incidentCase.setConfirmedClassification(resultSet.getString("confirmed_classification"));
                incidentCase.setReviewedBy(resultSet.getObject("reviewed_by", Integer.class));
                java.sql.Timestamp reviewedAt = resultSet.getTimestamp("reviewed_at");
                incidentCase.setReviewedAt(reviewedAt == null ? null : reviewedAt.toLocalDateTime());
                incidentCase.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                incidentCase.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
                cases.add(incidentCase);
            }
        }

        return cases;
    }
}
