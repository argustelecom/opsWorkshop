CREATE TABLE ops.product
(
    id bigint PRIMARY KEY NOT NULL,
    name varchar(128) NOT NULL,
    jira_project varchar(8),
    jira_component varchar(128)
);