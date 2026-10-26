package com.school.violationsystem.dao;

import com.school.violationsystem.database.DatabaseConnection;
import com.school.violationsystem.model.IncidentCaseSummary;
import com.school.violationsystem.model.StudentViolationReport;
import com.school.violationsystem.model.ViolationReport;
import java.sql.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ViolationReportDAO {

    public List<StudentViolationReport> findByStudentId(int studentId) throws SQLException {
        String sql = """
                SELECT vr.report_id, ic.case_id, vt.violation_name, ic.incident_date,
                       vr.incident_description, vr.submitted_at, vr.report_status,
                       ic.review_outcome
                FROM violation_reports vr
                JOIN incident_cases ic ON ic.case_id = vr.case_id
                JOIN violation_types vt ON vt.violation_type_id = ic.violation_type_id
                WHERE ic.student_id = ?
                ORDER BY vr.submitted_at DESC, vr.report_id DESC
                """;
        List<StudentViolationReport> reports = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reports.add(new StudentViolationReport(
                            resultSet.getInt("report_id"),
                            resultSet.getInt("case_id"),
                            resultSet.getString("violation_name"),
                            resultSet.getDate("incident_date").toLocalDate(),
                            resultSet.getString("incident_description"),
                            resultSet.getTimestamp("submitted_at").toLocalDateTime(),
                            resultSet.getString("report_status"),
                            resultSet.getString("review_outcome")));
                }
            }
        }

        return reports;
    }

    public List<IncidentCaseSummary> findReportableCasesByStudentId(int studentId) throws SQLException {
        String sql = """
                SELECT ic.case_id, vt.violation_name, vc.category_name AS classification,
                       ic.incident_date, ic.incident_time, ic.incident_location,
                       ay.year_name, ic.case_status
                FROM incident_cases ic
                JOIN violation_types vt ON vt.violation_type_id = ic.violation_type_id
                JOIN handbook_sections hs ON hs.handbook_section_id = vt.handbook_section_id
                JOIN violation_categories vc ON vc.category_id = hs.category_id
                JOIN academic_years ay ON ay.academic_year_id = ic.academic_year_id
                WHERE ic.student_id = ?
                  AND ic.case_status IN ('PENDING_REVIEW', 'UNDER_REVIEW', 'AWAITING_INFORMATION')
                  AND ic.review_outcome IN ('UNRESOLVED', 'FURTHER_REVIEW_REQUIRED')
                ORDER BY ic.incident_date DESC, ic.case_id DESC
                """;
        List<IncidentCaseSummary> cases = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    java.sql.Time incidentTime = resultSet.getTime("incident_time");
                    cases.add(new IncidentCaseSummary(
                            resultSet.getInt("case_id"),
                            resultSet.getString("violation_name"),
                            resultSet.getString("classification"),
                            resultSet.getDate("incident_date").toLocalDate(),
                            incidentTime == null ? null : incidentTime.toLocalTime(),
                            resultSet.getString("incident_location"),
                            resultSet.getString("year_name"),
                            resultSet.getString("case_status")));
                }
            }
        }

        return cases;
    }

    public void createForStudent(
            int studentId,
            int violationTypeId,
            int reportedBy,
            LocalDate incidentDate,
            LocalTime incidentTime,
            String incidentLocation,
            String incidentDescription) throws SQLException {
        String academicYearSql = """
                SELECT academic_year_id
                FROM academic_years
                WHERE ? BETWEEN start_date AND end_date
                ORDER BY start_date DESC, academic_year_id DESC
                LIMIT 1
                """;
        String insertCaseSql = """
                INSERT INTO incident_cases (
                    student_id, violation_type_id, academic_year_id, incident_date,
                    incident_time, incident_location
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        String insertReportSql = """
                INSERT INTO violation_reports (
                    case_id, reported_by, incident_description
                )
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int academicYearId;
                try (PreparedStatement statement = connection.prepareStatement(academicYearSql)) {
                    statement.setDate(1, Date.valueOf(incidentDate));
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new SQLException(
                                    "The incident date does not fall within a configured academic year.");
                        }
                        academicYearId = resultSet.getInt("academic_year_id");
                    }
                }

                int caseId;
                try (PreparedStatement statement = connection.prepareStatement(
                        insertCaseSql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, studentId);
                    statement.setInt(2, violationTypeId);
                    statement.setInt(3, academicYearId);
                    statement.setDate(4, Date.valueOf(incidentDate));
                    if (incidentTime == null) {
                        statement.setNull(5, java.sql.Types.TIME);
                    } else {
                        statement.setTime(5, java.sql.Time.valueOf(incidentTime));
                    }
                    statement.setString(6, incidentLocation);
                    statement.executeUpdate();
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (!generatedKeys.next()) {
                            throw new SQLException("The incident case was created without returning its generated ID.");
                        }
                        caseId = generatedKeys.getInt(1);
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(insertReportSql)) {
                    statement.setInt(1, caseId);
                    statement.setInt(2, reportedBy);
                    statement.setString(3, incidentDescription);
                    statement.executeUpdate();
                }

                insertAuditLog(connection, reportedBy, "CREATE_INCIDENT_CASE", "incident_cases",
                        caseId, "{\"initialReportCreated\":true}");
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
                throw e;
            }
        }
    }

    public void addReportToExistingCase(
            int studentId,
            int caseId,
            int reportedBy,
            String incidentDescription) throws SQLException {
        String validateCaseSql = """
                SELECT case_id
                FROM incident_cases
                WHERE case_id = ?
                  AND student_id = ?
                  AND case_status IN ('PENDING_REVIEW', 'UNDER_REVIEW', 'AWAITING_INFORMATION')
                  AND review_outcome IN ('UNRESOLVED', 'FURTHER_REVIEW_REQUIRED')
                FOR UPDATE
                """;
        String insertReportSql = """
                INSERT INTO violation_reports (case_id, reported_by, incident_description)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(validateCaseSql)) {
                    statement.setInt(1, caseId);
                    statement.setInt(2, studentId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new SQLException(
                                    "This incident is no longer open for additional reports or does not belong to this student.");
                        }
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(insertReportSql)) {
                    statement.setInt(1, caseId);
                    statement.setInt(2, reportedBy);
                    statement.setString(3, incidentDescription);
                    statement.executeUpdate();
                }

                insertAuditLog(connection, reportedBy, "ADD_INCIDENT_REPORT", "incident_cases",
                        caseId, "{\"reportAdded\":true}");
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
                throw e;
            }
        }
    }

    private void insertAuditLog(
            Connection connection, int userId, String action, String entityType, int entityId, String details)
            throws SQLException {
        String sql = """
                INSERT INTO audit_logs (user_id, action, entity_type, entity_id, details)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, action);
            statement.setString(3, entityType);
            statement.setInt(4, entityId);
            statement.setString(5, details);
            statement.executeUpdate();
        }
    }

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
