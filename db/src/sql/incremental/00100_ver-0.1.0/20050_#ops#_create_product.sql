create table ops.product (
		id BIGINT NOT NULL,
		name varchar(255),
		team_id BIGINT,
    CONSTRAINT pk_product PRIMARY KEY (id),
	CONSTRAINT fk_product_to_team FOREIGN KEY (team_id) REFERENCES ops.team (id)
    );