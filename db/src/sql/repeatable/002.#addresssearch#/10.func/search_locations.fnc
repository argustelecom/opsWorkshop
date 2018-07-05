CREATE OR REPLACE FUNCTION address_search.search_locations(
  a_raw_input  IN VARCHAR,
  a_limit      IN INT DEFAULT 10,
  a_classes    IN address_search.T_SEARCH_LEVEL DEFAULT 'ALL',
  a_start_with IN BIGINT DEFAULT -1
) RETURNS SETOF address_search.T_SEARCH_RESULT
AS $$
DECLARE
  l_search_tokens         VARCHAR [];
  l_false_positive_filter VARCHAR;
  l_location_class_filter address_search.T_LOCATION_CLASS [] := ARRAY [] :: address_search.T_LOCATION_CLASS [];
  l_matches               address_search.T_MATCH [];
  l_building_matches      address_search.T_MATCH [];
  l_building_iterator     RECORD;
BEGIN
  l_search_tokens := address_search.parse_raw_input(a_raw_input);
  IF l_search_tokens IS NULL OR array_length(l_search_tokens, 1) < 1 THEN
    RETURN;
  END IF;

  l_false_positive_filter := address_search.create_false_positive_filter(a_raw_input);
  l_location_class_filter := address_search.create_location_class_filter(a_classes);

  -- особая оптимизация для поиска одних лишь регионов. Т.к. искать больше ничего не нужно, то можно сразу вернуть
  -- запрос с искомыми значениями. Все остальные варианты поисков выполняются иерархично, с учетом поисков предыдущих
  -- более высокоуровневых элементов
  IF a_classes = 'REGION_ONLY' THEN
    RETURN QUERY SELECT
                   ci.id :: BIGINT                                                      AS location_id,
                   ci.parent_id :: BIGINT                                               AS location_parent_id,
                   ci.class :: address_search.T_LOCATION_CLASS                          AS location_class,
                   ci.tree_id :: BIGINT []                                              AS tree_id,
                   ci.tree_level :: INTEGER                                             AS tree_level,
                   ci.tree_display_name :: VARCHAR                                      AS tree_display_name,
                   similarity(ci.tree_display_name, l_false_positive_filter) :: NUMERIC AS search_rank,
                   mr.tokens :: VARCHAR []                                              AS search_tokens
                 FROM address_search.common_idx ci,
                       unnest(address_search.match_regions(
                                  l_search_tokens,
                                  array_length(l_search_tokens, 1),
                                  a_limit * 2,
                                  a_start_with
                              )) mr (id, tree_level, tokens)
                 WHERE ci.id = mr.id
                 ORDER BY tree_level, search_rank DESC
                 LIMIT a_limit;
    RETURN;
  END IF;

  l_matches := address_search.match_named_locations(l_search_tokens, a_limit * 2, a_start_with);

  IF a_classes IN ('BUILDING_ONLY', 'ALL') THEN
    FOR l_building_iterator IN SELECT
                                 address_search.create_bquery(m.tokens, l_search_tokens) AS bquery,
                                 m.id                                                    AS parent_id
                               FROM unnest(l_matches) m (id, tree_level, tokens)
                               WHERE array_length(m.tokens, 1) < array_length(l_search_tokens, 1)
                               ORDER BY array_length(m.tokens, 1) DESC LOOP

      SELECT array_agg(b :: address_search.T_MATCH)
      FROM (
             SELECT
               ci.id,
               ci.tree_level,
               l_search_tokens AS tokens
             FROM address_search.common_idx ci
             WHERE ci.class = 'B'
                   AND ci.parent_id = l_building_iterator.parent_id
                   AND ci.search_name LIKE l_building_iterator.bquery
             ORDER BY ci.class, ci.parent_id, ci.search_name
             LIMIT a_limit / 2
           ) b
      INTO l_building_matches;

      l_matches := array_cat(l_matches, l_building_matches);

    END LOOP;
  END IF;

  RETURN QUERY SELECT
                 ci.id :: BIGINT                                                      AS location_id,
                 ci.parent_id :: BIGINT                                               AS location_parent_id,
                 ci.class :: address_search.T_LOCATION_CLASS                          AS location_class,
                 ci.tree_id :: BIGINT []                                              AS tree_id,
                 ci.tree_level :: INTEGER                                             AS tree_level,
                 ci.tree_display_name :: VARCHAR                                      AS tree_display_name,
                 similarity(ci.tree_display_name, l_false_positive_filter) :: NUMERIC AS search_rank,
                 m.tokens :: VARCHAR []                                               AS search_tokens
               FROM address_search.common_idx ci, unnest(l_matches) m (id, tree_level, tokens)
               WHERE ci.id = m.id
                     AND ci.tree_display_name ILIKE l_false_positive_filter
                     AND ci.class = ANY (l_location_class_filter)
               ORDER BY array_length(m.tokens, 1) DESC, tree_level, search_rank DESC
               LIMIT a_limit;
END;
$$ LANGUAGE plpgsql
RETURNS NULL ON NULL INPUT;
/