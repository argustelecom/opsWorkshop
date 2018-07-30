create table ops.teammate (
	id BIGINT NOT NULL, 
  fio varchar(255),
	jira_name varchar(255), 
  email varchar(255),
  delivery_watching_type varchar(32), 
  version_watching_type varchar(32),
  CONSTRAINT pk_teammate PRIMARY KEY (id));