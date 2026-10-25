package com.school.violationsystem.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class IncidentCase {
    private int caseId;
    private int studentId;
    private int violationTypeId;
    private int academicYearId;
    private LocalDate incidentDate;
    private String caseStatus;
    private Integer reviewedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public IncidentCase() {
    }

    public IncidentCase(int caseId, int studentId, int violationTypeId, int academicYearId,
                       LocalDate incidentDate, String caseStatus, Integer reviewedBy,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.caseId = caseId;
        this.studentId = studentId;
        this.violationTypeId = violationTypeId;
        this.academicYearId = academicYearId;
        this.incidentDate = incidentDate;
        this.caseStatus = caseStatus;
        this.reviewedBy = reviewedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getViolationTypeId() {
        return violationTypeId;
    }

    public void setViolationTypeId(int violationTypeId) {
        this.violationTypeId = violationTypeId;
    }

    public int getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(int academicYearId) {
        this.academicYearId = academicYearId;
    }

    public LocalDate getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
