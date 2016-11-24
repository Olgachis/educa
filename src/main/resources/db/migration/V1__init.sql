CREATE TABLE permission (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(512) NOT NULL,
    date_created TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT
);

CREATE TABLE role (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(512) NOT NULL,
    date_created TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT
);

CREATE TABLE role_permission (
    role_permissions_id VARCHAR(255),
    permission_id VARCHAR(255)
);

CREATE TABLE institution (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(512) NOT NULL,
    date_created TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT
);

CREATE TABLE educa_user (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    username VARCHAR(512) NOT NULL,
    password VARCHAR(512) NOT NULL,
    institution_id VARCHAR(255) REFERENCES institution(id),
    role_id VARCHAR(255) NOT NULL REFERENCES role(id),
    date_created TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT
);

INSERT INTO role VALUES('admin', 'Administrador General', current_timestamp, current_timestamp);

INSERT INTO educa_user VALUES('iamedu', 'iamedu', '$2a$10$Mio7zSJymaQX3A9VAzuWguqg7.Cm4DD.cpFUXDeiX4UpbJHCFsS3m', null, 'admin', current_timestamp, current_timestamp);

