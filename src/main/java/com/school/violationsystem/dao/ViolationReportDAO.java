package com.school.violationsystem.dao;

import com.school.violationsystem.database.DatabaseConnection;
import com.school.violationsystem.model.ViolationReport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ViolationReportDAO {

    public List<ViolationReport> findAll() throws SQLException {
        String sql = "SELECT * FROM violation_reports";
        List<ViolationReport> reports = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ViolationReport report = new ViolationReport();
                report.setReportId(resultSet.getInt("report_id"));
                report.setCaseId(resultSet.getInt("case_id"));
                report.setReportedBy(resultSet.getInt("reported_by"));
                report.setIncidentDescription(resultSet.getString("incident_description"));
                report.setSubmittedAt(resultSet.getTimestamp("submitted_at").toLocalDateTime());
                report.setReportStatus(resultSet.getString("report_status"));
                reports.add(report);
            }
        }

        return reports;
    }
}
