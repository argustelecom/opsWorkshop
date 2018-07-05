CREATE OR REPLACE FUNCTION system.get_customer_balance_before(
  a_customer_id    IN BIGINT,
  a_date_exclusive IN TIMESTAMP
) RETURNS NUMERIC
AS $$
DECLARE
  l_result NUMERIC;
BEGIN
  SELECT coalesce(sum(b.balance), 0)
  FROM (
         SELECT pa.id AS personal_account_id
         FROM system.personal_account pa
         WHERE pa.customer_id = a_customer_id
       ) pa,
    LATERAL (
    SELECT system.get_balance_before(pa.personal_account_id, a_date_exclusive) AS balance
    ) b
  INTO l_result;

  RETURN l_result;
END;
$$ LANGUAGE plpgsql STABLE
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/