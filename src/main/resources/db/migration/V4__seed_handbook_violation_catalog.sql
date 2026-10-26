-- Replace the small illustrative catalog with the supplied handbook offenses.
-- The full handbook wording is stored in description; violation_name is the concise selector label.
-- Repeated-offense sections are intentionally excluded because they are policy rules, not conduct types.
CREATE TEMPORARY TABLE tmp_handbook_offenses (
    category_name VARCHAR(30) NOT NULL,
    section_name VARCHAR(120) NOT NULL,
    violation_name VARCHAR(150) NOT NULL,
    description TEXT NOT NULL
);

INSERT INTO tmp_handbook_offenses (category_name, section_name, violation_name, description) VALUES
('Minor', 'Attendance', 'Absenteeism', 'Absence without an excuse.'),
('Minor', 'Attendance', 'Truancy or aimless roaming', 'Truancy or aimless roaming.'),
('Minor', 'Attendance', 'Cutting or skipping classes', 'Cutting or skipping classes.'),
('Minor', 'Attendance', 'Failure to attend chapel hour', 'Failure to attend chapel hour on time or failure to attend at all.'),
('Minor', 'Attendance', 'Loitering along corridors', 'Loitering along corridors during class hours.'),
('Minor', 'Attendance', 'Staying outside classrooms', 'Staying idly in front of or outside classrooms while classes are ongoing.'),
('Minor', 'Identification Card', 'Not wearing the identification card', 'Non-wearing of identification card; consolidated with the handbook''s duplicate item "Not wearing the identification card on campus."'),
('Minor', 'Identification Card', 'Improper display of identification card', 'Improper display of identification card.'),
('Minor', 'Identification Card', 'Tampering with identification cards', 'Tampering with, mutilating, switching, or altering identification cards.'),
('Minor', 'Identification Card', 'Entering campus without school ID', 'Entering campus without the appropriate school ID.'),
('Minor', 'Identification Card', 'Lending another person an ID card', 'Lending someone else''s identification card.'),
('Minor', 'School Uniform', 'Improper or incomplete uniform', 'Improper or incomplete uniform.'),
('Minor', 'School Uniform', 'Unofficial uniform modification', 'Unofficial modification of the prescribed uniform.'),
('Minor', 'School Uniform', 'Uniform or wash-day regulation violation', 'Violation of school uniform or wash-day regulations.'),
('Minor', 'School Uniform', 'PE uniform in non-PE classes', 'Wearing the PE uniform in non-PE classes.'),
('Minor', 'School Uniform', 'Laboratory uniform in non-laboratory classes', 'Wearing laboratory uniforms in non-laboratory classes.'),
('Minor', 'School Uniform', 'Footwear regulation violation', 'Violation of prescribed footwear regulations.'),
('Minor', 'Haircut', 'Failure to comply with Gentleman''s Cut', 'Failure to comply with the prescribed Gentleman''s Cut for Senior High School boys.'),
('Minor', 'Haircut', 'Unauthorized haircut modification', 'Unauthorized modification of the prescribed haircut.'),
('Minor', 'Haircut', 'Hair color, style, or length violation', 'Violation of hair color, hairstyle, or hair-length policies.'),
('Minor', 'Attire/Grooming', 'Prohibited makeup, jewelry, or accessories', 'Wearing makeup, nail polish, expensive jewelry such as anklets or dangling earrings, hair wigs, or other prohibited accessories.'),
('Minor', 'Attire/Grooming', 'Unnatural body marks, piercings, or tattoos', 'Unnatural body marks or paints, body piercing, or tattoos.'),
('Minor', 'Attire/Grooming', 'Males wearing pierced earrings', 'Piercing and wearing earrings among males.'),
('Minor', 'Attire/Grooming', 'Head apparel inside campus', 'Wearing head apparel such as caps or scarves inside campus.'),
('Minor', 'Canteen', 'Canteen misbehavior', 'Misbehavior or conduct unbecoming in the canteen.'),
('Minor', 'Canteen', 'Leaving the canteen table unclean', 'Leaving the table unclean.'),
('Minor', 'Use of Facilities', 'Failure to maintain cleanliness', 'Failure to maintain cleanliness.'),
('Minor', 'Use of Facilities', 'Careless or deliberate waste disposal', 'Deliberate or careless disposal of waste.'),
('Minor', 'Use of Facilities', 'Littering or improper waste disposal', 'Littering or improper disposal of waste materials.'),
('Minor', 'Use of Facilities', 'Eating or drinking in instructional areas', 'Eating or drinking inside classrooms, laboratories, halls, the chapel, and other instructional areas.'),
('Minor', 'Classroom and Campus Decorum', 'Failure to observe silence', 'Failure to observe silence in classrooms, libraries, laboratories, offices, the chapel, and hallways.'),
('Minor', 'Classroom and Campus Decorum', 'Disturbing a class', 'Disturbing one''s own class or another class.'),
('Minor', 'Classroom and Campus Decorum', 'Blocking stairways or doors', 'Deliberately blocking stairways or doors.'),
('Minor', 'Classroom and Campus Decorum', 'Sitting on stairways during class', 'Sitting on stairways during class hours.'),
('Minor', 'Classroom and Campus Decorum', 'Using prohibited audio devices', 'Using iPods or similar devices during prohibited times.'),
('Minor', 'Classroom and Campus Decorum', 'Possession of playing cards', 'Possession of playing cards.'),
('Minor', 'Use of School Communication and Posting Facilities', 'Unauthorized posting of notices or materials', 'Posting notices, posters, tarpaulins, streamers, or similar materials without prior permission or in unauthorized areas.'),
('Minor', 'Use of School Communication and Posting Facilities', 'Improper use of boards', 'Using or writing on boards outside class sessions or for non-instructional purposes.'),
('Minor', 'Use of School Communication and Posting Facilities', 'Tampering with bulletin-board announcements', 'Tampering with announcements on bulletin boards.'),
('Minor', 'Use of School Communication and Posting Facilities', 'Unauthorized announcements or campaigns', 'Making oral announcements, conducting room-to-room campaigns, or engaging in propaganda activities without prior permission.'),
('Minor', 'Use of School Communication and Posting Facilities', 'Unauthorized leaflet or information-material distribution', 'Unauthorized possession, printing, reproduction, circulation, or distribution of leaflets or information materials, unless the incident is classified as a Major Offense by authorized school personnel.'),
('Minor', 'Compliance with School Regulations', 'Non-compliance with lawful signs or regulations', 'Non-compliance with lawful signs, school instrumentalities, and ordinary school regulations.'),
('Minor', 'Compliance with School Regulations', 'Non-observance of campus order and discipline', 'Non-observance of campus order and discipline policies of a similar nature.'),
('Moderate', 'Cellular Phones/Electronic Devices', 'Unauthorized device use', 'Using cellular phones or electronic devices without official permission while inside school.'),
('Moderate', 'Cellular Phones/Electronic Devices', 'Displaying prohibited electronic devices', 'Bringing out or displaying electronic devices within school premises when prohibited.'),
('Moderate', 'Cellular Phones/Electronic Devices', 'Cyber misconduct', 'Cyber misconduct, including misuse of ICT, online platforms, or digital devices.'),
('Moderate', 'Disruptive Behavior', 'Bullying', 'Bullying.'),
('Moderate', 'Disruptive Behavior', 'Creating boisterous noise', 'Creating boisterous noise.'),
('Moderate', 'Disruptive Behavior', 'Throwing objects', 'Throwing objects.'),
('Moderate', 'Disruptive Behavior', 'Unauthorized classroom intrusion', 'Intrusion, such as entering another classroom without permission.'),
('Moderate', 'Disruptive Behavior', 'Loitering or aimless roaming', 'Loitering or aimless roaming during school hours.'),
('Moderate', 'Disruptive Behavior', 'Provocation resulting in confrontation', 'Verbal, non-verbal, or actual provocation resulting in heated confrontation.'),
('Moderate', 'Disruptive Behavior', 'Disturbing campus peace and order', 'Acts disturbing peace and order within the school campus.'),
('Moderate', 'Disruptive Behavior', 'Gambling', 'Gambling in any form.'),
('Moderate', 'Disruptive Behavior', 'Misuse of another person''s ID card', 'Possession or use of another person''s ID card beyond ordinary misuse.'),
('Moderate', 'Academic Integrity', 'Plagiarism or cheating', 'Plagiarism and cheating.'),
('Moderate', 'Academic Integrity', 'Unauthorized use of artificial intelligence', 'Unauthorized use of artificial intelligence (AI).'),
('Moderate', 'Disobedience/Defiance', 'Disobedience of school or classroom rules', 'Disobedience of school or classroom rules and regulations.'),
('Moderate', 'Disobedience/Defiance', 'Failure to give proper greetings', 'Disrespect through failure to execute proper greetings to teaching and non-teaching personnel.'),
('Moderate', 'Disobedience/Defiance', 'Disrespect for national or religious symbols', 'Disrespect for emblems and symbols representing the country or religions, including the Bible and Qur''an.'),
('Moderate', 'Disobedience/Defiance', 'Preventing class attendance or inciting rule violations', 'Preventing students from attending classes or inciting them to violate school rules.'),
('Moderate', 'Disobedience/Defiance', 'Non-attendance at school events', 'Outright disregard through non-attendance at academic, spiritual, and special school events.'),
('Moderate', 'Disobedience/Defiance', 'Failure to comply with an investigation summons', 'Failure to comply with summons or calls for investigation.'),
('Moderate', 'Disobedience/Defiance', 'Other analogous acts of similar gravity', 'Other analogous acts of similar gravity as determined by school authorities.'),
('Moderate', 'Co-curricular Behavior', 'Abusive language or gestures at activities', 'Using obscene, profane, or abusive language or gestures during school co-curricular activities.'),
('Moderate', 'Co-curricular Behavior', 'Harassment during co-curricular activities', 'Harassing opposing teams, officials, bands, cheerleaders, or performing groups.'),
('Moderate', 'Co-curricular Behavior', 'Dangerous behavior during co-curricular activities', 'Fighting, pushing, tripping, or engaging in other behavior deemed dangerous during co-curricular activities.'),
('Moderate', 'Institutional Regulation Violations', 'Unauthorized use of school facilities', 'Unauthorized or illegitimate use of classrooms, halls, laboratories, and other school facilities.'),
('Moderate', 'Institutional Regulation Violations', 'Unauthorized ticket sales or fundraising', 'Unauthorized selling of tickets or fundraising activities without prior approval.'),
('Moderate', 'Institutional Regulation Violations', 'Unauthorized school contracts or transactions', 'Unauthorized entry into contracts or financial transactions in the name of the school.'),
('Major', 'Abuse (Verbal or Written)', 'Oral defamation', 'Oral defamation, including vulgar or offensive language, obscene or threatening statements, and derogatory remarks directed against a person.'),
('Major', 'Abuse (Verbal or Written)', 'False accusations damaging reputation', 'False accusations that damage another person''s dignity, reputation, or character.'),
('Major', 'Abuse (Verbal or Written)', 'Sending unwholesome remarks', 'Writing or sending unwholesome remarks through notes, text messages, social media, or other communication.'),
('Major', 'Abuse (Verbal or Written)', 'Threats, coercion, intimidation, or assault', 'Threats, coercion, intimidation, compulsion, or physical assault against any member of the school community.'),
('Major', 'Abuse (Verbal or Written)', 'Perjury or false testimony', 'Perjury or giving false testimony during investigations or proceedings.'),
('Major', 'Abuse (Verbal or Written)', 'Online bullying', 'Online bullying.'),
('Major', 'Abuse (Verbal or Written)', 'Contempt or ridicule of religious practices', 'Contempt or ridicule of religious celebrations, practices, beliefs, prayers, or activities.'),
('Major', 'Abuse (Verbal or Written)', 'Circulating false or derogatory statements', 'Circulating false or derogatory statements against members of the school community.'),
('Major', 'Abuse (Verbal or Written)', 'Libelous or abusive publication', 'Publishing articles deemed libelous or abusive against members of the school community.'),
('Major', 'Burglary/Theft/Extortion', 'Taking property without permission', 'Getting or borrowing someone else''s property without permission, by force, or under false pretenses.'),
('Major', 'Burglary/Theft/Extortion', 'Taking, using, or hiding another''s property', 'Willfully taking, using, transferring, or hiding another person''s property.'),
('Major', 'Burglary/Theft/Extortion', 'Forcefully collecting money', 'Forcefully collecting money from another person.'),
('Major', 'Burglary/Theft/Extortion', 'Forcible or unauthorized entry', 'Forcible or unauthorized entry into school premises.'),
('Major', 'Burglary/Theft/Extortion', 'Theft or stealing', 'Theft or stealing, whether attempted, frustrated, or consummated.'),
('Major', 'Unauthorized Acts/Items Causing Disruption', 'Possession or distribution of disruptive items', 'Possession, use, or distribution of objects or items that may cause distraction, disruption, or disturbance within school premises, including prohibited wallet chains, games, chains, laser pens, shock pens, and similar items.'),
('Major', 'Unauthorized Acts/Items Causing Disruption', 'Possession of pornographic paraphernalia', 'Possession of pornographic paraphernalia.'),
('Major', 'Unauthorized Acts/Items Causing Disruption', 'Cross-dressing as classified by the handbook', 'Cross-dressing, as classified by the supplied handbook.'),
('Major', 'Vandalism', 'Defacing or damaging school property', 'Defacing or damaging school property by writing, carving, marking, cutting, or otherwise defacing facilities, furniture, equipment, or fixtures.'),
('Major', 'Vandalism', 'Damaging instructional or informational materials', 'Damaging instructional or informational materials, including bulletin boards, notices, posters, and displayed learning materials.'),
('Major', 'Vandalism', 'Unauthorized marks or graffiti on premises', 'Writing, drawing, or placing unauthorized marks, graffiti, or inscriptions on walls, floors, doors, or other school premises.'),
('Major', 'Falsification', 'Altering school records', 'Alteration of school records, including unauthorized changes to grades, entries, or official school documents.'),
('Major', 'Falsification', 'Forgery or misrepresentation', 'Forgery and misrepresentation, including forged signatures, documents, or falsified records.'),
('Major', 'Falsification', 'Tampering with academic records', 'Falsification or tampering with academic records, documents, or school papers.'),
('Major', 'Falsification', 'False statements in official dealings', 'Making false statements, fraud, or misrepresentation in official dealings.'),
('Major', 'Falsification', 'Tampering with school ID cards', 'Forging, falsifying, mutilating, or tampering with school ID cards.'),
('Major', 'Falsification', 'Unauthorized use of the school name', 'Unauthorized or unofficial use of the school''s name in publications and similar materials.'),
('Major', 'Falsification', 'Unauthorized representation harming school reputation', 'Causing damage to the school''s name and reputation through unauthorized representation.'),
('Major', 'Falsification', 'Violating school-record confidentiality or security', 'Violating the confidentiality or security of school records or credentials.'),
('Major', 'Physical Misconduct and Acts of Violence', 'Physical harm or altercations', 'Inflicting harm or engaging in physical altercations, including hurting others, causing injury, fighting, or provoking physical confrontations.'),
('Major', 'Physical Misconduct and Acts of Violence', 'Damaging belongings or school property', 'Destroying or damaging other people''s belongings or school property.'),
('Major', 'Physical Misconduct and Acts of Violence', 'Assaulting school personnel', 'Assaulting teaching or non-teaching school personnel.'),
('Major', 'Physical Misconduct and Acts of Violence', 'Open defiance of authority', 'Open defiance of authority, including blatant disrespect or refusal to comply with school authorities, security personnel, or authorized representatives.'),
('Major', 'Physical Misconduct and Acts of Violence', 'Threats, coercion, intimidation, or assault', 'Threats, coercion, intimidation, compulsion, or physical assault.'),
('Major', 'Physical Misconduct and Acts of Violence', 'Hazing or initiation-related harm', 'Hazing or inflicting physical or mental harm related to initiation rites.'),
('Major', 'Prohibited Possession and Use of Hazardous and Illegal Items', 'Possession of tobacco or smoking paraphernalia', 'Possession of tobacco or smoking-related items, including cigarettes, tobacco products, matches, lighters, and similar paraphernalia.'),
('Major', 'Prohibited Possession and Use of Hazardous and Illegal Items', 'Possession or use of electronic smoking devices', 'Possession or use of electronic smoking devices, including vapes and related accessories.'),
('Major', 'Prohibited Possession and Use of Hazardous and Illegal Items', 'Illegal drugs or controlled substances', 'Possession, use, or distribution of illegal drugs and controlled substances, or being under their influence as described by the applicable policy.'),
('Major', 'Prohibited Possession and Use of Hazardous and Illegal Items', 'Possession of gambling or betting materials', 'Possession of gambling or betting materials.'),
('Major', 'Prohibited Possession and Use of Hazardous and Illegal Items', 'Entering campus under alcohol influence', 'Entering campus under the influence of alcohol.'),
('Major', 'Prohibited Possession and Use of Hazardous and Illegal Items', 'Possession of deadly weapons or harmful objects', 'Possession of deadly weapons or harmful objects capable of causing injury.'),
('Major', 'Immoral Acts', 'Acts of lasciviousness', 'Acts of lasciviousness.'),
('Major', 'Immoral Acts', 'Peeping tom', 'Peeping tom.'),
('Major', 'Immoral Acts', 'Exhibitionism', 'Exhibitionism.'),
('Major', 'Immoral Acts', 'Sexual promiscuity', 'Sexual promiscuity.'),
('Major', 'Immoral Acts', 'Premarital sex (fornication)', 'Premarital sex (fornication), as stated in the handbook.'),
('Major', 'Immoral Acts', 'Sexual harassment', 'Sexual harassment.'),
('Major', 'Immoral Acts', 'Rape', 'Rape.'),
('Major', 'Immoral Acts', 'Pregnancy without marriage', 'Pregnancy without marriage, as stated in the handbook.'),
('Major', 'Immoral Acts', 'Seduction', 'Seduction.'),
('Major', 'Immoral Acts', 'Sexual advances or acts endangering moral integrity', 'Sexual advances or other acts the handbook describes as endangering moral integrity.'),
('Major', 'Immoral Acts', 'Abortion', 'Abortion, as stated in the handbook.'),
('Major', 'Illegal and Anti-social Activities', 'Establishing or joining a fraternity or sorority', 'Establishing or joining a fraternity or sorority.'),
('Major', 'Illegal and Anti-social Activities', 'Recruiting for a fraternity or sorority', 'Recruiting members to join a fraternity or sorority.'),
('Major', 'Illegal and Anti-social Activities', 'Establishing or joining gangs', 'Establishing or joining gangs.'),
('Major', 'Illegal and Anti-social Activities', 'Bribery of school personnel', 'Bribery of teachers, school authorities, or personnel.'),
('Major', 'Illegal and Anti-social Activities', 'Unauthorized unlawful boycotts or demonstrations', 'Organizing or participating in unauthorized unlawful boycotts, strikes, or demonstrations.'),
('Major', 'Illegal and Anti-social Activities', 'Swindling, extortion, fraud, or bouncing checks', 'Swindling, extortion, fraud, or issuing bouncing checks.'),
('Major', 'Illegal and Anti-social Activities', 'Malversation or misappropriation of funds', 'Malversation or misappropriation of funds.'),
('Major', 'Illegal and Anti-social Activities', 'Court conviction affecting conduct', 'Conviction by a court for a criminal or civil offense affecting moral character or student conduct.'),
('Major', 'Illegal and Anti-social Activities', 'Serious off-campus misconduct connected with school', 'Off-campus misconduct connected with school activities that seriously affects the school or its members.'),
('Major', 'False Alarm/Bomb Threats', 'False reporting creating commotion', 'False reporting of any kind that may create commotion.'),
('Major', 'False Alarm/Bomb Threats', 'Breaking or tampering with fire alarm', 'Breaking or tampering with the fire alarm.'),
('Major', 'Arson', 'Burning school property', 'Deliberate or accidental burning of school property.'),
('Major', 'Cyber-Related Offenses', 'Malicious or indecent posts humiliating the school', 'Posting malicious, indecent, or vulgar comments or pictures on YouTube, Facebook, or similar platforms that humiliate or embarrass the school or its community.'),
('Major', 'Cyber-Related Offenses', 'Sending pornographic pictures online', 'Sending pornographic pictures to anyone on the internet.'),
('Major', 'Cyber-Related Offenses', 'Supporting degrading online comments about the institution', 'Posting in support of people making degrading comments about the institution through malicious, vulgar, or indecent statements.'),
('Major', 'Cyber-Related Offenses', 'Unauthorized access or alteration of computer data', 'Unauthorized access to, decoding of, or alteration of computer data or school systems.'),
('Major', 'Cyber-Related Offenses', 'Unauthorized online use of the school name', 'Unauthorized or unofficial use of the school''s name in publications or similar materials on cyber platforms.'),
('Major', 'Cyber-Related Offenses', 'Circulating false statements online', 'Circulating false or derogatory statements against school-community members in cyberspace.'),
('Major', 'Cyber-Related Offenses', 'Online violation of record confidentiality or security', 'Violating the confidentiality or security of school records or credentials posted on social media platforms.'),
('Major', 'Cyber-Related Offenses', 'Libelous or abusive online publication', 'Publishing articles deemed libelous or abusive against school-community members in cyberspace.'),
('Major', 'Prohibited Operation', 'Unauthorized operation of circuit breakers', 'Unauthorized switching off or switching on of circuit breakers.'),
('Major', 'Prohibited Operation', 'Redirecting CCTV cameras', 'Redirecting CCTV cameras.'),
('Major', 'Prohibited Operation', 'Unauthorized use of classroom equipment', 'Unauthorized use of classroom equipment.'),
('Major', 'Prohibited Operation', 'Unauthorized use of school property', 'Unauthorized use of school property.');

