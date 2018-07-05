CREATE OR REPLACE FUNCTION system.is_value_unique(index_schema VARCHAR,
                                                  index_table  VARCHAR,
                                                  instance_id  BIGINT,
                                                  property_id  BIGINT,
                                                  value        VARCHAR)
  RETURNS SETOF BOOLEAN
AS $$
BEGIN
  RETURN QUERY EXECUTE format(
      'SELECT count(value) < 1 AS result FROM %I.%I WHERE instance_id != $1 AND property_id = $2 AND value = $3',
      index_schema, index_table)
  USING instance_id, property_id, value;
END;
$$
LANGUAGE plpgsql;
/