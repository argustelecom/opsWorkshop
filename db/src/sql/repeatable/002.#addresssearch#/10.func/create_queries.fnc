CREATE OR REPLACE FUNCTION address_search.create_queries(
  a_tokens  IN VARCHAR [],
  a_reverse IN BOOLEAN DEFAULT FALSE
) RETURNS address_search.T_QUERY []
AS $$
DECLARE
  l_level  INT := 1;
  l_count  INT := array_length(a_tokens, 1);

  l_result address_search.T_QUERY [] := ARRAY [] :: address_search.T_QUERY [];
BEGIN
  WHILE l_level <= l_count LOOP
    l_result := array_prepend(address_search.create_query(a_tokens, l_level, a_reverse), l_result);
    l_level := l_level + 1;
  END LOOP;

  RETURN l_result;
END;
$$ LANGUAGE plpgsql;
/