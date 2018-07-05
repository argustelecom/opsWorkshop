CREATE OR REPLACE FUNCTION address_search.match_regions(
  a_tokens     IN VARCHAR [],
  a_level      IN INT,
  a_limit      IN INT DEFAULT 5,
  a_start_with IN BIGINT DEFAULT -1
) RETURNS address_search.T_MATCH []
AS $$
DECLARE
  l_result       address_search.T_MATCH [] := ARRAY [] :: address_search.T_MATCH [];
  l_query        address_search.T_HIERARCHICAL_QUERY;
  l_bouded_query BOOLEAN := a_start_with > -1;
BEGIN
  IF array_length(a_tokens, 1) > 0 THEN
    l_query := address_search.create_hquery(a_tokens, a_level);

    SELECT array_agg(m :: address_search.T_MATCH)
    FROM (SELECT
            r.id,
            r.tree_level,
            l_query.tokens
          FROM address_search.region_idx r
          WHERE r.tree_search_name LIKE l_query.hquery
                AND r.search_name LIKE l_query.lquery
                AND (NOT l_bouded_query OR ARRAY [a_start_with] :: BIGINT [] <@ r.tree_id)
          LIMIT a_limit) m
    INTO l_result;
  END IF;

  RETURN coalesce(l_result, ARRAY [] :: address_search.T_MATCH []);
END;
$$ LANGUAGE plpgsql;
/