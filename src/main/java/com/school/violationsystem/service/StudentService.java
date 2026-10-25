package com.school.violationsystem.service;

import com.school.violationsystem.dao.StudentDAO;
import com.school.violationsystem.model.Student;
import java.sql.SQLException;
import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO = new StudentDAO();

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }
}
