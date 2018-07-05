CREATE OR REPLACE FUNCTION address_search.match_streets(
  a_tokens     IN VARCHAR [],
  a_limit      IN INT DEFAULT 5,
  a_start_with IN BIGINT DEFAULT -1
) RETURNS address_search.T_MATCH []
AS $$
DECLARE
  l_result_local address_search.T_MATCH [] := ARRAY [] :: address_search.T_MATCH [];
  l_result_total address_search.T_MATCH [] := ARRAY [] :: address_search.T_MATCH [];
  l_query        address_search.T_QUERY;
  l_queries      address_search.T_QUERY [];
  l_bouded_query BOOLEAN := a_start_with > -1;
BEGIN
  IF array_length(a_tokens, 1) > 0 THEN
    l_queries := address_search.create_queries(a_tokens);

    FOREACH l_query IN ARRAY l_queries LOOP
      SELECT array_agg(m :: address_search.T_MATCH)
      FROM (SELECT
              s.id,
              s.tree_level,
              l_query.tokens
            FROM address_search.street_idx s
            WHERE s.search_name LIKE l_query.query
                  AND (NOT l_bouded_query OR ARRAY [a_start_with] :: BIGINT [] <@ s.tree_id)
            LIMIT a_limit) m
      INTO l_result_local;

      l_result_total := array_cat(l_result_total, coalesce(l_result_local, ARRAY [] :: address_search.T_MATCH []));
    END LOOP;
  END IF;

  RETURN l_result_total;
END;
$$ LANGUAGE plpgsql;
/