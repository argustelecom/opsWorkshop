CREATE OR REPLACE FUNCTION address_search.normalize(
  a_raw_input         IN VARCHAR,
  a_filter_stop_words IN BOOLEAN DEFAULT TRUE
) RETURNS VARCHAR
AS $$
BEGIN
	RETURN array_to_string(address_search.parse_raw_input(a_raw_input, a_filter_stop_words), ' ') :: VARCHAR;
END;
$$ LANGUAGE plpgsql
STABLE COST 15
RETURNS NULL ON NULL INPUT;
/