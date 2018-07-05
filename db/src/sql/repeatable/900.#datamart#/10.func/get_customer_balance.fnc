CREATE OR REPLACE FUNCTION datamart.get_customer_balance(
  a_customer_id IN BIGINT
) RETURNS NUMERIC
AS $$
BEGIN
  RETURN system.get_customer_balance(a_customer_id);
END;
$$ LANGUAGE plpgsql STABLE
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/