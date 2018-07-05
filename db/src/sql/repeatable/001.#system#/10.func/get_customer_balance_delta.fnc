CREATE OR REPLACE FUNCTION system.get_customer_balance_delta(
  a_customer_id         IN BIGINT,
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
    coalesce(sum(d.debet), 0)  AS debet,
    coalesce(sum(d.credit), 0) AS credit
  FROM
    (
      SELECT pa.id AS personal_account_id
      FROM system.personal_account pa
      WHERE pa.customer_id = a_customer_id
    ) pa,
    LATERAL (
    SELECT *
    FROM system.get_balance_delta(pa.personal_account_id, a_date_from_inclusive, a_date_to_exclusive)
    ) d
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