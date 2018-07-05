CREATE OR REPLACE FUNCTION address_search.get_region_type_short_name(
  a_region_id IN BIGINT
) RETURNS VARCHAR
AS $$
DECLARE
    short_name VARCHAR;
BEGIN
    SELECT lt.short_name
	FROM system.region r
	  JOIN system.location_type lt ON r.type_id = lt.id
	WHERE r.id = a_region_id INTO short_name;
	RETURN short_name;
END;
$$ LANGUAGE plpgsql
STABLE
RETURNS NULL ON NULL INPUT;
/