CREATE OR REPLACE FUNCTION datamart.get_balance(
  a_account_id IN BIGINT
) RETURNS NUMERIC
AS $$
BEGIN
  RETURN system.get_balance(a_account_id);
END;
$$ LANGUAGE plpgsql STABLE
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/