--INSERT INTO educa_user (id, username, password, institution_id, date_created, last_updated, version, role_id) VALUES ('1', 'APAC', '$2a$10$FbcwrD05ZzYzvaHBFNOzn.uAD1dF2re2iGr3qGFNo/mHCTHGZaHP2',
-- '1', current_timestamp, current_timestamp, 0, 'institution');

--INSERT INTO campus(id, name, campus_name, type, institution_id, primary_campus, internship, initial_education, preschool, basic, secondary, high_school, version, date_created, last_updated)
--VALUES('54', 'APAC, I.A.P. Asociación Pro-Personas con Parálisis Cerebral', 'Dr. Arce', 'I.A.P.', '1', false, false, false, false, false, true, true, 0, current_timestamp, current_timestamp);

--INSERT INTO educa_user (id, username, password, campus_id, date_created, last_updated, version, role_id)
--VALUES ('54', 'APAC2', '$2a$06$Tdqp2mLDyail5pelXJa.VOY0TnyNFB8j35/1MUgn1FhwhUUmz0Kkq', '54', current_timestamp, current_timestamp, 0, 'institution');

-- CLAUDINA 14
INSERT INTO campus(id, name, campus_name, type, institution_id, primary_campus, internship, initial_education, preschool, basic, secondary, high_school, version, date_created, last_updated)
VALUES('73', 'Claudina Thévenet, A.C.', 'Secundaria', 'A.C.', '14', false, false, false, false, true, false, false, 0, current_timestamp, current_timestamp);

INSERT INTO educa_user (id, username, password, campus_id, date_created, last_updated, version, role_id)
VALUES ('73', 'CLAUDINA2', '$2a$10$S0hnP75V.S56LO0x9l3uiefiTih1qzqJ68maz.5RA5pLPQua2XL4O', '73', current_timestamp, current_timestamp, 0, 'institution');
