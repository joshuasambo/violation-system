package com.school.violationsystem.model;

public class Student {
    private int studentId;
    private String schoolStudentNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String enrollmentStatus;

    public Student() {
    }

    public Student(int studentId, String schoolStudentNumber, String firstName, String middleName,
                  String lastName, String enrollmentStatus) {
        this.studentId = studentId;
        this.schoolStudentNumber = schoolStudentNumber;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.enrollmentStatus = enrollmentStatus;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getSchoolStudentNumber() {
        return schoolStudentNumber;
    }

    public void setSchoolStudentNumber(String schoolStudentNumber) {
        this.schoolStudentNumber = schoolStudentNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }
}
