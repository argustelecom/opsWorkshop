CREATE OR REPLACE FUNCTION address_search.match_named_locations(
  a_tokens     IN VARCHAR [],
  a_limit      IN INT DEFAULT 15,
  a_start_with IN BIGINT DEFAULT -1
) RETURNS address_search.T_MATCH []
AS $$
DECLARE
  -- 10 подобрано экспериментальным путем и дает достаточно хорошие результаты
  c_limit_local  INT := 10;

  l_region_level INT := array_length(coalesce(a_tokens, ARRAY [] :: VARCHAR []), 1);
  l_street_level INT := l_region_level;
  l_result_local address_search.T_MATCH [] := ARRAY [] :: address_search.T_MATCH [];
  l_result_total address_search.T_MATCH [] := ARRAY [] :: address_search.T_MATCH [];

  l_result_loop  address_search.T_MATCH;
  l_result_last  address_search.T_MATCH;
BEGIN
  IF l_region_level > 0 THEN
    l_result_total := array_cat(
        l_result_total,
        address_search.match_regions(a_tokens, l_region_level, c_limit_local, a_start_with)
    );
    l_region_level := l_region_level - 1;

    WHILE l_street_level > 0 LOOP

      IF (l_region_level > 0) THEN
        l_result_local := address_search.match_regions(a_tokens, l_region_level, c_limit_local, a_start_with);
        l_result_total := array_cat(l_result_total, l_result_local);
        l_result_total := array_cat(
            l_result_total,
            address_search.match_streets_within_parents(l_result_local, a_tokens, c_limit_local)
        );
      ELSE
        l_result_total := array_cat(
            l_result_total,
            address_search.match_streets(a_tokens, c_limit_local, a_start_with)
        );
      END IF;

      l_region_level := l_region_level - 1;
      l_street_level := l_street_level - 1;
      l_result_local := ARRAY [] :: address_search.T_MATCH [];
    END LOOP;

    -- лаконичный distinct on пришлось заменить на это, потому что сортировать нужно по
    -- максимальному вхождению токенов поиска, а уникальность обеспечивать по id
    -- distinct on при этом хочет, чтобы его выражение соответствовало выражению сортировки
    -- что дает неуникальные результаты, найденные по разным токенам
    -- FIXME придумать, как сделать нужную уникальность средствами SQL
    l_result_local := l_result_total;
    l_result_total := ARRAY [] :: address_search.T_MATCH [];
    FOR l_result_loop IN SELECT nu.*
                         FROM unnest(l_result_local) nu (id, tree_level, tokens)
                         ORDER BY nu.id, array_length(nu.tokens, 1) DESC
    LOOP
      IF l_result_last IS NULL OR l_result_last.id != l_result_loop.id THEN
        l_result_last := l_result_loop;
        l_result_total := array_append(l_result_total, l_result_last);
      END IF;
    END LOOP;

    -- итоговый результат
    SELECT array_agg(l :: address_search.T_MATCH)
    FROM (SELECT *
          FROM unnest(l_result_total) u (id, tree_level, tokens)
          ORDER BY array_length(u.tokens, 1) DESC, u.tree_level
          LIMIT a_limit) l
    INTO l_result_total;
  END IF;

  RETURN coalesce(l_result_total, ARRAY [] :: address_search.T_MATCH []);
END;
$$ LANGUAGE plpgsql;
/