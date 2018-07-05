CREATE OR REPLACE FUNCTION address_search.get_street_type_name(
  a_street_id IN BIGINT
) RETURNS VARCHAR
AS $$
DECLARE
	name VARCHAR;
BEGIN
	SELECT lt.name
	FROM system.street s
	  JOIN system.location_type lt ON s.type_id = lt.id
	WHERE s.id = a_street_id INTO name;
	RETURN name;
END;
$$ LANGUAGE plpgsql
STABLE
RETURNS NULL ON NULL INPUT;
/