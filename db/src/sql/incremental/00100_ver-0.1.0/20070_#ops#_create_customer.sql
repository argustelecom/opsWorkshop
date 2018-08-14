CREATE TABLE ops.customer
(
    id int PRIMARY KEY NOT NULL,
    name varchar(128) NOT NULL,
    jira_name varchar(128),
    jira_project varchar(8)
);
CREATE UNIQUE INDEX customer_name_uindex ON ops.customer (name);