DROP FUNCTION IF EXISTS datamart_sys.extract_entity_id( VARCHAR, VARCHAR );

CREATE OR REPLACE FUNCTION datamart_sys.extract_entity_id(
  a_entity_ref      IN VARCHAR,
  a_entity_perfixes IN VARCHAR []
) RETURNS BIGINT
AS $$
DECLARE
  l_entity_ref VARCHAR;
  l_prefix     VARCHAR;
BEGIN
  FOREACH l_prefix IN ARRAY a_entity_perfixes LOOP
    l_entity_ref := trim(trim(a_entity_ref), '"');
    IF l_entity_ref IS NOT NULL AND strpos(l_entity_ref, l_prefix) = 1  THEN
      RETURN substr(l_entity_ref, length(l_prefix) + 1) :: BIGINT;
    END IF;
  END LOOP;

  RETURN NULL;
END;
$$
LANGUAGE plpgsql STABLE COST 1
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/