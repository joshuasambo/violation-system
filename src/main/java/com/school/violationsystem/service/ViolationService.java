package com.school.violationsystem.service;

import com.school.violationsystem.dao.ViolationTypeDAO;
import com.school.violationsystem.dao.ViolationReportDAO;
import com.school.violationsystem.model.IncidentCaseSummary;
import com.school.violationsystem.model.StudentViolationReport;
import com.school.violationsystem.model.User;
import com.school.violationsystem.model.ViolationType;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ViolationService {
    private final ViolationTypeDAO violationTypeDAO = new ViolationTypeDAO();
    private final ViolationReportDAO violationReportDAO = new ViolationReportDAO();

    public List<ViolationType> getViolationTypes() throws SQLException {
        return violationTypeDAO.findAll();
    }

    public List<ViolationType> getActiveViolationTypes() throws SQLException {
        return violationTypeDAO.findAll().stream()
                .filter(ViolationType::isActive)
                .toList();
    }

    public List<StudentViolationReport> getReportsByStudent(int studentId) throws SQLException {
        return violationReportDAO.findByStudentId(studentId);
    }

    public List<IncidentCaseSummary> getReportableCasesByStudent(int studentId, User user) throws SQLException {
        validateReporter(user);
        if (studentId <= 0) {
            throw new IllegalArgumentException("Select a valid student.");
        }
        return violationReportDAO.findReportableCasesByStudentId(studentId);
    }

    public void submitReport(
            int studentId,
            int violationTypeId,
            User reporter,
            LocalDate incidentDate,
            LocalTime incidentTime,
            String incidentLocation,
            String incidentDescription) throws SQLException {
        validateReporter(reporter);
        if (studentId <= 0 || violationTypeId <= 0 || incidentDate == null
                || incidentDescription == null || incidentDescription.isBlank()) {
            throw new IllegalArgumentException("Choose a violation type, incident date, and description.");
        }
        violationReportDAO.createForStudent(
                studentId, violationTypeId, reporter.getUserId(), incidentDate, incidentTime,
                incidentLocation == null || incidentLocation.isBlank() ? null : incidentLocation.trim(),
                incidentDescription.trim());
    }

    public void submitAdditionalReport(
            int studentId,
            int caseId,
            User reporter,
            String incidentDescription) throws SQLException {
        validateReporter(reporter);
        if (studentId <= 0 || caseId <= 0
                || incidentDescription == null || incidentDescription.isBlank()) {
            throw new IllegalArgumentException("Choose an incident and enter a report description.");
        }
        violationReportDAO.addReportToExistingCase(
                studentId, caseId, reporter.getUserId(), incidentDescription.trim());
    }

    private void validateReporter(User reporter) {
        if (reporter == null || reporter.getUserId() <= 0 || !reporter.isActive()
                || !("Teacher".equals(reporter.getRoleName())
                        || "Administrator".equals(reporter.getRoleName()))) {
            throw new SecurityException("Only an active teacher or administrator can submit incident reports.");
        }
    }
}
