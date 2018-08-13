CREATE TABLE ops.artifact
(
    id bigint PRIMARY KEY NOT NULL,
    name varchar(128) NOT NULL,
    git_repository varchar(128)
);