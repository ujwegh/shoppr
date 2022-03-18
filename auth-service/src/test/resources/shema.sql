--liquibase formatted sql

--changeset aleks:HB-395-light-auth-service logicalFilePath:/

CREATE SCHEMA ${auth.schemaName};

CREATE TABLE ${auth.schemaName}.registered_user
(
    userId    serial PRIMARY KEY,
    email     varchar(24) NOT NULL,
    salt      varchar(24) NOT NULL, -- 16 bytes base64
    hash      varchar(44) NOT NULL, -- 32 bytes base64
    secretKey varchar(32) NOT NULL
);

CREATE UNIQUE INDEX reg_user_email_uidx ON ${auth.schemaName}.registered_user (email);