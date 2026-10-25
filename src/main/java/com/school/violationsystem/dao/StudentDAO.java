package com.school.violationsystem.dao;

import com.school.violationsystem.database.DatabaseConnection;
import com.school.violationsystem.model.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

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
