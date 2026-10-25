package com.school.violationsystem.model;

import java.time.LocalDateTime;

public class ViolationReport {
    private int reportId;
    private int caseId;
    private int reportedBy;
    private String incidentDescription;
    private LocalDateTime submittedAt;
    private String reportStatus;

    public ViolationReport() {
    }

    public ViolationReport(int reportId, int caseId, int reportedBy, String incidentDescription,
                          LocalDateTime submittedAt, String reportStatus) {
        this.reportId = reportId;
        this.caseId = caseId;
        this.reportedBy = reportedBy;
        this.incidentDescription = incidentDescription;
        this.submittedAt = submittedAt;
        this.reportStatus = reportStatus;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public int getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(int reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }
}
