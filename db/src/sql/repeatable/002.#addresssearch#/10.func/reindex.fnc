CREATE OR REPLACE FUNCTION address_search.reindex(
  a_start_with IN BIGINT
) RETURNS BOOLEAN
AS $$
DECLARE
BEGIN
  LOCK TABLE address_search.common_idx;

  DELETE
  FROM address_search.common_idx
  WHERE ARRAY [a_start_with] :: BIGINT [] <@ tree_id;

  DELETE
  FROM address_search.region_idx
  WHERE ARRAY [a_start_with] :: BIGINT [] <@ tree_id;

  DELETE
  FROM address_search.street_idx
  WHERE ARRAY [a_start_with] :: BIGINT [] <@ tree_id;

  INSERT INTO address_search.common_idx (id, parent_id, type_id, class, search_name, tree_id, tree_level, tree_search_name, tree_display_name)
    WITH RECURSIVE location_tree (id, parent_id, type_id, class, search_name, tree_id, tree_level, tree_search_name, tree_display_name) AS (
      (
        SELECT
          l.id,
          l.parent_id,
          l.type_id,
          l.class,
          l.search_name,
          array_append(p.tree_id, l.id)                               AS tree_id,
          p.tree_level + 1                                            AS tree_level,
          trim(concat(p.tree_search_name, ' ', l.search_name))        AS tree_search_name,
          trim(concat(p.tree_display_name, ', ', l.typed_name), ', ') AS tree_display_name
        FROM address_search.location_vw l,
              address_search.get_location_parent_info(l.id) p
              (tree_id, tree_level, tree_search_name, tree_display_name )
        WHERE l.id = a_start_with
      )
      UNION ALL
      (
        SELECT
          l.id,
          l.parent_id,
          l.type_id,
          l.class,
          l.search_name,
          array_append(ltr.tree_id, l.id)                                     AS tree_id,
          ltr.tree_level + 1                                                  AS tree_level,
          trim(concat(ltr.tree_search_name, ' ', l.search_name))              AS tree_search_name,
          rtrim(trim(concat(ltr.tree_display_name, ', ', l.typed_name)), ',') AS tree_display_name
        FROM address_search.location_vw l, location_tree ltr
        WHERE l.parent_id = ltr.id
      )
    )
    SELECT
      id,
      parent_id,
      type_id,
      class,
      search_name,
      tree_id,
      tree_level,
      tree_search_name,
      tree_display_name
    FROM location_tree;

  INSERT INTO address_search.region_idx (id, parent_id, type_id, search_name, tree_id, tree_level, tree_search_name, tree_display_name)
    SELECT
      id,
      parent_id,
      type_id,
      search_name,
      tree_id,
      tree_level,
      tree_search_name,
      tree_display_name
    FROM address_search.common_idx
    WHERE ARRAY [a_start_with] :: BIGINT [] <@ tree_id
          AND class = 'R';

  INSERT INTO address_search.street_idx (id, parent_id, type_id, search_name, tree_id, tree_level, tree_search_name, tree_display_name)
    SELECT
      id,
      parent_id,
      type_id,
      search_name,
      tree_id,
      tree_level,
      tree_search_name,
      tree_display_name
    FROM address_search.common_idx
    WHERE ARRAY [a_start_with] :: BIGINT [] <@ tree_id
          AND class = 'S';

  RETURN TRUE;
END;
$$ LANGUAGE plpgsql
RETURNS NULL ON NULL INPUT;
/