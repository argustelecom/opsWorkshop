CREATE OR REPLACE FUNCTION address_search.create_false_positive_filter(
  a_raw_input IN VARCHAR
) RETURNS VARCHAR
AS $$
DECLARE
  l_token  VARCHAR;
  l_tokens VARCHAR [];
  l_result VARCHAR := '%';
BEGIN
  l_tokens := address_search.parse_raw_input(a_raw_input, FALSE);
  FOREACH l_token IN ARRAY l_tokens LOOP
    l_result := concat(l_result, l_token, '%');
  END LOOP;

  RETURN l_result;
END;
$$ LANGUAGE plpgsql;
/