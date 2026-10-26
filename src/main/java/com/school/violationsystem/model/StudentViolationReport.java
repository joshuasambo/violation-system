package com.school.violationsystem.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudentViolationReport(
        int reportId,
        int caseId,
        String violationName,
        LocalDate incidentDate,
        String incidentDescription,
        LocalDateTime submittedAt,
        String reportStatus,
        String reviewOutcome) {
}
