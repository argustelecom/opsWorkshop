CREATE OR REPLACE FUNCTION address_search.create_query(
  a_tokens  IN VARCHAR [],
  a_level   IN INT DEFAULT 1,
  a_reverse IN BOOLEAN DEFAULT FALSE
) RETURNS address_search.T_QUERY
AS $$
DECLARE
  l_query_tokens VARCHAR [] := ARRAY [] :: VARCHAR [];
  l_query        VARCHAR := '%';
  l_length       INT := array_length(a_tokens, 1);
  idx            INT;

  l_result       address_search.T_QUERY;
BEGIN
  IF a_level > l_length THEN
    RETURN NULL;
  END IF;

  IF a_reverse THEN
    FOR idx IN REVERSE l_length .. l_length - a_level + 1 LOOP
      l_query := concat('%', a_tokens [idx], l_query);
      l_query_tokens := array_prepend(a_tokens [idx], l_query_tokens);
    END LOOP;
  ELSE
    FOR idx IN 1 .. a_level LOOP
      l_query := concat(l_query, a_tokens [idx], '%');
      l_query_tokens := array_append(l_query_tokens, a_tokens [idx]);
    END LOOP;
  END IF;

  l_result.tokens := l_query_tokens;
  l_result.level := a_level;
  l_result.query := l_query;

  RETURN l_result;
END;
$$ LANGUAGE plpgsql;
/