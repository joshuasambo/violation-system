package com.school.violationsystem.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record IncidentCaseSummary(
        int caseId,
        String violationName,
        String severityCategory,
        LocalDate incidentDate,
        LocalTime incidentTime,
        String incidentLocation,
        String academicYearName,
        String caseStatus) {

    @Override
    public String toString() {
        String classification = severityCategory == null || severityCategory.isBlank()
                ? "" : " (" + severityCategory + ")";
        return "Case #" + caseId + " | " + violationName + classification + " | " + incidentDate
                + (incidentTime == null ? "" : " " + incidentTime)
                + (incidentLocation == null || incidentLocation.isBlank() ? "" : " | " + incidentLocation)
                + " | " + academicYearName
                + " | " + caseStatus.replace('_', ' ');
    }
}
