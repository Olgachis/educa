INSERT INTO campus(id, name, campus_name, type, institution_id, primary_campus, internship, initial_education, preschool, basic, secondary, high_school, version, date_created, last_updated)
VALUES('72', 'Fundación Don Bosco para el Desarrollo del Estudiante Morelense, A.C. ', 'Juan Morales', 'A.C.', '28', false, false, false, false, false, false, true, 0, current_timestamp, current_timestamp);


INSERT INTO educa_user (id, username, password, campus_id, date_created, last_updated, version, role_id)
VALUES ('72', 'DONBOSCO6', '$2a$08$In/QhvfcnwvgfA3dKrqQNuPnfu3Gv6IJ3NDPyyLBYs9Z/hhZN9aXG', '72', current_timestamp, current_timestamp, 0, 'institution');
