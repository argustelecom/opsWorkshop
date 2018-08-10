create table ops.usage_type (
		id BIGINT NOT NULL,
		name varchar(255),
		abbreviation varchar(16),        
	CONSTRAINT pk_usage_type PRIMARY KEY (id)
    );