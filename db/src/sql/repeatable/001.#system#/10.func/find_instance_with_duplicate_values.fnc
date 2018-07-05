CREATE OR REPLACE FUNCTION system.find_instance_with_duplicate_values(instance_schema    VARCHAR,
                                                                      instance_table     VARCHAR,
                                                                      id_column          VARCHAR,
                                                                      properties_column  VARCHAR,
                                                                      property_qualifier VARCHAR)
  RETURNS SETOF BIGINT
AS $$
DECLARE
BEGIN
  RETURN QUERY EXECUTE format(
      'SELECT
        %I
      FROM %I.%I
      WHERE %I ->> $1 IN (
        SELECT %I ->> $1
        FROM %I.%I
        GROUP BY %I ->> $1
        HAVING count(jsonb_strip_nulls(%I) -> $1) > 1)', id_column, instance_schema, instance_table, properties_column,
      properties_column, instance_schema,
      instance_table,
      properties_column,
      properties_column)
  USING property_qualifier;
END;
$$
LANGUAGE plpgsql;
/