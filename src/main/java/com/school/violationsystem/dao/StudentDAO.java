package com.school.violationsystem.dao;

import com.school.violationsystem.database.DatabaseConnection;
import com.school.violationsystem.model.GradeLevel;
import com.school.violationsystem.model.Guardian;
import com.school.violationsystem.model.Section;
import com.school.violationsystem.model.Student;
import com.school.violationsystem.model.StudentPlacement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAO {

    public List<GradeLevel> findAvailableGradeLevels() throws SQLException {
        String sql = """
                SELECT DISTINCT gl.grade_level_id, gl.grade_name
                FROM grade_levels gl
                JOIN sections s ON s.grade_level_id = gl.grade_level_id
                JOIN academic_years ay ON ay.academic_year_id = s.academic_year_id
                WHERE ay.is_active = TRUE
                ORDER BY gl.grade_name, gl.grade_level_id
                """;
        List<GradeLevel> gradeLevels = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                gradeLevels.add(new GradeLevel(
                        resultSet.getInt("grade_level_id"),
                        resultSet.getString("grade_name")));
            }
        }

        return gradeLevels;
    }

    public List<Section> findSectionsByGradeLevelId(int gradeLevelId) throws SQLException {
        String sql = """
                SELECT s.section_id, s.section_name, ay.year_name
                FROM sections s
                JOIN academic_years ay ON ay.academic_year_id = s.academic_year_id
                WHERE s.grade_level_id = ? AND ay.is_active = TRUE
                ORDER BY ay.year_name DESC, s.section_name
                """;
        List<Section> sections = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gradeLevelId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sections.add(new Section(
                            resultSet.getInt("section_id"),
                            resultSet.getString("section_name"),
                            resultSet.getString("year_name")));
                }
            }
        }

        return sections;
    }

    public List<Student> findStudentsBySectionId(int sectionId) throws SQLException {
        String sql = """
                SELECT DISTINCT st.student_id, st.school_student_number, st.first_name,
                       st.middle_name, st.last_name, e.enrollment_status
                FROM student_enrollments e
                JOIN students st ON st.student_id = e.student_id
                JOIN sections s ON s.section_id = e.section_id
                JOIN academic_years ay ON ay.academic_year_id = s.academic_year_id
                WHERE e.section_id = ?
                  AND e.academic_year_id = s.academic_year_id
                  AND ay.is_active = TRUE
                ORDER BY st.first_name, st.middle_name, st.last_name
                """;
        List<Student> students = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sectionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Student student = new Student();
                    student.setStudentId(resultSet.getInt("student_id"));
                    student.setSchoolStudentNumber(resultSet.getString("school_student_number"));
                    student.setFirstName(resultSet.getString("first_name"));
                    student.setMiddleName(resultSet.getString("middle_name"));
                    student.setLastName(resultSet.getString("last_name"));
                    student.setEnrollmentStatus(resultSet.getString("enrollment_status"));
                    students.add(student);
                }
            }
        }

        return students;
    }

    public List<Guardian> findGuardiansByStudentId(int studentId) throws SQLException {
        String sql = """
                SELECT g.guardian_id, g.full_name, g.relationship, g.contact_number, g.email
                FROM student_guardians sg
                JOIN guardians g ON g.guardian_id = sg.guardian_id
                WHERE sg.student_id = ?
                ORDER BY sg.is_primary_contact DESC, g.full_name
                """;
        List<Guardian> guardians = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    guardians.add(new Guardian(
                            resultSet.getInt("guardian_id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("relationship"),
                            resultSet.getString("contact_number"),
                            resultSet.getString("email")));
                }
            }
        }

        return guardians;
    }

    public Optional<StudentPlacement> findActivePlacementByStudentId(int studentId) throws SQLException {
        String sql = """
                SELECT gl.grade_name, s.section_name
                FROM student_enrollments e
                JOIN sections s ON s.section_id = e.section_id
                JOIN grade_levels gl ON gl.grade_level_id = s.grade_level_id
                JOIN academic_years ay ON ay.academic_year_id = e.academic_year_id
                WHERE e.student_id = ? AND ay.is_active = TRUE
                ORDER BY e.enrollment_id DESC
                LIMIT 1
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new StudentPlacement(
                            resultSet.getString("grade_name"),
                            resultSet.getString("section_name")));
                }
            }
        }

        return Optional.empty();
    }

    public List<Student> findAll() throws SQLException {
        String sql = "SELECT * FROM students";
        List<Student> students = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Student student = new Student();
                student.setStudentId(resultSet.getInt("student_id"));
                student.setSchoolStudentNumber(resultSet.getString("school_student_number"));
                student.setFirstName(resultSet.getString("first_name"));
                student.setMiddleName(resultSet.getString("middle_name"));
                student.setLastName(resultSet.getString("last_name"));
                student.setEnrollmentStatus(resultSet.getString("enrollment_status"));
                students.add(student);
            }
        }

        return students;
    }
}
