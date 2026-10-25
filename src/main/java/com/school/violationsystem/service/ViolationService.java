package com.school.violationsystem.service;

import com.school.violationsystem.dao.ViolationTypeDAO;
import com.school.violationsystem.model.ViolationType;
import java.sql.SQLException;
import java.util.List;

public class ViolationService {
    private final ViolationTypeDAO violationTypeDAO = new ViolationTypeDAO();

    public List<ViolationType> getViolationTypes() throws SQLException {
        return violationTypeDAO.findAll();
    }
}
