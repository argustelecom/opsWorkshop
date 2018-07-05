CREATE OR REPLACE FUNCTION system.unmake_property_unique(index_schema VARCHAR, index_table VARCHAR,
                                                         property_id  BIGINT)
  RETURNS BOOLEAN
AS $$
BEGIN
  EXECUTE format('DELETE FROM %I.%I WHERE property_id = $1', index_schema, index_table)
  USING property_id;
  RETURN TRUE;
END;
$$
LANGUAGE plpgsql;
/