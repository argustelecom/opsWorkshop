DO $$
DECLARE
  required_extensions VARCHAR [] := '{plpgsql, jsquery, btree_gin, pg_trgm, postgres_fdw, postgis, postgis_topology}' :: VARCHAR [];
  required_extension  VARCHAR;
  installed_version   VARCHAR;
BEGIN
  FOREACH required_extension IN ARRAY required_extensions LOOP
    SELECT extversion
    INTO installed_version
    FROM pg_catalog.pg_extension
    WHERE lower(extname) = required_extension;

    IF installed_version IS NULL THEN
      RAISE EXCEPTION 'Required extension "%" is not installed', required_extension;
    ELSE
      RAISE NOTICE 'Found required extension "%" with version "%"', required_extension, installed_version;
    END IF;
  END LOOP;
END;
$$ LANGUAGE plpgsql;
/