create table ops.team (
	id BIGINT NOT NULL, 
	jira_component varchar(255), 
	name varchar(255), 
  CONSTRAINT pk_team PRIMARY KEY (id));