-- Retire the small illustrative sample choices so they are not mixed with handbook entries.
UPDATE violation_types
SET is_active = FALSE
WHERE description LIKE 'Sample % violation type.';

INSERT INTO handbook_sections (category_id, section_name, description)
SELECT categories.category_id, sections.section_name, 'Section from the supplied student handbook.'
FROM (
    SELECT DISTINCT category_name, section_name
    FROM tmp_handbook_offenses
) sections
JOIN violation_categories categories ON categories.category_name = sections.category_name
WHERE NOT EXISTS (
    SELECT 1
    FROM handbook_sections existing
    WHERE existing.category_id = categories.category_id
      AND existing.section_name = sections.section_name
);

INSERT INTO violation_types (handbook_section_id, violation_name, description, is_active)
SELECT sections.handbook_section_id, offenses.violation_name, offenses.description, TRUE
FROM tmp_handbook_offenses offenses
JOIN violation_categories categories ON categories.category_name = offenses.category_name
JOIN handbook_sections sections
  ON sections.category_id = categories.category_id
 AND sections.section_name = offenses.section_name
WHERE NOT EXISTS (
    SELECT 1
    FROM violation_types existing
    WHERE existing.handbook_section_id = sections.handbook_section_id
      AND existing.violation_name = offenses.violation_name
);

DROP TEMPORARY TABLE tmp_handbook_offenses;
