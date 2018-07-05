CREATE OR REPLACE FUNCTION datamart.get_flow_of_funds(
  a_account_id IN BIGINT,
  a_date_from  IN TIMESTAMP,
  a_date_to    IN TIMESTAMP
) RETURNS datamart.T_FLOW_OF_FUNDS
AS $$
DECLARE
  l_start_balance NUMERIC;
  l_end_balance   NUMERIC;
  l_delta_balance system.T_BALANCE_DELTA;
  l_result        datamart.T_FLOW_OF_FUNDS;
BEGIN
  l_start_balance := system.get_balance_before(a_account_id, a_date_from);
  l_delta_balance := system.get_balance_delta(a_account_id, a_date_from, a_date_to);
  l_end_balance := l_start_balance + l_delta_balance.delta;

  l_result.start_debet := CASE WHEN l_start_balance > 0 THEN l_start_balance ELSE 0 END;
  l_result.start_credit := CASE WHEN l_start_balance < 0 THEN abs(l_start_balance) ELSE 0 END;
  l_result.delta_debet := l_delta_balance.debet;
  l_result.delta_credit := l_delta_balance.credit;
  l_result.end_debet := CASE WHEN l_end_balance > 0 THEN l_end_balance ELSE 0 END;
  l_result.end_credit := CASE WHEN l_end_balance < 0 THEN abs(l_end_balance) ELSE 0 END;

  RETURN l_result;
END;
$$ LANGUAGE plpgsql
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/