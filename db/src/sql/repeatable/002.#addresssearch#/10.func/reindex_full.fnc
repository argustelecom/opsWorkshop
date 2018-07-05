CREATE OR REPLACE FUNCTION address_search.reindex_full()
  RETURNS BOOLEAN
AS $$
DECLARE
  l_top_locations BIGINT [];
BEGIN
  LOCK TABLE address_search.common_idx;

  PERFORM address_search.disable_all_idx();

  TRUNCATE TABLE address_search.common_idx;
  TRUNCATE TABLE address_search.region_idx;
  TRUNCATE TABLE address_search.street_idx;

  SELECT coalesce(array_agg(top.id), ARRAY [] :: BIGINT [])
  FROM (
         SELECT top_within_country.*
         FROM
           (
             SELECT id
             FROM system.location l
             WHERE dtype = 'Country'
           ) c,
           LATERAL (
			   SELECT r.id
			   FROM system.location r
			   WHERE r.parent_id = c.id
           ) top_within_country
         UNION
         SELECT top_regions.id
         FROM system.location top_regions
         WHERE parent_id IS NULL
               AND dtype = 'Region'
       ) AS top
  INTO l_top_locations;

  INSERT INTO address_search.common_idx (id, parent_id, type_id, class, search_name, tree_id, tree_level, tree_search_name, tree_display_name)
    WITH RECURSIVE location_tree (id, parent_id, type_id, class, search_name, tree_id, tree_level, tree_search_name, tree_display_name) AS (
      (
        SELECT
          l.id,
          l.parent_id,
          l.type_id,
          l.class,
          l.search_name,
          ARRAY [l.id]  AS tree_id,
          1             AS tree_level,
          l.search_name AS tree_search_name,
          l.typed_name  AS tree_display_name
        FROM address_search.location_vw l
        WHERE l.id = ANY (l_top_locations)
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
    WHERE class = 'R';

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
    WHERE class = 'S';

  PERFORM address_search.enable_all_idx();

  RETURN TRUE;
END;
$$ LANGUAGE plpgsql;
/