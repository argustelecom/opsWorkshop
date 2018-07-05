CREATE OR REPLACE FUNCTION address_search.get_location_parent_info(
  a_start_location_id IN BIGINT
) RETURNS address_search.T_LOCATION_PARENT_INFO
AS $$
DECLARE
  l_location_rec      system.location%ROWTYPE;
  l_parent_id         BIGINT;
  l_type_name         VARCHAR;

  l_tree_id           BIGINT [] := '{}';
  l_tree_level        INTEGER := 0;
  l_tree_search_name  VARCHAR := '';
  l_tree_display_name VARCHAR := '';

  l_parent_info       address_search.T_LOCATION_PARENT_INFO;
BEGIN
  SELECT parent_id
  INTO l_parent_id
  FROM system.location
  WHERE id = a_start_location_id;

  WHILE l_parent_id IS NOT NULL LOOP

    SELECT l.*
    INTO l_location_rec
    FROM system.location l
    WHERE id = l_parent_id;

    IF l_location_rec IS NULL OR l_location_rec.dtype = 'Country'
    THEN
      l_parent_id := NULL;
    ELSE
      l_type_name := CASE l_location_rec.dtype
                     WHEN 'Region' THEN coalesce(address_search.get_region_type_short_name(l_location_rec.id), '')
                     WHEN 'Street' THEN coalesce(address_search.get_street_type_short_name(l_location_rec.id), '')
                     ELSE ''
                     END;

      l_tree_id := array_prepend(l_location_rec.id, l_tree_id);

      l_tree_search_name := trim(concat(address_search.normalize(l_location_rec.name), ' ', l_tree_search_name));

      l_tree_display_name := trim(
          trim(concat(trim(concat(l_location_rec.name, ' ', l_type_name)), ', ', l_tree_display_name))
          , ','
      );

      l_tree_level := l_tree_level + 1;

      l_parent_id := l_location_rec.parent_id;
    END IF;
  END LOOP;

  SELECT INTO l_parent_info
    l_tree_id           AS tree_id,
    l_tree_level        AS tree_level,
    l_tree_search_name  AS tree_search_name,
    l_tree_display_name AS tree_display_name;

  RETURN l_parent_info;
END;
$$ LANGUAGE plpgsql
STABLE COST 50;
/