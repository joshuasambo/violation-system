-- Fictional sample records for development and demonstration; no real contact details are used.

-- Adds the demo academic year when there is no active year already configured.
-- Columns: academicYearId, yearName, startDate, endDate, isActive.
-- Auto-generated ID: academicYearId.
INSERT INTO academic_years (year_name, start_date, end_date, is_active)
SELECT '2026-2027', '2026-06-01', '2027-03-31', TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM academic_years
    WHERE year_name = '2026-2027'
)
AND NOT EXISTS (
    SELECT 1
    FROM academic_years
    WHERE is_active = TRUE
);

-- Creates the grade levels used by the demo sections and student enrollments.
-- Columns: gradeLevelId, gradeName.
-- Auto-generated ID: gradeLevelId.
INSERT INTO grade_levels (grade_name)
SELECT seed.grade_name
FROM (
    SELECT 'Grade 7' AS grade_name
    UNION ALL SELECT 'Grade 8'
    UNION ALL SELECT 'Grade 9'
    UNION ALL SELECT 'Grade 10'
    UNION ALL SELECT 'Grade 11'
    UNION ALL SELECT 'Grade 12'
) seed
WHERE NOT EXISTS (
    SELECT 1
    FROM grade_levels existing
    WHERE existing.grade_name = seed.grade_name
);

-- Creates two demo sections for each grade in the active academic year.
-- Columns: sectionId, sectionName, gradeLevelId, academicYearId.
-- Auto-generated ID: sectionId.
INSERT INTO sections (section_name, grade_level_id, academic_year_id)
SELECT seed.section_name, grade.grade_level_id, school_year.academic_year_id
FROM (
    SELECT 'Grade 7' AS grade_name, 'Section A' AS section_name
    UNION ALL SELECT 'Grade 7', 'Section B'
    UNION ALL SELECT 'Grade 8', 'Section A'
    UNION ALL SELECT 'Grade 8', 'Section B'
    UNION ALL SELECT 'Grade 9', 'Section A'
    UNION ALL SELECT 'Grade 9', 'Section B'
    UNION ALL SELECT 'Grade 10', 'Section A'
    UNION ALL SELECT 'Grade 10', 'Section B'
    UNION ALL SELECT 'Grade 11', 'Section A'
    UNION ALL SELECT 'Grade 11', 'Section B'
    UNION ALL SELECT 'Grade 12', 'Section A'
    UNION ALL SELECT 'Grade 12', 'Section B'
) seed
JOIN grade_levels grade
    ON grade.grade_name = seed.grade_name
   AND grade.grade_level_id = (
       SELECT MIN(existing.grade_level_id)
       FROM grade_levels existing
       WHERE existing.grade_name = seed.grade_name
   )
JOIN academic_years school_year
    ON school_year.academic_year_id = (
        SELECT active_year.academic_year_id
        FROM academic_years active_year
        WHERE active_year.is_active = TRUE
        ORDER BY active_year.start_date DESC, active_year.academic_year_id DESC
        LIMIT 1
    )
WHERE NOT EXISTS (
    SELECT 1
    FROM sections existing
    WHERE existing.section_name = seed.section_name
      AND existing.grade_level_id = grade.grade_level_id
      AND existing.academic_year_id = school_year.academic_year_id
);


-- Adds twelve fictional students for demonstrating the student workflows.
-- Columns: studentId, schoolStudentNumber, firstName, middleName, lastName, enrollmentStatus.
-- Auto-generated ID: studentId.
INSERT INTO students (
    school_student_number, first_name, middle_name, last_name, enrollment_status
)
SELECT seed.school_student_number, seed.first_name, seed.middle_name, seed.last_name, 'Active'
FROM (
    SELECT 'DEMO-2026-0001' AS school_student_number, 'Demo Student 01' AS first_name,
           NULL AS middle_name, 'Example' AS last_name
    UNION ALL SELECT 'DEMO-2026-0002', 'Demo Student 02', NULL, 'Example'
    UNION ALL SELECT 'DEMO-2026-0003', 'Demo Student 03', NULL, 'Example'
    UNION ALL SELECT 'DEMO-2026-0004', 'Demo Student 04', NULL, 'Example'
    UNION ALL SELECT 'DEMO-2026-0005', 'Demo Student 05', NULL, 'Example'
    UNION ALL SELECT 'DEMO-2026-0006', 'Demo Student 06', NULL, 'Example'
    UNION ALL SELECT 'DEMO-2026-0007', 'Demo Student 07', NULL, 'Example'
    UNION ALL SELECT 'DEMO-2026-0008', 'Demo Student 08', NULL, 'Example'
    UNION ALL SELECT 'DEMO-2026-0009', 'Demo Student 09', NULL, 'Example'
    UNION ALL SELECT 'DEMO-2026-0010', 'Demo Student 10', NULL, 'Example'
    UNION ALL SELECT 'DEMO-2026-0011', 'Demo Student 11', NULL, 'Example'
    UNION ALL SELECT 'DEMO-2026-0012', 'Demo Student 12', NULL, 'Example'
) seed
WHERE NOT EXISTS (
    SELECT 1
    FROM students existing
    WHERE existing.school_student_number = seed.school_student_number
       OR (
           existing.first_name = seed.first_name
           AND existing.middle_name <=> seed.middle_name
           AND existing.last_name = seed.last_name
       )
);


