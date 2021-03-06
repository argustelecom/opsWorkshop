CREATE EXTENSION IF NOT EXISTS jsquery SCHEMA public;
CREATE EXTENSION IF NOT EXISTS btree_gin SCHEMA public;
CREATE EXTENSION IF NOT EXISTS pg_trgm SCHEMA public;
CREATE EXTENSION IF NOT EXISTS postgis SCHEMA public;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
CREATE EXTENSION IF NOT EXISTS postgres_fdw SCHEMA public;

ALTER SCHEMA topology OWNER TO argus_sys;
GRANT USAGE ON FOREIGN DATA WRAPPER postgres_fdw TO argus_sys;
