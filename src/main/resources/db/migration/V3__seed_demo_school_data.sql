-- Fictional sample records for development and demonstration; contact details are left blank.

DELIMITER //

DROP PROCEDURE IF EXISTS temp_seed_mock_data//

CREATE PROCEDURE temp_seed_mock_data()
BEGIN
    DECLARE v_academic_year_id INT;
    DECLARE v_section_id INT;
    DECLARE v_student_count INT;
    DECLARE v_counter INT DEFAULT 1;
    DECLARE v_fn VARCHAR(50);
    DECLARE v_mn VARCHAR(50);
    DECLARE v_ln VARCHAR(50);
    DECLARE v_guardian_fn VARCHAR(50);
    DECLARE v_guardian_mn VARCHAR(50);
    DECLARE v_guardian_names_valid BOOLEAN DEFAULT FALSE;
    DECLARE v_guardian_name VARCHAR(100);
    DECLARE v_email VARCHAR(150);
    DECLARE v_contact VARCHAR(50);
    DECLARE v_student_num VARCHAR(50);
    DECLARE v_student_id INT;
    DECLARE v_guardian_id INT;
    DECLARE v_name_inserted INT DEFAULT 0;
    DECLARE v_name_attempts INT DEFAULT 0;
    DECLARE v_email_suffix INT;
    DECLARE v_contact_suffix INT;
    DECLARE v_existing_contact_count INT;
    DECLARE s_idx INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        DROP TEMPORARY TABLE IF EXISTS temp_first_names;
        DROP TEMPORARY TABLE IF EXISTS temp_middle_names;
        DROP TEMPORARY TABLE IF EXISTS temp_last_names;
        DROP TEMPORARY TABLE IF EXISTS temp_generated_student_names;
        RESIGNAL;
    END;

    -- Create or find the target school year, then make only that row active.
    -- Columns: academicYearId, yearName, startDate, endDate, isActive.
    -- Auto-generated ID: academicYearId.
    INSERT INTO academic_years (year_name, start_date, end_date, is_active)
    SELECT '2026-2027', '2026-06-01', '2027-03-31', FALSE
    WHERE NOT EXISTS (
        SELECT 1
        FROM academic_years
        WHERE year_name = '2026-2027'
          AND start_date = '2026-06-01'
    );

    SELECT academic_year_id
    INTO v_academic_year_id
    FROM academic_years
    WHERE year_name = '2026-2027'
      AND start_date = '2026-06-01'
    ORDER BY academic_year_id
    LIMIT 1;

    UPDATE academic_years
    SET is_active = (academic_year_id = v_academic_year_id);

    -- Continue demo student numbering after any existing STU-2026 records.
    SELECT COALESCE(
        MAX(CAST(SUBSTRING_INDEX(school_student_number, '-', -1) AS UNSIGNED)),
        0
    ) + 1
    INTO v_counter
    FROM students
    WHERE school_student_number LIKE 'STU-2026-%';

    -- Add the grades needed by the generated sections.
    -- Columns: gradeLevelId, gradeName.
    -- Auto-generated ID: gradeLevelId.
    INSERT INTO grade_levels (grade_name)
    SELECT seed.grade_name
    FROM (
        SELECT 'Grade 9' AS grade_name
        UNION ALL SELECT 'Grade 10'
        UNION ALL SELECT 'Grade 11'
        UNION ALL SELECT 'Grade 12'
    ) seed
    WHERE NOT EXISTS (
        SELECT 1
        FROM grade_levels existing
        WHERE existing.grade_name = seed.grade_name
    );

    -- Add three named sections for each grade in the target year.
    -- Columns: sectionId, sectionName, gradeLevelId, academicYearId.
    -- Auto-generated ID: sectionId.
    INSERT INTO sections (section_name, grade_level_id, academic_year_id)
    SELECT seed.section_name, grade.grade_level_id, v_academic_year_id
    FROM (
        SELECT 'Grade 9' AS grade_name, 'Section Bonifacio' AS section_name
        UNION ALL SELECT 'Grade 9', 'Section Rizal'
        UNION ALL SELECT 'Grade 9', 'Section Mabini'
        UNION ALL SELECT 'Grade 10', 'Section Emerald'
        UNION ALL SELECT 'Grade 10', 'Section Ruby'
        UNION ALL SELECT 'Grade 10', 'Section Sapphire'
        UNION ALL SELECT 'Grade 11', 'Section Faraday'
        UNION ALL SELECT 'Grade 11', 'Section Newton'
        UNION ALL SELECT 'Grade 11', 'Section Mendel'
        UNION ALL SELECT 'Grade 12', 'Section Tesla'
        UNION ALL SELECT 'Grade 12', 'Section Edison'
        UNION ALL SELECT 'Grade 12', 'Section Curie'
    ) seed
    JOIN grade_levels grade ON grade.grade_name = seed.grade_name
    WHERE NOT EXISTS (
        SELECT 1
        FROM sections existing
        WHERE existing.section_name = seed.section_name
          AND existing.grade_level_id = grade.grade_level_id
          AND existing.academic_year_id = v_academic_year_id
    );

    -- Name pools are temporary and are removed on success or error.
    CREATE TEMPORARY TABLE temp_first_names (name VARCHAR(50));
    -- Columns: name. The 100 supplied first names are unique within this pool.
    INSERT INTO temp_first_names (name) VALUES
        ('Aaron'), ('Abigail'), ('Adam'), ('Adrian'), ('Ainsley'), ('Alaric'), ('Albert'), ('Alexander'),
        ('Alexis'), ('Alfred'), ('Alice'), ('Alicia'), ('Alistair'), ('Amara'), ('Ambrose'), ('Amelia'),
        ('Amos'), ('Amy'), ('Anastas'), ('Anatole'), ('Anemone'), ('Angela'), ('Angus'), ('Annalise'),
        ('Anthony'), ('Antoinette'), ('Apollo'), ('Archibald'), ('Ariadne'), ('Arlo'), ('Armand'), ('Arthur'),
        ('Astrid'), ('Audrey'), ('Augustine'), ('Aurelia'), ('Austin'), ('Ava'), ('Avery'), ('Axel'),
        ('Beatrice'), ('Bellatrix'), ('Benedict'), ('Benjamin'), ('Bernice'), ('Bertram'), ('Bianca'), ('Blair'),
        ('Blake'), ('Boris'), ('Bradley'), ('Bridget'), ('Caden'), ('Caleb'), ('Callum'), ('Calvin'),
        ('Camilla'), ('Candice'), ('Cassandra'), ('Cedric'), ('Celeste'), ('Charles'), ('Charlotte'), ('Chloe'),
        ('Christian'), ('Clara'), ('Clarence'), ('Clementine'), ('Clifford'), ('Clive'), ('Cora'), ('Cordelia'),
        ('Corinne'), ('Damian'), ('Daniel'), ('Daphne'), ('Darius'), ('David'), ('Deborah'), ('Delilah'),
        ('Desmond'), ('Diana'), ('Dominic'), ('Doris'), ('Douglas'), ('Duncan'), ('Dylan'), ('Ebenezer'),
        ('Edith'), ('Edmund'), ('Edward'), ('Eleanor'), ('Eli'), ('Eliana'), ('Elias'), ('Elijah'),
        ('Eliza'), ('Elizabeth'), ('Ella'), ('Elliot'), ('Ellis'), ('Elmer'), ('Eloise'), ('Elsie'),
        ('Emery'), ('Emilia'), ('Emily'), ('Emma'), ('Emmanuel'), ('Emmeline'), ('Eric'), ('Ernest'),
        ('Esme'), ('Ethan'), ('Eugene'), ('Eva'), ('Evander'), ('Evelyn'), ('Ezekiel'), ('Ezra'),
        ('Faith'), ('Felicity'), ('Fiona'), ('Fletcher'), ('Flora'), ('Florence'), ('Flynn'), ('Francesca'),
        ('Francis'), ('Frederick'), ('Gabriel'), ('Gemma'), ('Genevieve'), ('Geoffrey'), ('George'), ('Georgia'),
        ('Gerald'), ('Gideon'), ('Gilbert'), ('Giselle'), ('Gladys'), ('Glen'), ('Gloria'), ('Grace'),
        ('Graham'), ('Gregory'), ('Gwendolyn'), ('Hadley'), ('Hannah'), ('Harriet'), ('Harrison'), ('Harry'),
        ('Harvey'), ('Hazel'), ('Heath'), ('Heather'), ('Helen'), ('Helena'), ('Henry'), ('Herbert'),
        ('Hester'), ('Hezekiah'), ('Hilary'), ('Hilda'), ('Holly'), ('Homer'), ('Hope'), ('Horace'),
        ('Howard'), ('Hubert'), ('Hugh'), ('Hugo'), ('Ian'), ('Ida'), ('Imogen'), ('Ingrid'),
        ('Irene'), ('Iris'), ('Irvin'), ('Isaac'), ('Isabel'), ('Isaias'), ('Isolde'), ('Ivan'),
        ('Ivy'), ('Jack'), ('Jacob'), ('Jacqueline'), ('Jade'), ('James'), ('Janice'), ('Jasper'),
        ('Jayden'), ('Jean'), ('Jeremiah'), ('Jeremy'), ('Jerome'), ('Jessica'), ('Jill'), ('Joan'),
        ('Jocelyn'), ('Joel'), ('John'), ('Jonah'), ('Jonathan'), ('Jordan'), ('Joseph'), ('Joshua');


    CREATE TEMPORARY TABLE temp_middle_names (name VARCHAR(50));
    -- Columns: name. The 100 supplied middle names are unique within this pool.
    INSERT INTO temp_middle_names (name) VALUES
        ('Abner'), ('Abraham'), ('Alan'), ('Alder'), ('Alex'), ('Amos'), ('Archambault'), ('Baldwin'),
        ('Barnaby'), ('Barry'), ('Bennett'), ('Bernard'), ('Bill'), ('Brad'), ('Brian'), ('Bruce'),
        ('Carl'), ('Cassian'), ('Chad'), ('Colin'), ('Conrad'), ('Craig'), ('Dale'), ('Dan'),
        ('Dave'), ('Dean'), ('Demetrius'), ('Dennis'), ('Dexter'), ('Donovan'), ('Dorian'), ('Earl'),
        ('East'), ('Edgar'), ('Emmanuel'), ('Evander'), ('Fabian'), ('Felix'), ('Finnegan'), ('Floyd'),
        ('Fox'), ('Frank'), ('Fred'), ('Gary'), ('Gavin'), ('Gene'), ('Giles'), ('Gordon'),
        ('Guy'), ('Hank'), ('Hans'), ('Harold'), ('Hezekiah'), ('High'), ('Ignatius'), ('Isadore'),
        ('Jason'), ('Julian'), ('Justin'), ('Kaelen'), ('Karl'), ('Keith'), ('Kenneth'), ('Kent'),
        ('Kirk'), ('Kurt'), ('Kurtis'), ('Lance'), ('Lazarus'), ('Leander'), ('Leo'), ('Leonard'),
        ('Linus'), ('Louis'), ('Luke'), ('Lyle'), ('Lysander'), ('Malachi'), ('Marc'), ('Marcellus'),
        ('Mark'), ('Methodius'), ('Morgan'), ('Nathaniel'), ('Nehemiah'), ('Neil'), ('Nicholas'), ('Nikolas'),
        ('Noel'), ('Nolan'), ('Norbert'), ('Oberon'), ('Octavius'), ('Oliver'), ('Orson'), ('Oscar'),
        ('Oswald'), ('Otto'), ('Owen'), ('Patrick'), ('Paul'), ('Percival'), ('Peter'), ('Phineas'),
        ('Philip'), ('Quentin'), ('Quincy'), ('Quintin'), ('Quintus'), ('Ralph'), ('Raphael'), ('Reuben'),
        ('Rex'), ('Richard'), ('Robert'), ('Roderick'), ('Ross'), ('Roy'), ('Rylan'), ('Sam'),
        ('Samuel'), ('Saul'), ('Sean'), ('Seth'), ('Silas'), ('Simon'), ('Soren'), ('Stephen'),
        ('Sylvester'), ('Thaddeus'), ('Thomas'), ('Timothy'), ('Titus'), ('Todd'), ('Tom'), ('Trent'),
        ('Trevor'), ('Tristan'), ('Troy'), ('Ulisse'), ('Ulysses'), ('Uriah'), ('Uriel'), ('Valentine'),
        ('Valerius'), ('Vance'), ('Victor'), ('Vincent'), ('Wallace'), ('Walter'), ('Warren'), ('Wayne'),
        ('Will'), ('William'), ('Xander'), ('Xavier'), ('Yael'), ('Yves'), ('Zachariah'), ('Zacharias'),
        ('Zachary'), ('Zander'), ('Zephyr'), ('Aiden'), ('Alvin'), ('Asher'), ('Austin'), ('Barrett'),
        ('Benton'), ('Boyd'), ('Brant'), ('Brody'), ('Brooks'), ('Bryan'), ('Bryce'), ('Byron'),
        ('Calvin'), ('Camden'), ('Carson'), ('Carter'), ('Chance'), ('Chase'), ('Clark'), ('Clay'),
        ('Clayton'), ('Cody'), ('Colby'), ('Cole'), ('Colton'), ('Connor'), ('Corbin'), ('Corey');

    CREATE TEMPORARY TABLE temp_last_names (name VARCHAR(50));
    -- Columns: name. The 100 supplied last names are unique within this pool.
    INSERT INTO temp_last_names (name) VALUES
        ('Abernathy'), ('Aldercliff'), ('Alderdale'), ('Alderwood'), ('Applebrook'), ('Archwood'), ('Ashbrook'), ('Ashworth'),
        ('Avenport'), ('Beauchamp'), ('Beechwood'), ('Birchwood'), ('Blackthorn'), ('Blackwood'), ('Bramblewood'), ('Briarcliff'),
        ('Briarcrest'), ('Briardale'), ('Briarwood'), ('Brightwater'), ('Broadwing'), ('Carmichael'), ('Cedarbrook'), ('Cedarwood'),
        ('Chesterfield'), ('Claybrook'), ('Copperfield'), ('Crestdale'), ('Crestland'), ('Crestview'), ('Crestwood'), ('Dalebrook'),
        ('Davenport'), ('Deepcrest'), ('Deepdale'), ('Deepvale'), ('Deepwood'), ('Driftwood'), ('Dunsmore'), ('Eastcrest'),
        ('Eastdale'), ('Eastford'), ('Eastwood'), ('Elmwood'), ('Fairweather'), ('Falconbridge'), ('Featherstone'), ('Fernwood'),
        ('Fieldwood'), ('Fitzpatrick'), ('Foxcrest'), ('Foxdale'), ('Foxhollow'), ('Foxwood'), ('Glenbrook'), ('Glenwood'),
        ('Goldsworth'), ('Greenwood'), ('Hallowell'), ('Harrington'), ('Hazelwood'), ('Highcrest'), ('Highland'), ('Highwood'),
        ('Hightower'), ('Hollobrook'), ('Hollingsworth'), ('Inglewood'), ('Ironbrook'), ('Ironcliff'), ('Irondale'), ('Ironland'),
        ('Ironridge'), ('Ironwood'), ('Ivybrook'), ('Jenkins'), ('Kensington'), ('Kingsbridge'), ('Kingsbury'), ('Kingsdale'),
        ('Kingsford'), ('Kingsland'), ('Kingswood'), ('Lakebrook'), ('Lakedale'), ('Lakeland'), ('Lakeview'), ('Lakewood'),
        ('Laurelwood'), ('Leadbeater'), ('Lockridge'), ('MacAllister'), ('Maplewood'), ('Meadowwood'), ('Millcrest'), ('Milldale'),
        ('Millfield'), ('Millwood'), ('Montgomery'), ('Mossbrook'), ('Nethercott'), ('Nightengale'), ('Northbrook'), ('Northcliff'),
        ('Northdale'), ('Northland'), ('Northwood'), ('Oakbrook'), ('Oakcliff'), ('Oakdale'), ('Oakhaven'), ('Oakland'),
        ('Oakridge'), ('Oakwood'), ('Parkbrook'), ('Pemberton'), ('Pendelton'), ('Pinebrook'), ('Pinecrest'), ('Pinedale'),
        ('Pineland'), ('Pinewood'), ('Quigley'), ('Quimby'), ('Radcliffe'), ('Ravenscroft'), ('Redford'), ('Redwood'),
        ('Ridgeborne'), ('Riverbend'), ('Riverbrook'), ('Riverdale'), ('Riverland'), ('Riverwood'), ('Rosewood'), ('Seabrook'),
        ('Shadowbrook'), ('Shadowdale'), ('Shadowland'), ('Shadowridge'), ('Shadowwood'), ('Silverstein'), ('Springwood'), ('Standish'),
        ('Sterling'), ('Sterlingford'), ('Stonebrook'), ('Stonecliff'), ('Stonedale'), ('Stonefield'), ('Stonehaven'), ('Stoneland'),
        ('Stonecrest'), ('Summerbee'), ('Sunbrook'), ('Swiftwood'), ('Thornbrook'), ('Thornfield'), ('Timberbrook'), ('Timbercliff'),
        ('Timberdale'), ('Timberland'), ('Timberridge'), ('Upshaw'), ('Uxbridge'), ('Valecrest'), ('Valedale'), ('Valeland'),
        ('Valenti'), ('Valewood'), ('Vinebrook'), ('Westcliff'), ('Westdale'), ('Westland'), ('Westridge'), ('Westwood'),
        ('Whitewater'), ('Whitmore'), ('Wildwood'), ('Windermere'), ('Winterbourne'), ('Winterbrook'), ('Winterfield'), ('Winterfrost'),
        ('Winterhart'), ('Alderhaven'), ('Alderstone'), ('Aldertown'), ('Alderwater'), ('Applewood'), ('Ashcliff'), ('Ashdale'),
        ('Ashfield'), ('Ashford'), ('Ashridge'), ('Ashvale'), ('Aspenwood'), ('Avencliff'), ('Avendale'), ('Avenfield');

    CREATE TEMPORARY TABLE temp_generated_student_names (
        first_name VARCHAR(80) NOT NULL,
        middle_name VARCHAR(80) NOT NULL,
        last_name VARCHAR(80) NOT NULL,
        PRIMARY KEY (first_name, middle_name, last_name)
    );
    INSERT IGNORE INTO temp_generated_student_names (first_name, middle_name, last_name)
    SELECT first_name, COALESCE(middle_name, ''), last_name
    FROM students;

    -- Populate only these twelve sections; existing V3 demo sections are unaffected.
    IF NOT EXISTS (
        SELECT 1
        FROM student_enrollments enrollment
        JOIN sections section ON section.section_id = enrollment.section_id
        WHERE section.academic_year_id = v_academic_year_id
          AND section.section_name IN (
              'Section Bonifacio', 'Section Rizal', 'Section Mabini',
              'Section Emerald', 'Section Ruby', 'Section Sapphire',
              'Section Faraday', 'Section Newton', 'Section Mendel',
              'Section Tesla', 'Section Edison', 'Section Curie'
          )
    ) THEN
        BEGIN
            DECLARE done BOOLEAN DEFAULT FALSE;
            DECLARE section_cursor CURSOR FOR
                SELECT section_id
                FROM sections
                WHERE academic_year_id = v_academic_year_id
                  AND section_name IN (
                      'Section Bonifacio', 'Section Rizal', 'Section Mabini',
                      'Section Emerald', 'Section Ruby', 'Section Sapphire',
                      'Section Faraday', 'Section Newton', 'Section Mendel',
                      'Section Tesla', 'Section Edison', 'Section Curie'
                  );
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

            OPEN section_cursor;

            read_loop: LOOP
                FETCH section_cursor INTO v_section_id;
                IF done THEN
                    LEAVE read_loop;
                END IF;

                SET v_student_count = FLOOR(RAND() * 11 + 28);
                SET s_idx = 1;

                WHILE s_idx <= v_student_count DO
                    SET v_name_inserted = 0;
                    SET v_name_attempts = 0;
                    WHILE v_name_inserted = 0 DO
                        SELECT name INTO v_fn
                        FROM temp_first_names
                        ORDER BY RAND()
                        LIMIT 1;

                        SELECT name INTO v_mn
                        FROM temp_middle_names
                        ORDER BY RAND()
                        LIMIT 1;

                        SELECT name INTO v_ln
                        FROM temp_last_names
                        ORDER BY RAND()
                        LIMIT 1;

                        INSERT IGNORE INTO temp_generated_student_names (
                            first_name, middle_name, last_name
                        )
                        VALUES (v_fn, v_mn, v_ln);
                        SET v_name_inserted = ROW_COUNT();
                        IF v_name_inserted = 0 THEN
                            SET v_name_attempts = v_name_attempts + 1;
                            IF v_name_attempts >= 10000 THEN
                                SIGNAL SQLSTATE '45000'
                                    SET MESSAGE_TEXT = 'Could not find a unique demo student name after 10000 attempts.';
                            END IF;
                        END IF;
                    END WHILE;

                    SET v_student_num = CONCAT('STU-2026-', LPAD(v_counter, 5, '0'));
                    SET v_counter = v_counter + 1;

                    SET v_guardian_names_valid = FALSE;
                    WHILE NOT v_guardian_names_valid DO
                        SELECT name INTO v_guardian_fn
                        FROM temp_first_names
                        ORDER BY RAND()
                        LIMIT 1;

                        SELECT name INTO v_guardian_mn
                        FROM temp_middle_names
                        ORDER BY RAND()
                        LIMIT 1;

                        IF v_guardian_fn <> v_fn
                           AND v_guardian_fn <> v_mn
                           AND v_guardian_mn <> v_fn
                           AND v_guardian_mn <> v_mn
                           AND v_guardian_mn <> v_guardian_fn THEN
                            SET v_guardian_names_valid = TRUE;
                        END IF;
                    END WHILE;

                    SET v_guardian_name = CONCAT(
                        v_guardian_fn, ' ', v_guardian_mn, ' ', v_ln
                    );

                    SET v_email_suffix = 1;
                    email_loop: LOOP
                        SET v_email = CONCAT(
                            LOWER(v_guardian_fn), '.', LOWER(v_ln), '.',
                            LPAD(v_email_suffix, 5, '0'), '@example.invalid'
                        );
                        SELECT COUNT(*) INTO v_existing_contact_count
                        FROM guardians
                        WHERE email = v_email;
                        IF v_existing_contact_count = 0 THEN
                            LEAVE email_loop;
                        END IF;
                        SET v_email_suffix = v_email_suffix + 1;
                    END LOOP;

                    SET v_contact_suffix = 1;
                    contact_loop: LOOP
                        SET v_contact = CONCAT('TEST-PHONE-', LPAD(v_contact_suffix, 6, '0'));
                        SELECT COUNT(*) INTO v_existing_contact_count
                        FROM guardians
                        WHERE contact_number = v_contact;
                        IF v_existing_contact_count = 0 THEN
                            LEAVE contact_loop;
                        END IF;
                        SET v_contact_suffix = v_contact_suffix + 1;
                    END LOOP;

                    -- Columns: guardianId, fullName, relationship, contactNumber, email.
                    -- Auto-generated ID: guardianId.
                    INSERT INTO guardians (full_name, relationship, contact_number, email)
                    VALUES (v_guardian_name, 'Parent/Guardian', v_contact, v_email);
                    SET v_guardian_id = LAST_INSERT_ID();

                    -- Columns: studentId, schoolStudentNumber, firstName, middleName, lastName, enrollmentStatus.
                    -- Auto-generated ID: studentId.
                    INSERT INTO students (
                        school_student_number, first_name, middle_name, last_name, enrollment_status
                    )
                    VALUES (v_student_num, v_fn, v_mn, v_ln, 'Active');
                    SET v_student_id = LAST_INSERT_ID();

                    -- Columns: enrollmentId, studentId, sectionId, academicYearId, enrollmentStatus.
                    -- Auto-generated ID: enrollmentId.
                    INSERT INTO student_enrollments (
                        student_id, section_id, academic_year_id, enrollment_status
                    )
                    VALUES (v_student_id, v_section_id, v_academic_year_id, 'Active');

                    -- Columns: studentId, guardianId, isPrimaryContact, mayReceiveNotifications.
                    INSERT INTO student_guardians (
                        student_id, guardian_id, is_primary_contact, may_receive_notifications
                    )
                    VALUES (v_student_id, v_guardian_id, TRUE, TRUE);

                    SET s_idx = s_idx + 1;
                END WHILE;
            END LOOP;

            CLOSE section_cursor;
        END;
    END IF;

    DROP TEMPORARY TABLE temp_first_names;
    DROP TEMPORARY TABLE temp_middle_names;
    DROP TEMPORARY TABLE temp_last_names;
    DROP TEMPORARY TABLE temp_generated_student_names;
