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
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE IF NOT EXISTS grade_levels (
    grade_level_id INT PRIMARY KEY AUTO_INCREMENT,
    grade_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS academic_years (
    academic_year_id INT PRIMARY KEY AUTO_INCREMENT,
    year_name VARCHAR(50) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_academic_year_dates CHECK (end_date >= start_date)
);

CREATE TABLE IF NOT EXISTS sections (
    section_id INT PRIMARY KEY AUTO_INCREMENT,
    section_name VARCHAR(80) NOT NULL,
    grade_level_id INT NOT NULL,
    academic_year_id INT NOT NULL,
    UNIQUE (section_name, grade_level_id, academic_year_id),
    FOREIGN KEY (grade_level_id) REFERENCES grade_levels(grade_level_id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id)
);

CREATE TABLE IF NOT EXISTS students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    school_student_number VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    middle_name VARCHAR(80),
    last_name VARCHAR(80) NOT NULL,
    enrollment_status VARCHAR(30) NOT NULL DEFAULT 'Active'
);

CREATE TABLE IF NOT EXISTS student_enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    academic_year_id INT NOT NULL,
    enrollment_status VARCHAR(30) NOT NULL DEFAULT 'Active',
    UNIQUE (student_id, section_id, academic_year_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (section_id) REFERENCES sections(section_id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id)
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
    is_primary_contact BOOLEAN NOT NULL DEFAULT FALSE,
    may_receive_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (student_id, guardian_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (guardian_id) REFERENCES guardians(guardian_id)
);

CREATE TABLE IF NOT EXISTS violation_categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(30) NOT NULL UNIQUE,
    description TEXT,
    CONSTRAINT chk_violation_category_name CHECK (
        category_name IN ('Minor', 'Moderate', 'Major')
    )
);

CREATE TABLE IF NOT EXISTS handbook_sections (
    handbook_section_id INT PRIMARY KEY AUTO_INCREMENT,
    category_id INT NOT NULL,
    section_name VARCHAR(120) NOT NULL,
    description TEXT,
    UNIQUE (category_id, section_name),
    FOREIGN KEY (category_id) REFERENCES violation_categories(category_id),
    INDEX idx_handbook_sections_category (category_id, section_name)
);

CREATE TABLE IF NOT EXISTS violation_types (
    violation_type_id INT PRIMARY KEY AUTO_INCREMENT,
    handbook_section_id INT NOT NULL,
    violation_name VARCHAR(150) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    policy_reference VARCHAR(255),
    UNIQUE (handbook_section_id, violation_name),
    FOREIGN KEY (handbook_section_id) REFERENCES handbook_sections(handbook_section_id),
    INDEX idx_violation_types_section_active (handbook_section_id, is_active)
);

CREATE TABLE IF NOT EXISTS incident_cases (
    case_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    violation_type_id INT NOT NULL,
    academic_year_id INT NOT NULL,
    incident_date DATE NOT NULL,
    incident_time TIME,
    incident_location VARCHAR(255),
    case_status VARCHAR(30) NOT NULL DEFAULT 'PENDING_REVIEW',
    review_outcome VARCHAR(30) NOT NULL DEFAULT 'UNRESOLVED',
    confirmed_classification VARCHAR(20),
    reviewed_by INT,
    reviewed_at DATETIME,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_case_status CHECK (
        case_status IN ('PENDING_REVIEW', 'UNDER_REVIEW', 'AWAITING_INFORMATION', 'CLOSED')
    ),
    CONSTRAINT chk_case_review_outcome CHECK (
        review_outcome IN ('UNRESOLVED', 'VIOLATION_ESTABLISHED',
                           'NOT_ESTABLISHED', 'FURTHER_REVIEW_REQUIRED')
    ),
    CONSTRAINT chk_case_confirmed_classification CHECK (
        confirmed_classification IS NULL
        OR confirmed_classification IN ('Minor', 'Moderate', 'Major', 'Grave')
    ),
    CONSTRAINT chk_case_classification_matches_outcome CHECK (
        (review_outcome = 'VIOLATION_ESTABLISHED' AND confirmed_classification IS NOT NULL)
        OR (review_outcome <> 'VIOLATION_ESTABLISHED' AND confirmed_classification IS NULL)
    ),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (violation_type_id) REFERENCES violation_types(violation_type_id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id),
    FOREIGN KEY (reviewed_by) REFERENCES users(user_id),
    INDEX idx_incident_student_year_outcome (student_id, academic_year_id, review_outcome),
    INDEX idx_incident_violation_year_outcome (violation_type_id, academic_year_id, review_outcome)
);

