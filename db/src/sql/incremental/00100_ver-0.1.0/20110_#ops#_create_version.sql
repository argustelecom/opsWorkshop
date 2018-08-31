create table ops.version (
		id BIGINT NOT NULL,
		version_name varchar(255) NOT NULL,
		fixation_date date NOT NULL
		shipment_date date NOT NULL
		jira_task varchar(255),
		version_status varchar(255) NOT NULL
    CONSTRAINT pk_version PRIMARY KEY (id)
    );