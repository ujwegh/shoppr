--liquibase formatted sql

--changeset akovylin:HB-395-light-auth-service logicalFilePath:/

CREATE SCHEMA ${auth.schemaName};

CREATE TABLE ${auth.schemaName}.registered_client
(
  id                        serial PRIMARY KEY,
  reg_date_timestamp        timestamp WITH TIME ZONE NOT NULL,
  sovcom_client_id          varchar(64)              NOT NULL,
  login                     varchar(128)             NOT NULL,
  password_salt             varchar(24)              NOT NULL, -- 16 bytes base64
  password_hash             varchar(44)              NOT NULL, -- 32 bytes base64
  access_status             varchar(32)              NOT NULL, -- enum
  failed_password_count     int                      NOT NULL DEFAULT 0,
  last_failed_password_date timestamp WITH TIME ZONE NULL,
  failed_otp_count          int                      NOT NULL DEFAULT 0,
  last_failed_otp_timestamp timestamp WITH TIME ZONE NULL,
  block_duration            int                      NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX reg_client_login_uidx ON ${auth.schemaName}.registered_client (login);

CREATE TABLE ${auth.schemaName}.terminal
(
  id                     serial PRIMARY KEY,
  registered_client_id   int                      NOT NULL,
  reg_date_timestamp     timestamp WITH TIME ZONE NOT NULL,
  terminal_type          varchar(32)              NOT NULL, -- enum
  terminal_id            varchar(128)             NOT NULL,
  terminal_status        varchar(32)              NOT NULL, -- enum
  otp_salt               varchar(24)              NULL,     -- 16 bytes base64
  otp_hash               varchar(44)              NULL,     -- 32 bytes base64
  otp_creation_timestamp timestamp WITH TIME ZONE NULL,
  pin_salt               varchar(24)              NULL,     -- 16 bytes base64
  pin_hash               varchar(44)              NULL,     -- 32 bytes base64
  bio_key_salt           varchar(24)              NULL,     -- 16 bytes base64
  bio_key_hash           varchar(44)              NULL,     -- 32 bytes base64
  last_ip                varchar(1024)            NULL,
  last_access_timestamp  timestamp WITH TIME ZONE NULL,
  device_info            varchar(256)             NULL,
  failed_pin_count       int                      NOT NULL DEFAULT 0,
  failed_bio_count       int                      NOT NULL DEFAULT 0
);

CREATE INDEX terminal_termid_idx ON ${auth.schemaName}.terminal (terminal_id);
CREATE INDEX terminal_regclientid_idx ON ${auth.schemaName}.terminal (registered_client_id);
CREATE UNIQUE INDEX terminal_regclientid_termid_uidx ON ${auth.schemaName}.terminal (registered_client_id, terminal_id);

CREATE TABLE ${auth.schemaName}.ongoing_client_reg
(
  id                        serial PRIMARY KEY,
  start_date_timestamp      timestamp WITH TIME ZONE NOT NULL,
  next_step                 varchar(32)              NOT NULL, -- enum
  pan_prefix                varchar(4)               NOT NULL,
  pan_suffix                varchar(4)               NOT NULL,
  pan_hash                  varchar(44)              NOT NULL, -- 32 bytes base64
  terminal_type             varchar(32)              NULL,     -- enum
  terminal_id               varchar(128)             NULL,
  access_status             varchar(32)              NOT NULL, -- enum
  sovcom_client_id          varchar(64)              NULL,
  otp_salt                  varchar(24)              NULL,     -- 16 bytes base64
  otp_hash                  varchar(44)              NULL,     -- 32 bytes base64
  otp_creation_timestamp    timestamp WITH TIME ZONE NULL,
  failed_otp_count          int                      NOT NULL DEFAULT 0,
  last_failed_otp_timestamp timestamp WITH TIME ZONE NULL,
  login                     varchar(128)             NULL,
  password_salt             varchar(24)              NULL,     -- 16 bytes base64
  password_hash             varchar(44)              NULL,     -- 32 bytes base64
  block_duration            int                      NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX ongoingreg_pan_uidx ON ${auth.schemaName}.ongoing_client_reg (pan_prefix, pan_suffix, pan_hash);
CREATE INDEX ongoingreg_termid_idx ON ${auth.schemaName}.ongoing_client_reg (terminal_id);

CREATE TABLE ${auth.schemaName}.distributed_locks
(
  lock_key           varchar(300) PRIMARY KEY,
  uuid               varchar(60) NOT NULL,
  creation_date_time timestamp   NOT NULL
);

CREATE UNIQUE INDEX distrlocks_lock_key_uidx ON ${auth.schemaName}.distributed_locks (lock_key);