CREATE TABLE IF NOT EXISTS violation_reports (
    report_id INT PRIMARY KEY AUTO_INCREMENT,
    case_id INT NOT NULL,
    reported_by INT NOT NULL,
    incident_description TEXT NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    report_status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    CONSTRAINT chk_report_status CHECK (
        report_status IN ('SUBMITTED', 'UNDER_REVIEW', 'NEEDS_INFORMATION', 'WITHDRAWN')
    ),
    FOREIGN KEY (case_id) REFERENCES incident_cases(case_id),
    FOREIGN KEY (reported_by) REFERENCES users(user_id),
    INDEX idx_report_case_submitted (case_id, submitted_at),
    INDEX idx_report_reporter (reported_by, submitted_at)
);

CREATE TABLE IF NOT EXISTS penalty_types (
    penalty_type_id INT PRIMARY KEY AUTO_INCREMENT,
    penalty_name VARCHAR(100) NOT NULL UNIQUE,
    measure_kind VARCHAR(20) NOT NULL DEFAULT 'PENALTY',
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_penalty_measure_kind CHECK (
        measure_kind IN ('PENALTY', 'PROCEDURE', 'ADMINISTRATIVE')
    )
);

CREATE TABLE IF NOT EXISTS penalty_rules (
    penalty_rule_id INT PRIMARY KEY AUTO_INCREMENT,
    rule_code VARCHAR(50) NOT NULL UNIQUE,
    rule_name VARCHAR(150) NOT NULL,
    policy_version VARCHAR(80) NOT NULL,
    penalty_type_id INT NOT NULL,
    effective_from DATE,
    effective_until DATE,
    rule_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    interpretation_notes TEXT,
    CONSTRAINT chk_penalty_rule_status CHECK (
        rule_status IN ('DRAFT', 'ACTIVE', 'RETIRED')
    ),
    CONSTRAINT chk_penalty_rule_dates CHECK (
        effective_until IS NULL OR effective_from IS NULL OR effective_until >= effective_from
    ),
    FOREIGN KEY (penalty_type_id) REFERENCES penalty_types(penalty_type_id)
);

CREATE TABLE IF NOT EXISTS penalty_rule_condition_groups (
    condition_group_id INT PRIMARY KEY AUTO_INCREMENT,
    penalty_rule_id INT NOT NULL,
    group_name VARCHAR(100),
    FOREIGN KEY (penalty_rule_id) REFERENCES penalty_rules(penalty_rule_id),
    INDEX idx_rule_condition_groups_rule (penalty_rule_id)
);

-- Conditions in a group are combined with AND; groups of one rule are alternatives (OR).
CREATE TABLE IF NOT EXISTS penalty_rule_conditions (
    condition_id INT PRIMARY KEY AUTO_INCREMENT,
    condition_group_id INT NOT NULL,
    measure VARCHAR(40) NOT NULL,
    comparison_operator VARCHAR(5) NOT NULL,
    threshold_value INT NOT NULL,
    severity_category_id INT,
    CONSTRAINT chk_rule_condition_measure CHECK (
        measure IN ('MINOR_CASE_COUNT', 'MODERATE_CASE_COUNT', 'MAJOR_CASE_COUNT',
                    'GRAVE_CASE_COUNT', 'SAME_TYPE_CASE_COUNT', 'CATEGORY_CASE_COUNT',
                    'SUSPENSION_SET_COUNT', 'GRADING_PERIOD')
    ),
    CONSTRAINT chk_rule_condition_operator CHECK (
        comparison_operator IN ('=', '>', '>=', '<', '<=')
    ),
    CONSTRAINT chk_rule_condition_threshold CHECK (threshold_value >= 0),
    CONSTRAINT chk_rule_condition_category CHECK (
        (measure = 'CATEGORY_CASE_COUNT' AND severity_category_id IS NOT NULL)
        OR (measure <> 'CATEGORY_CASE_COUNT' AND severity_category_id IS NULL)
    ),
    FOREIGN KEY (condition_group_id)
        REFERENCES penalty_rule_condition_groups(condition_group_id),
    FOREIGN KEY (severity_category_id) REFERENCES violation_categories(category_id),
    INDEX idx_rule_conditions_group (condition_group_id)
);

