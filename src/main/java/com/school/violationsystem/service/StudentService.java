package com.school.violationsystem.service;

import com.school.violationsystem.dao.StudentDAO;
import com.school.violationsystem.model.GradeLevel;
import com.school.violationsystem.model.Guardian;
import com.school.violationsystem.model.Section;
import com.school.violationsystem.model.Student;
import com.school.violationsystem.model.StudentPlacement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class StudentService {
    private final StudentDAO studentDAO = new StudentDAO();

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    public List<GradeLevel> getAvailableGradeLevels() throws SQLException {
        return studentDAO.findAvailableGradeLevels();
    }

    public List<Section> getSectionsByGradeLevel(int gradeLevelId) throws SQLException {
        return studentDAO.findSectionsByGradeLevelId(gradeLevelId);
    }

    public List<Student> getStudentsBySection(int sectionId) throws SQLException {
        return studentDAO.findStudentsBySectionId(sectionId);
    }

    public List<Guardian> getGuardiansByStudent(int studentId) throws SQLException {
        return studentDAO.findGuardiansByStudentId(studentId);
    }

    public Optional<StudentPlacement> getActivePlacementByStudent(int studentId) throws SQLException {
        return studentDAO.findActivePlacementByStudentId(studentId);
    }
}