-- Adds a fictional guardian record for each demo student.
-- Columns: guardianId, fullName, relationship, contactNumber, email.
-- Auto-generated ID: guardianId.
INSERT INTO guardians (full_name, relationship)
SELECT seed.full_name, 'Parent/Guardian'
FROM (
    SELECT 'Guardian Example 01' AS full_name
    UNION ALL SELECT 'Guardian Example 02'
    UNION ALL SELECT 'Guardian Example 03'
    UNION ALL SELECT 'Guardian Example 04'
    UNION ALL SELECT 'Guardian Example 05'
    UNION ALL SELECT 'Guardian Example 06'
    UNION ALL SELECT 'Guardian Example 07'
    UNION ALL SELECT 'Guardian Example 08'
    UNION ALL SELECT 'Guardian Example 09'
    UNION ALL SELECT 'Guardian Example 10'
    UNION ALL SELECT 'Guardian Example 11'
    UNION ALL SELECT 'Guardian Example 12'
) seed
WHERE NOT EXISTS (
    SELECT 1
    FROM guardians existing
    WHERE existing.full_name = seed.full_name
);

-- Places each demo student in a grade section for the active academic year.
-- Columns: enrollmentId, studentId, sectionId, academicYearId, enrollmentStatus.
-- Auto-generated ID: enrollmentId.
INSERT INTO student_enrollments (student_id, section_id, academic_year_id, enrollment_status)
SELECT student.student_id, section.section_id, section.academic_year_id, 'Active'
FROM (
    SELECT 'DEMO-2026-0001' AS school_student_number, 'Demo Student 01' AS first_name,
           'Grade 7' AS grade_name, 'Section A' AS section_name
    UNION ALL SELECT 'DEMO-2026-0002', 'Demo Student 02', 'Grade 7', 'Section B'
    UNION ALL SELECT 'DEMO-2026-0003', 'Demo Student 03', 'Grade 8', 'Section A'
    UNION ALL SELECT 'DEMO-2026-0004', 'Demo Student 04', 'Grade 8', 'Section B'
    UNION ALL SELECT 'DEMO-2026-0005', 'Demo Student 05', 'Grade 9', 'Section A'
    UNION ALL SELECT 'DEMO-2026-0006', 'Demo Student 06', 'Grade 9', 'Section B'
    UNION ALL SELECT 'DEMO-2026-0007', 'Demo Student 07', 'Grade 10', 'Section A'
    UNION ALL SELECT 'DEMO-2026-0008', 'Demo Student 08', 'Grade 10', 'Section B'
    UNION ALL SELECT 'DEMO-2026-0009', 'Demo Student 09', 'Grade 11', 'Section A'
    UNION ALL SELECT 'DEMO-2026-0010', 'Demo Student 10', 'Grade 11', 'Section B'
    UNION ALL SELECT 'DEMO-2026-0011', 'Demo Student 11', 'Grade 12', 'Section A'
    UNION ALL SELECT 'DEMO-2026-0012', 'Demo Student 12', 'Grade 12', 'Section B'
) seed
JOIN students student
    ON student.school_student_number = seed.school_student_number
   AND student.first_name = seed.first_name
   AND student.middle_name IS NULL
   AND student.last_name = 'Example'
JOIN grade_levels grade
    ON grade.grade_name = seed.grade_name
   AND grade.grade_level_id = (
       SELECT MIN(existing.grade_level_id)
       FROM grade_levels existing
       WHERE existing.grade_name = seed.grade_name
   )
JOIN sections section
    ON section.grade_level_id = grade.grade_level_id
   AND section.section_name = seed.section_name
   AND section.academic_year_id = (
       SELECT active_year.academic_year_id
       FROM academic_years active_year
       WHERE active_year.is_active = TRUE
       ORDER BY active_year.start_date DESC, active_year.academic_year_id DESC
       LIMIT 1
   )
   AND section.section_id = (
       SELECT MIN(existing.section_id)
       FROM sections existing
       WHERE existing.grade_level_id = grade.grade_level_id
         AND existing.section_name = seed.section_name
         AND existing.academic_year_id = section.academic_year_id
   )
