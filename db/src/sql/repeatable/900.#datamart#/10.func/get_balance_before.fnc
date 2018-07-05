CREATE OR REPLACE FUNCTION datamart.get_balance_before(
  a_account_id     IN BIGINT,
  a_date_exclusive IN TIMESTAMP
) RETURNS NUMERIC
AS $$
BEGIN
  RETURN system.get_balance_before(a_account_id, a_date_exclusive);
END;
$$ LANGUAGE plpgsql STABLE
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/