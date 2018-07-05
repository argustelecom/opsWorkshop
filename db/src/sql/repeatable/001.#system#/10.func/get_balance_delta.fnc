CREATE OR REPLACE FUNCTION system.get_balance_delta(
  a_account_id          IN BIGINT,
  a_date_from_inclusive IN TIMESTAMP,
  a_date_to_exclusive   IN TIMESTAMP
) RETURNS system.T_BALANCE_DELTA
AS $$
DECLARE
  l_debet  NUMERIC;
  l_credit NUMERIC;
  l_result system.T_BALANCE_DELTA;
BEGIN

  SELECT
    coalesce(sum(CASE WHEN amount >= 0 THEN amount ELSE 0 END), 0),
    coalesce(sum(CASE WHEN amount < 0 THEN abs(amount) ELSE 0 END), 0)
  FROM system.transactions
  WHERE personal_account_id = a_account_id
        AND business_date >= a_date_from_inclusive
        AND business_date < a_date_to_exclusive
  INTO l_debet, l_credit;

  l_result.debet := l_debet;
  l_result.credit := l_credit;
  l_result.delta := l_debet - l_credit;

  RETURN l_result;
END;
$$ LANGUAGE plpgsql STABLE
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/