WHERE NOT EXISTS (
    SELECT 1
    FROM student_enrollments existing
    WHERE existing.student_id = student.student_id
      AND existing.section_id = section.section_id
      AND existing.academic_year_id = section.academic_year_id
);

-- Links each demo student to their primary guardian and enables notifications.
-- Columns: studentId, guardianId, isPrimaryContact, mayReceiveNotifications.
INSERT INTO student_guardians (student_id, guardian_id, is_primary_contact, may_receive_notifications)
SELECT student.student_id, guardian.guardian_id, TRUE, TRUE
FROM (
    SELECT 'DEMO-2026-0001' AS school_student_number, 'Demo Student 01' AS first_name,
           'Guardian Example 01' AS guardian_name
    UNION ALL SELECT 'DEMO-2026-0002', 'Demo Student 02', 'Guardian Example 02'
    UNION ALL SELECT 'DEMO-2026-0003', 'Demo Student 03', 'Guardian Example 03'
    UNION ALL SELECT 'DEMO-2026-0004', 'Demo Student 04', 'Guardian Example 04'
    UNION ALL SELECT 'DEMO-2026-0005', 'Demo Student 05', 'Guardian Example 05'
    UNION ALL SELECT 'DEMO-2026-0006', 'Demo Student 06', 'Guardian Example 06'
    UNION ALL SELECT 'DEMO-2026-0007', 'Demo Student 07', 'Guardian Example 07'
    UNION ALL SELECT 'DEMO-2026-0008', 'Demo Student 08', 'Guardian Example 08'
    UNION ALL SELECT 'DEMO-2026-0009', 'Demo Student 09', 'Guardian Example 09'
    UNION ALL SELECT 'DEMO-2026-0010', 'Demo Student 10', 'Guardian Example 10'
    UNION ALL SELECT 'DEMO-2026-0011', 'Demo Student 11', 'Guardian Example 11'
    UNION ALL SELECT 'DEMO-2026-0012', 'Demo Student 12', 'Guardian Example 12'
) seed
JOIN students student
    ON student.school_student_number = seed.school_student_number
   AND student.first_name = seed.first_name
   AND student.middle_name IS NULL
   AND student.last_name = 'Example'
JOIN guardians guardian
    ON guardian.full_name = seed.guardian_name
   AND guardian.guardian_id = (
       SELECT MIN(existing.guardian_id)
       FROM guardians existing
       WHERE existing.full_name = seed.guardian_name
   )
WHERE NOT EXISTS (
    SELECT 1
    FROM student_guardians existing
    WHERE existing.student_id = student.student_id
      AND existing.guardian_id = guardian.guardian_id
);


-- Adds example categories for organizing the demo violation types.
-- Columns: categoryId, categoryName, description.
-- Auto-generated ID: categoryId.
INSERT INTO violation_categories (category_name, description)
SELECT seed.category_name, seed.description
FROM (
    SELECT 'Attendance' AS category_name, 'Attendance-related policy examples.' AS description
    UNION ALL SELECT 'Classroom Conduct', 'Classroom conduct policy examples.'
    UNION ALL SELECT 'Property', 'Care of school property policy examples.'
) seed
WHERE NOT EXISTS (
    SELECT 1
    FROM violation_categories existing
    WHERE existing.category_name = seed.category_name
);

-- Adds active example violation types under their matching categories.
-- Columns: violationTypeId, categoryId, violationName, description, isActive, policyReference.
-- Auto-generated ID: violationTypeId.
INSERT INTO violation_types (category_id, violation_name, description, is_active)
SELECT category.category_id, seed.violation_name, seed.description, TRUE
FROM (
    SELECT 'Attendance' AS category_name, 'Unexcused Absence' AS violation_name,
           'Sample attendance violation type.' AS description
    UNION ALL SELECT 'Attendance', 'Repeated Tardiness', 'Sample attendance violation type.'
    UNION ALL SELECT 'Classroom Conduct', 'Classroom Disruption', 'Sample conduct violation type.'
    UNION ALL SELECT 'Classroom Conduct', 'Disrespectful Conduct', 'Sample conduct violation type.'
    UNION ALL SELECT 'Property', 'Damage to School Property', 'Sample property violation type.'
) seed
JOIN violation_categories category ON category.category_name = seed.category_name
   AND category.category_id = (
       SELECT MIN(existing.category_id)
       FROM violation_categories existing
       WHERE existing.category_name = seed.category_name
   )
WHERE NOT EXISTS (
    SELECT 1
    FROM violation_types existing
    WHERE existing.violation_name = seed.violation_name
);
