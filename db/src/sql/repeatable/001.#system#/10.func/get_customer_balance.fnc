CREATE OR REPLACE FUNCTION system.get_customer_balance(
  a_customer_id IN BIGINT
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
    SELECT system.get_balance(pa.personal_account_id) AS balance
    ) b
  INTO l_result;

  RETURN l_result;
END;
$$ LANGUAGE plpgsql STABLE
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/