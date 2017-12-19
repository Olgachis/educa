--nueva institución
INSERT INTO institution(id, name, version, internship, initial_education, preschool, basic, secondary, high_school, date_created, last_updated)
VALUES('54', 'Espíritu de Campeón, A.C.', 0, false, false, false, false, false, false, current_timestamp, current_timestamp);

INSERT INTO campus(id, name, campus_name, type, institution_id, primary_campus, internship, initial_education, preschool, basic, secondary, high_school, version, date_created, last_updated)
VALUES('80', 'Espíritu de Campeón, A.C.', 'Primaria', 'A.C.', '54', true, false, false, false, true, false, false, 0, current_timestamp, current_timestamp);

INSERT INTO educa_user (id, username, password, campus_id, date_created, last_updated, version, role_id)
VALUES ('80', 'CAMPEON', '$2a$10$mOMftnUXyTVcQaPnoer6o.t05UWlAdTAZZMsSuKnH.ILvxeVzK43a', '54', current_timestamp, current_timestamp, 0, 'institution');

INSERT INTO campus(id, name, campus_name, type, institution_id, primary_campus, internship, initial_education, preschool, basic, secondary, high_school, version, date_created, last_updated)
VALUES('81', 'Espíritu de Campeón, A.C.', 'Preescolar', 'I.A.P.', '54', false, false, true, true, false, true, true, 0, current_timestamp, current_timestamp);

INSERT INTO educa_user (id, username, password, campus_id, date_created, last_updated, version, role_id)
VALUES ('81', 'CAMPEON2', '$2a$10$pDnTaOrEpE6xA9cLAOFDI.0XHYFAOcG9mmLwLocz10dhqnzi0UaXy', '54', current_timestamp, current_timestamp, 0, 'institution');