CREATE TABLE IF NOT EXISTS penalty_rule_evaluations (
    evaluation_id INT PRIMARY KEY AUTO_INCREMENT,
    case_id INT NOT NULL,
    penalty_rule_id INT NOT NULL,
    evaluated_by INT NOT NULL,
    evaluated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    evaluation_result VARCHAR(20) NOT NULL,
    count_snapshot JSON NOT NULL,
    notes TEXT,
    CONSTRAINT chk_rule_evaluation_result CHECK (
        evaluation_result IN ('MATCHED', 'NOT_MATCHED', 'NEEDS_REVIEW')
    ),
    FOREIGN KEY (case_id) REFERENCES incident_cases(case_id),
    FOREIGN KEY (penalty_rule_id) REFERENCES penalty_rules(penalty_rule_id),
    FOREIGN KEY (evaluated_by) REFERENCES users(user_id),
    INDEX idx_rule_evaluations_case (case_id, evaluated_at)
);

CREATE TABLE IF NOT EXISTS disciplinary_actions (
    action_id INT PRIMARY KEY AUTO_INCREMENT,
    case_id INT NOT NULL,
    penalty_type_id INT,
    action_outcome VARCHAR(20) NOT NULL,
    decision_notes TEXT,
    decided_by INT NOT NULL,
    decided_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    implementation_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT chk_disciplinary_action_outcome CHECK (
        (action_outcome = 'MEASURE_IMPOSED' AND penalty_type_id IS NOT NULL)
        OR (action_outcome = 'NO_PENALTY' AND penalty_type_id IS NULL)
    ),
    CONSTRAINT chk_disciplinary_implementation_status CHECK (
        implementation_status IN ('NOT_APPLICABLE', 'PENDING', 'SCHEDULED',
                                  'IN_PROGRESS', 'COMPLETED', 'NOT_IMPLEMENTED')
    ),
    FOREIGN KEY (case_id) REFERENCES incident_cases(case_id),
    FOREIGN KEY (penalty_type_id) REFERENCES penalty_types(penalty_type_id),
    FOREIGN KEY (decided_by) REFERENCES users(user_id),
    INDEX idx_disciplinary_case_date (case_id, decided_at)
);

CREATE TABLE IF NOT EXISTS suspension_details (
    action_id INT PRIMARY KEY,
    duration_days SMALLINT UNSIGNED NOT NULL,
    academic_year_id INT NOT NULL,
    imposed_at DATETIME NOT NULL,
    grading_period SMALLINT UNSIGNED,
    service_mode VARCHAR(15) NOT NULL,
    service_status VARCHAR(20) NOT NULL DEFAULT 'NOT_SCHEDULED',
    CONSTRAINT chk_suspension_duration CHECK (duration_days > 0),
    CONSTRAINT chk_suspension_grading_period CHECK (
        grading_period IS NULL OR grading_period > 0
    ),
    CONSTRAINT chk_suspension_service_mode CHECK (
        service_mode IN ('CONTINUOUS', 'STAGGERED')
    ),
    CONSTRAINT chk_suspension_service_status CHECK (
        service_status IN ('NOT_SCHEDULED', 'SCHEDULED', 'IN_PROGRESS',
                           'COMPLETED', 'NOT_SERVED')
    ),
    FOREIGN KEY (action_id) REFERENCES disciplinary_actions(action_id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id),
    INDEX idx_suspension_year_imposed (academic_year_id, imposed_at)
);

CREATE TABLE IF NOT EXISTS suspension_service_periods (
    service_period_id INT PRIMARY KEY AUTO_INCREMENT,
    action_id INT NOT NULL,
    service_start_date DATE NOT NULL,
    service_end_date DATE NOT NULL,
    service_status VARCHAR(15) NOT NULL DEFAULT 'SCHEDULED',
    CONSTRAINT chk_suspension_service_dates CHECK (service_end_date >= service_start_date),
    CONSTRAINT chk_suspension_period_status CHECK (
        service_status IN ('SCHEDULED', 'SERVED', 'MISSED', 'RESCHEDULED')
    ),
    FOREIGN KEY (action_id) REFERENCES suspension_details(action_id),
    INDEX idx_suspension_period_action (action_id, service_start_date)
);

CREATE TABLE IF NOT EXISTS guardian_notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    case_id INT NOT NULL,
    guardian_id INT NOT NULL,
    notification_type VARCHAR(80),
    message TEXT,
    attempted_at TIMESTAMP NULL,
    sent_at TIMESTAMP NULL,
    delivery_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    delivery_error TEXT,
    FOREIGN KEY (case_id) REFERENCES incident_cases(case_id),
    FOREIGN KEY (guardian_id) REFERENCES guardians(guardian_id),
    INDEX idx_guardian_notification_case (case_id),
    INDEX idx_guardian_notification_guardian (guardian_id, delivery_status)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    audit_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action VARCHAR(120) NOT NULL,
    entity_type VARCHAR(80),
    entity_id INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details JSON,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_audit_entity (entity_type, entity_id, created_at),
    INDEX idx_audit_user (user_id, created_at)
);
