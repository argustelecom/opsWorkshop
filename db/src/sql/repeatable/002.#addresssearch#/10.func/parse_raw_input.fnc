CREATE OR REPLACE FUNCTION address_search.parse_raw_input(
  a_raw_input         IN VARCHAR,
  a_filter_stop_words IN BOOLEAN DEFAULT TRUE
) RETURNS VARCHAR []
AS $$
DECLARE
	arr VARCHAR [];
BEGIN
	WITH tokens AS (
		SELECT regexp_split_to_table AS entry
		FROM regexp_split_to_table(lower(a_raw_input), '[^[:alnum:]_-]+'))
	SELECT array_agg(trim(t.entry)) :: VARCHAR []
	FROM tokens t
	WHERE t.entry IS NOT NULL
		  AND trim(t.entry) != ''
		  AND (NOT a_filter_stop_words OR NOT exists(SELECT NULL
													 FROM system.location_type lt
													 WHERE lower(lt.short_name) = t.entry)) INTO arr;
	RETURN arr;
END;
$$ LANGUAGE plpgsql
STABLE COST 15
RETURNS NULL ON NULL INPUT;
/