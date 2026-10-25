CREATE DATABASE IF NOT EXISTS violation_system;
USE violation_system;

CREATE TABLE IF NOT EXISTS roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(150) NOT NULL,
    username VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE IF NOT EXISTS grade_levels (
    grade_level_id INT PRIMARY KEY AUTO_INCREMENT,
    grade_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS sections (
    section_id INT PRIMARY KEY AUTO_INCREMENT,
    section_name VARCHAR(80) NOT NULL,
    grade_level_id INT NOT NULL,
    academic_year_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS academic_years (
    academic_year_id INT PRIMARY KEY AUTO_INCREMENT,
    year_name VARCHAR(50) NOT NULL,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    school_student_number VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    middle_name VARCHAR(80),
    last_name VARCHAR(80) NOT NULL,
    enrollment_status VARCHAR(30) DEFAULT 'Active'
);

CREATE TABLE IF NOT EXISTS student_enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    academic_year_id INT NOT NULL,
    enrollment_status VARCHAR(30) DEFAULT 'Active',
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

CREATE TABLE IF NOT EXISTS guardians (
    guardian_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(150) NOT NULL,
    relationship VARCHAR(50),
    contact_number VARCHAR(50),
    email VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS student_guardians (
    student_id INT NOT NULL,
    guardian_id INT NOT NULL,
    is_primary_contact BOOLEAN DEFAULT FALSE,
    may_receive_notifications BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (student_id, guardian_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (guardian_id) REFERENCES guardians(guardian_id)
);

CREATE TABLE IF NOT EXISTS violation_categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(80) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS violation_types (
    violation_type_id INT PRIMARY KEY AUTO_INCREMENT,
    category_id INT NOT NULL,
    violation_name VARCHAR(150) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    policy_reference VARCHAR(255),
    FOREIGN KEY (category_id) REFERENCES violation_categories(category_id)
);

CREATE TABLE IF NOT EXISTS incident_cases (
    case_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    violation_type_id INT NOT NULL,
    academic_year_id INT NOT NULL,
    incident_date DATE NOT NULL,
    incident_location VARCHAR(255),
    case_status VARCHAR(50) DEFAULT 'Pending Review',
    reviewed_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (violation_type_id) REFERENCES violation_types(violation_type_id),
    FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS violation_reports (
    report_id INT PRIMARY KEY AUTO_INCREMENT,
    case_id INT NOT NULL,
    reported_by INT NOT NULL,
    violation_type_id INT NOT NULL,
    incident_description TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    report_status VARCHAR(50) DEFAULT 'Pending Review',
    FOREIGN KEY (case_id) REFERENCES incident_cases(case_id),
    FOREIGN KEY (reported_by) REFERENCES users(user_id),
    FOREIGN KEY (violation_type_id) REFERENCES violation_types(violation_type_id)
);

CREATE TABLE IF NOT EXISTS disciplinary_actions (
    action_id INT PRIMARY KEY AUTO_INCREMENT,
    case_id INT NOT NULL,
    action_type VARCHAR(80) NOT NULL,
    decision_notes TEXT,
    decided_by INT NOT NULL,
    decision_date DATE,
    implementation_status VARCHAR(50) DEFAULT 'Pending',
    FOREIGN KEY (case_id) REFERENCES incident_cases(case_id),
    FOREIGN KEY (decided_by) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS guardian_notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    case_id INT NOT NULL,
    guardian_id INT NOT NULL,
    notification_type VARCHAR(80),
    message TEXT,
    sent_at TIMESTAMP,
    delivery_status VARCHAR(50) DEFAULT 'Pending',
    FOREIGN KEY (case_id) REFERENCES incident_cases(case_id),
    FOREIGN KEY (guardian_id) REFERENCES guardians(guardian_id)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    audit_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action VARCHAR(120) NOT NULL,
    entity_type VARCHAR(80),
    entity_id INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
