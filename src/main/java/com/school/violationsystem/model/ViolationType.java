package com.school.violationsystem.model;

public class ViolationType {
    private int violationTypeId;
    private int handbookSectionId;
    private int severityCategoryId;
    private String handbookSectionName;
    private String severityCategoryName;
    private String violationName;
    private String description;
    private boolean active;

    public ViolationType() {
    }

    public ViolationType(
            int violationTypeId, int handbookSectionId, String violationName, String description, boolean active) {
        this.violationTypeId = violationTypeId;
        this.handbookSectionId = handbookSectionId;
        this.violationName = violationName;
        this.description = description;
        this.active = active;
    }

    public int getViolationTypeId() {
        return violationTypeId;
    }

    public void setViolationTypeId(int violationTypeId) {
        this.violationTypeId = violationTypeId;
    }

    public int getHandbookSectionId() {
        return handbookSectionId;
    }

    public void setHandbookSectionId(int handbookSectionId) {
        this.handbookSectionId = handbookSectionId;
    }

    public int getSeverityCategoryId() {
        return severityCategoryId;
    }

    public void setSeverityCategoryId(int severityCategoryId) {
        this.severityCategoryId = severityCategoryId;
    }

    public String getHandbookSectionName() {
        return handbookSectionName;
    }

    public void setHandbookSectionName(String handbookSectionName) {
        this.handbookSectionName = handbookSectionName;
    }

    public String getSeverityCategoryName() {
        return severityCategoryName;
    }

    public void setSeverityCategoryName(String severityCategoryName) {
        this.severityCategoryName = severityCategoryName;
    }

    public String getViolationName() {
        return violationName;
    }

    public void setViolationName(String violationName) {
        this.violationName = violationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
