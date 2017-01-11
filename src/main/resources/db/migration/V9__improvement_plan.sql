CREATE TABLE questionnaire_response (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    institution_id VARCHAR(255) NOT NULL REFERENCES institution(id),
    response_json TEXT NOT NULL,
    date_created TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT
);


