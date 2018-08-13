CREATE TABLE ops.usage_type
(
    key varchar(128) PRIMARY KEY NOT NULL,
    name varchar(128) NOT NULL
);
CREATE UNIQUE INDEX usage_type_name_uindex ON ops.usage_type (name);