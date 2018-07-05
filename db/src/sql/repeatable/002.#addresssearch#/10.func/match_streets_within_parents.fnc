CREATE OR REPLACE FUNCTION address_search.match_streets_within_parents(
  a_parents IN address_search.T_MATCH [],
  a_tokens  IN VARCHAR [],
  a_limit   IN INT DEFAULT 5
) RETURNS address_search.T_MATCH []
AS $$
DECLARE
  l_result_local address_search.T_MATCH [] := ARRAY [] :: address_search.T_MATCH [];
  l_result_total address_search.T_MATCH [] := ARRAY [] :: address_search.T_MATCH [];
  l_queries      address_search.T_QUERY [];
  l_query        address_search.T_QUERY;

  l_match_loop   RECORD;
  l_tokens_diff  VARCHAR [];
  l_tokens_match VARCHAR [];
BEGIN
  IF a_parents IS NOT NULL AND array_length(a_parents, 1) > 0 AND array_length(a_tokens, 1) > 0 THEN

    FOR l_match_loop IN SELECT
                          tokens,
                          array_agg(u.id) :: BIGINT [] AS parent_ids
                        FROM unnest(a_parents) u (id, tree_level, tokens)
                        GROUP BY tokens
    LOOP
      l_tokens_diff := address_search.diff_search_tokens(l_match_loop.tokens, a_tokens);
      l_queries := address_search.create_queries(l_tokens_diff);

      FOREACH l_query IN ARRAY l_queries LOOP
        l_tokens_match := array_cat(l_match_loop.tokens, l_query.tokens);

        SELECT array_agg(m :: address_search.T_MATCH)
        FROM (SELECT
                s.id,
                s.tree_level,
                l_tokens_match
              FROM address_search.street_idx s
              WHERE s.parent_id = ANY (l_match_loop.parent_ids)
                    AND s.search_name LIKE l_query.query
              LIMIT a_limit) m
        INTO l_result_local;

        l_result_total := array_cat(l_result_total, l_result_local);
      END LOOP;
    END LOOP;
  END IF;

  RETURN coalesce(l_result_total, ARRAY [] :: address_search.T_MATCH []);
END;
$$ LANGUAGE plpgsql;
/