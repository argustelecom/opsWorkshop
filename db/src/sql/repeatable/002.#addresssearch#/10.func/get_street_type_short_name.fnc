CREATE OR REPLACE FUNCTION address_search.get_street_type_short_name(
  a_street_id IN BIGINT
) RETURNS VARCHAR
AS $$
DECLARE
	short_name VARCHAR;
BEGIN
	SELECT lt.short_name
	FROM system.street s
	  JOIN system.location_type lt ON s.type_id = lt.id
	WHERE s.id = a_street_id INTO short_name;
	RETURN short_name;
END;
$$ LANGUAGE plpgsql
STABLE
RETURNS NULL ON NULL INPUT;
/