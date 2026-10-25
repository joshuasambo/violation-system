package com.school.violationsystem.model;

public class ViolationType {
    private int violationTypeId;
    private int categoryId;
    private String violationName;
    private String description;
    private boolean active;

    public ViolationType() {
    }

    public ViolationType(int violationTypeId, int categoryId, String violationName, String description, boolean active) {
        this.violationTypeId = violationTypeId;
        this.categoryId = categoryId;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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
