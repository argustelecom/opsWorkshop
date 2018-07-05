CREATE OR REPLACE FUNCTION address_search.diff_search_tokens(
  a_inbound_array  IN VARCHAR [],
  a_outbound_array IN VARCHAR []
) RETURNS VARCHAR []
AS $$
DECLARE
  idx         INT;
  inbound_al  INT := array_length(a_inbound_array, 1);
  outbound_al INT := array_length(a_outbound_array, 1);

  result      VARCHAR [] := '{}';
BEGIN
  IF a_inbound_array <@ a_outbound_array THEN
    FOR idx IN inbound_al + 1 .. outbound_al LOOP
      result := array_append(result, a_outbound_array [idx]);
    END LOOP;
  END IF;

  RETURN result;
END;
$$ LANGUAGE plpgsql;
/