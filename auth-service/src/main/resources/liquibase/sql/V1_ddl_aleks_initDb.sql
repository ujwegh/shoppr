--liquibase formatted sql

--changeset aleks:initDB logicalFilePath:/

CREATE SCHEMA ${auth.schemaName};

CREATE TABLE ${auth.schemaName}.users
(
    user_id    serial PRIMARY KEY,
    email     varchar(24) NOT NULL,
    salt      varchar(32) NOT NULL, -- 16 bytes base64
    hash      varchar(60) NOT NULL, -- 32 bytes base64
    secret_key varchar(32) NOT NULL
);

CREATE UNIQUE INDEX reg_user_email_uidx ON ${auth.schemaName}.users (email);