CREATE OR REPLACE FUNCTION address_search.get_region_type_name(
  a_region_id IN BIGINT
)
  RETURNS VARCHAR
AS $$
DECLARE
  region_name VARCHAR;
BEGIN
  SELECT lt.name
  FROM system.region r
    JOIN system.location_type lt ON r.type_id = lt.id
  WHERE r.id = a_region_id
  INTO region_name;
  RETURN region_name;
END;
$$
LANGUAGE plpgsql
STABLE
RETURNS NULL ON NULL INPUT;
/