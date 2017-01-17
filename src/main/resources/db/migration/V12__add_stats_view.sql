DROP VIEW IF EXISTS progress_data;
DROP VIEW IF EXISTS institution_section_data;
DROP VIEW IF EXISTS institution_data;
DROP VIEW IF EXISTS institution_types;

CREATE OR REPLACE VIEW campus_types AS SELECT campus.id,
    'internship'::text AS type
   FROM campus
  WHERE campus.internship = true
UNION ALL
 SELECT campus.id,
    'initial_education'::text AS type
   FROM campus
  WHERE campus.initial_education = true
UNION ALL
 SELECT campus.id,
    'preschool'::text AS type
   FROM campus
  WHERE campus.preschool = true
UNION ALL
 SELECT campus.id,
    'basic'::text AS type
   FROM campus
  WHERE campus.basic = true
UNION ALL
 SELECT campus.id,
    'secondary'::text AS type
   FROM campus
  WHERE campus.secondary = true
UNION ALL
 SELECT campus.id,
    'high_school'::text AS type
   FROM campus
  WHERE campus.high_school = true;

create or replace view campus_data as SELECT campus.id,
    campus.name,
    campus.date_created,
    campus.last_updated,
    campus.version,
    campus.type,
    campus.internship,
    campus.initial_education,
    campus.preschool,
    campus.basic,
    campus.secondary,
    campus.high_school,
        CASE campus.type
            WHEN 'A.C.'::text THEN 'ac'::text
            ELSE 'iap'::text
        END AS type_id
   FROM campus;


create or replace view campus_section_data as SELECT eu.id AS user_id,
    id.id AS campus_id,
    id.name AS campus_name,
    id.type_id AS type,
    qs.id AS section_id,
    qs.name AS section_name,
    json_array_length(qs.question_json::json -> 'questions'::text) AS total_questions
   FROM campus_data id
     JOIN educa_user eu ON eu.campus_id::text = id.id::text
     JOIN questionnaire_section qs ON qs.questionnaire_id::text = id.type_id
  WHERE (qs.question_json::json -> 'dependsOn'::text) IS NULL OR (qs.question_json::json ->> 'dependsOn'::text IN ( SELECT campus_types.type
           FROM campus_types
          WHERE campus_types.id::text = id.id::text));

create or replace view progress_data as SELECT qs.dimension_id,
    qs.dimension,
    qs.subdimension_id,
    qs.subdimension,
    isd.user_id,
    isd.campus_id,
    isd.campus_name,
    isd.type,
    isd.section_id,
    isd.section_name,
    isd.total_questions,
    ( SELECT count(*) AS count
           FROM ( SELECT json_array_elements(sr.response_json::json -> 'responses'::text) ->> 'response'::text AS r
                   FROM section_response sr
                  WHERE sr.section_id::text = isd.section_id::text AND isd.user_id::text = sr.user_id::text) a
          WHERE a.r IS NOT NULL) AS answered_questions,
    ( SELECT sr.last_updated AT TIME ZONE 'America/Mexico_City'
                   FROM section_response sr
                  WHERE sr.section_id::text = isd.section_id::text AND isd.user_id::text = sr.user_id::text ) AS last_updated
   FROM campus_section_data isd
     JOIN questionnaire_section qs ON qs.id::text = isd.section_id::text;