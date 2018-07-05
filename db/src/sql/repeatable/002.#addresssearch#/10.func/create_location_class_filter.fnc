CREATE OR REPLACE FUNCTION address_search.create_location_class_filter(
  a_search_level IN address_search.T_SEARCH_LEVEL
) RETURNS address_search.T_LOCATION_CLASS []
AS $$
DECLARE
  l_result address_search.T_LOCATION_CLASS [] := ARRAY [] :: address_search.T_LOCATION_CLASS [];
BEGIN
  IF a_search_level IN ('REGION_ONLY', 'NAMED_LOCATION', 'ALL')  THEN
    l_result := array_append(l_result, 'R' :: address_search.T_LOCATION_CLASS);
  END IF;

  IF a_search_level IN ('STREET_ONLY', 'NAMED_LOCATION', 'ALL')  THEN
    l_result := array_append(l_result, 'S' :: address_search.T_LOCATION_CLASS);
  END IF;

  IF a_search_level IN ('BUILDING_ONLY', 'ALL')  THEN
    l_result := array_append(l_result, 'B' :: address_search.T_LOCATION_CLASS);
  END IF;

  RETURN l_result;
END;
$$ LANGUAGE plpgsql;
/