END//

CALL temp_seed_mock_data()//
DROP PROCEDURE IF EXISTS temp_seed_mock_data//

DELIMITER ;


-- Adds handbook severity categories used by the demo catalog.
-- Columns: categoryId, categoryName, description.
-- Auto-generated ID: categoryId.
INSERT INTO violation_categories (category_name, description)
SELECT seed.category_name, seed.description
FROM (
    SELECT 'Minor' AS category_name, 'Minor offense classification from the handbook.' AS description
    UNION ALL SELECT 'Moderate', 'Moderate offense classification from the handbook.'
    UNION ALL SELECT 'Major', 'Major offense classification from the handbook.'
) seed
WHERE NOT EXISTS (
    SELECT 1
    FROM violation_categories existing
    WHERE existing.category_name = seed.category_name
);

-- Adds demo handbook sections beneath their severity categories.
-- Columns: handbookSectionId, categoryId, sectionName, description.
-- Auto-generated ID: handbookSectionId.
INSERT INTO handbook_sections (category_id, section_name, description)
SELECT category.category_id, seed.section_name, seed.description
FROM (
    SELECT 'Minor' AS category_name, 'Attendance' AS section_name,
           'Demo section for attendance-related minor offenses.' AS description
    UNION ALL SELECT 'Moderate', 'Disruptive Behavior',
           'Demo section for disruptive-behavior moderate offenses.'
    UNION ALL SELECT 'Major', 'Vandalism',
           'Demo section for major property-damage offenses.'
) seed
JOIN violation_categories category ON category.category_name = seed.category_name
WHERE NOT EXISTS (
    SELECT 1
    FROM handbook_sections existing
    WHERE existing.category_id = category.category_id
      AND existing.section_name = seed.section_name
);

