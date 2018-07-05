CREATE OR REPLACE FUNCTION system.remove_property_index(index_schema VARCHAR, index_table VARCHAR, instance_id BIGINT)
  RETURNS BOOLEAN
AS $$
BEGIN
  EXECUTE format('DELETE FROM %I.%I WHERE instance_id = $1', index_schema, index_table)
  USING instance_id;
  RETURN TRUE;
END;
$$
LANGUAGE plpgsql;
/