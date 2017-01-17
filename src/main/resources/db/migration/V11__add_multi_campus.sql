ALTER TABLE institution RENAME TO campus;
ALTER TABLE educa_user RENAME COLUMN institution_id TO campus_id;
ALTER TABLE questionnaire_response RENAME COLUMN institution_id TO campus_id;

CREATE TABLE institution (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(512) NOT NULL,
    internship BOOLEAN NOT NULL,
    initial_education BOOLEAN NOT NULL,
    preschool BOOLEAN NOT NULL,
    basic BOOLEAN NOT NULL,
    secondary BOOLEAN NOT NULL,
    high_school BOOLEAN NOT NULL,
    date_created TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT
);

INSERT INTO institution
SELECT id,
name,
internship,
initial_education,
preschool,
basic,
secondary,
high_school,
current_timestamp,
current_timestamp,
0 FROM campus;

ALTER TABLE campus ADD COLUMN primary_campus BOOLEAN;
UPDATE campus SET primary_campus = true;
ALTER TABLE campus ALTER COLUMN primary_campus SET NOT NULL;


ALTER TABLE campus ADD COLUMN institution_id VARCHAR(255) REFERENCES institution(id);
UPDATE campus SET institution_id = id;

ALTER TABLE campus ALTER COLUMN institution_id SET NOT NULL;

