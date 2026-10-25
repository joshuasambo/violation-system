package com.school.violationsystem.model;

public class Guardian {
    private int guardianId;
    private String fullName;
    private String relationship;
    private String contactNumber;
    private String email;

    public Guardian() {
    }

    public Guardian(int guardianId, String fullName, String relationship, String contactNumber, String email) {
        this.guardianId = guardianId;
        this.fullName = fullName;
        this.relationship = relationship;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public int getGuardianId() {
        return guardianId;
    }

    public void setGuardianId(int guardianId) {
        this.guardianId = guardianId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