-- Adds illustrative active violation types under their handbook sections.
-- Columns: violationTypeId, handbookSectionId, violationName, description, isActive, policyReference.
-- Auto-generated ID: violationTypeId.
INSERT INTO violation_types (handbook_section_id, violation_name, description, is_active)
SELECT section.handbook_section_id, seed.violation_name, seed.description, TRUE
FROM (
    SELECT 'Minor' AS category_name, 'Attendance' AS section_name,
           'Unexcused Absence' AS violation_name, 'Sample attendance violation type.' AS description
    UNION ALL SELECT 'Minor', 'Attendance', 'Repeated Tardiness', 'Sample attendance violation type.'
    UNION ALL SELECT 'Moderate', 'Disruptive Behavior', 'Classroom Disruption',
           'Sample disruptive-behavior violation type.'
    UNION ALL SELECT 'Moderate', 'Disruptive Behavior', 'Disrespectful Conduct',
           'Sample disruptive-behavior violation type.'
    UNION ALL SELECT 'Major', 'Vandalism', 'Damage to School Property',
           'Sample major property-damage violation type.'
) seed
JOIN violation_categories category ON category.category_name = seed.category_name
JOIN handbook_sections section ON section.category_id = category.category_id
   AND section.section_name = seed.section_name
WHERE NOT EXISTS (
    SELECT 1
    FROM violation_types existing
    WHERE existing.handbook_section_id = section.handbook_section_id
      AND existing.violation_name = seed.violation_name
);
