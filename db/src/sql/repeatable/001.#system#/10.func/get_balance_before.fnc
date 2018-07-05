CREATE OR REPLACE FUNCTION system.get_balance_before(
  a_account_id     IN BIGINT,
  a_date_exclusive IN TIMESTAMP
) RETURNS NUMERIC
AS $$
DECLARE
  l_result NUMERIC;
BEGIN
  -- в будущем эта функция должна будет научиться
  -- вычислять баланс с использованием снапшотов
  -- все остальные функции так или иначе используют
  -- эту функцию

  SELECT coalesce(sum(amount), 0)
  FROM system.transactions
  WHERE personal_account_id = a_account_id
        AND business_date < a_date_exclusive
  INTO l_result;

  RETURN l_result;
END;
$$ LANGUAGE plpgsql STABLE
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/