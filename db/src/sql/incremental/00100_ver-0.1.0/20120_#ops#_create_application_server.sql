create table ops.application_server (
		id BIGINT NOT NULL,
        name varchar(128) NOT NULL,
        build_number varchar(255),
        comment varchar(512),
        host varchar(255) NOT NULL,
        appserver_install_path varchar(255) NOT NULL,
        port_offset integer NOT NULL,
        status varchar(255) NOT NULL,
        url_address varchar(255),
        version_id BIGINT,
        customer_id BIGINT,
        usage_type_id BIGINT,
	CONSTRAINT pk_application_server PRIMARY KEY (id),
	CONSTRAINT fk_application_server_to_customer FOREIGN KEY (customer_id) REFERENCES ops.customer (id),
	CONSTRAINT fk_application_server_to_usage_type FOREIGN KEY (usage_type_id) REFERENCES ops.usage_type (id),
	CONSTRAINT fk_application_server_to_version FOREIGN KEY (version_id) REFERENCES ops.version (id)
	);