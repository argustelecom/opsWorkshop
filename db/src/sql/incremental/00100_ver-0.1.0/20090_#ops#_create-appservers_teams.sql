create table ops.appservers_teams (
		appserver_id BIGINT NOT NULL,
		team_id BIGINT NOT NULL,
	CONSTRAINT pk_appservers_teams PRIMARY KEY (appserver_id, team_id),
	CONSTRAINT fk_team FOREIGN KEY (team_id) REFERENCES ops.team (id),
	CONSTRAINT fk_application_sever FOREIGN KEY (appserver_id) REFERENCES ops.application_server (id)
    );