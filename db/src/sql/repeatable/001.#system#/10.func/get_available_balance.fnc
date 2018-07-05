CREATE OR REPLACE FUNCTION system.get_available_balance(
  a_account_id IN BIGINT
) RETURNS NUMERIC
AS $$
DECLARE
  l_result NUMERIC;
BEGIN
  SELECT system.get_balance(a_account_id) - coalesce(sum(r.amount), 0)
  FROM system.reserve r
  WHERE r.personal_account_id = a_account_id
  INTO l_result;

  RETURN l_result;
END;
$$ LANGUAGE plpgsql STABLE
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/