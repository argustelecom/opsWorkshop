CREATE OR REPLACE FUNCTION address_search.create_hquery(
  a_tokens IN VARCHAR [],
  a_level  IN INT DEFAULT 1
) RETURNS address_search.T_HIERARCHICAL_QUERY
AS $$
DECLARE
  l_query_tokens VARCHAR [] := ARRAY [] :: VARCHAR [];
  l_hquery       VARCHAR := '%';
  l_lquery       VARCHAR := '%';
  l_length       INT := array_length(a_tokens, 1);
  idx            INT;

  l_result       address_search.T_HIERARCHICAL_QUERY;
BEGIN
  IF a_level > l_length THEN
    RETURN NULL;
  END IF;

  FOR idx IN 1 .. a_level LOOP
    IF idx < a_level THEN
      l_hquery := concat(l_hquery, a_tokens [idx], '%');
    ELSE
      l_lquery := concat(l_lquery, a_tokens [idx], '%');
    END IF;
    l_query_tokens := array_append(l_query_tokens, a_tokens [idx]);
  END LOOP;

  l_result.tokens := l_query_tokens;
  l_result.level := a_level;
  l_result.hquery := l_hquery;
  l_result.lquery := l_lquery;

  RETURN l_result;
END;
$$ LANGUAGE plpgsql;
/