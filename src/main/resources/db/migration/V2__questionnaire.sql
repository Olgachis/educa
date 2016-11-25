CREATE TABLE questionnaire (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(512) NOT NULL,
    date_created TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT
);

CREATE TABLE questionnaire_section (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(512) NOT NULL,
    questionnaire_id VARCHAR(255) NOT NULL REFERENCES questionnaire(id),
    question_json TEXT NOT NULL,
    date_created TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT
);

CREATE TABLE section_response (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    questionnaire_id VARCHAR(255) NOT NULL REFERENCES questionnaire(id),
    section_id VARCHAR(255) NOT NULL REFERENCES questionnaire_section(id),
    user_id VARCHAR(255) NOT NULL REFERENCES educa_user(id),
    response_json TEXT NOT NULL,
    comments TEXT,
    date_created TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT
);
