CREATE OR REPLACE FUNCTION address_search.create_bquery(
  a_inbound_tokens  IN VARCHAR [],
  a_outbound_tokens IN VARCHAR []
) RETURNS VARCHAR
AS $$
DECLARE
  l_diff  VARCHAR [];
  l_query address_search.T_QUERY;
BEGIN
  l_diff := address_search.diff_search_tokens(a_inbound_tokens, a_outbound_tokens);
  l_query := address_search.create_query(l_diff, array_length(l_diff, 1));
  RETURN ltrim(l_query.query, '%');
END;
$$ LANGUAGE plpgsql;
/