CREATE OR REPLACE FUNCTION system.get_balance(
  a_account_id IN BIGINT
) RETURNS NUMERIC
AS $$
DECLARE
  l_result NUMERIC;
BEGIN

  SELECT coalesce(r.balance, 0)
  FROM
    (
      SELECT max(business_date) + '1 millisecond' AS last_tx_instant
      FROM system.transactions
      WHERE personal_account_id = a_account_id
    ) ad,
    LATERAL (
    SELECT system.get_balance_before(
               a_account_id,
               coalesce(ad.last_tx_instant, current_timestamp :: TIMESTAMP)
           ) AS balance
    ) r
  INTO l_result;

  RETURN l_result;
END;
$$ LANGUAGE plpgsql STABLE
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/