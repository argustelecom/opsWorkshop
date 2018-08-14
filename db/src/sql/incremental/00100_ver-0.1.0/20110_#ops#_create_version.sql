create table ops.version (
		id BIGINT NOT NULL,
		version_name varchar(255),
    CONSTRAINT pk_version PRIMARY KEY (id)
    );