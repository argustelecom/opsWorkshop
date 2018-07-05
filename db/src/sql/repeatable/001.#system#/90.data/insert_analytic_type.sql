--TODO заполнить service_name
INSERT INTO system.analytic_type
  (id, name, keyword, service_name, available_for_custom_period, analytic_category, charges_type, bill_date_getter, round_negative_value, dtype, description, is_row, invertible) VALUES
  (1, '{BillAnalyticTypeDbBundle:box.bill.previous_period_charges.name}', 'PreviousPeriodCharges', NULL, FALSE, 'CHARGE', NULL, 'PREVIOUS_PERIOD', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (2, '{BillAnalyticTypeDbBundle:box.bill.next_period_charges.name}', 'NextPeriodCharges', NULL, FALSE, 'CHARGE', NULL, 'NEXT_PERIOD', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (3, '{BillAnalyticTypeDbBundle:box.bill.previous_period_incomes.name}', 'PreviousPeriodIncomes', NULL, FALSE, 'INCOME', NULL, 'PREVIOUS_PERIOD', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (4, '{BillAnalyticTypeDbBundle:box.bill.current_period_incomes.name}', 'CurrentPeriodIncomes', NULL, TRUE, 'INCOME', NULL, 'CURRENT_PERIOD', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (5, '{BillAnalyticTypeDbBundle:box.bill.next_period_incomes.name}', 'NextPeriodIncomes', NULL, FALSE, 'INCOME', NULL, 'NEXT_PERIOD', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (6, '{BillAnalyticTypeDbBundle:box.bill.next_period_incomes_before_bill_date.name}', 'NextPeriodIncomesBeforeBillDate', NULL, FALSE, 'INCOME', NULL, 'NEXT_PERIOD_INCOMES_BEFORE_BILL_DATE', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (7, '{BillAnalyticTypeDbBundle:box.bill.next_period_incomes_before_bill_creation_date.name}', 'NextPeriodIncomesBeforeBillCreationDate', NULL, FALSE, 'INCOME', NULL, 'NEXT_PERIOD_INCOMES_BEFORE_BILL_CREATION_DATE', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (8, '{BillAnalyticTypeDbBundle:box.bill.previous_period_starting_balance.name}', 'PreviousPeriodStartingBalance', NULL, FALSE, 'BALANCE', NULL, 'PREVIOUS_PERIOD_STARTING_BALANCE', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (9, '{BillAnalyticTypeDbBundle:box.bill.current_period_starting_balance.name}', 'CurrentPeriodStartingBalance', NULL, TRUE, 'BALANCE', NULL, 'CURRENT_PERIOD_STARTING_BALANCE', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (10, '{BillAnalyticTypeDbBundle:box.bill.current_period_ending_balance.name}', 'CurrentPeriodEndingBalance', NULL, TRUE, 'BALANCE', NULL, 'CURRENT_PERIOD_ENDING_BALANCE', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (11, '{BillAnalyticTypeDbBundle:box.bill.next_period_ending_balance.name}', 'NextPeriodEndingBalance', NULL, FALSE, 'BALANCE', NULL, 'NEXT_PERIOD_ENDING_BALANCE', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (12, '{BillAnalyticTypeDbBundle:box.bill.bill_date_balance.name}', 'BillDateBalance', NULL, TRUE, 'BALANCE', NULL, 'BILL_DATE_BALANCE', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (13, '{BillAnalyticTypeDbBundle:box.bill.bill_creation_date_balance.name}', 'BillCreationDateBalance', NULL, TRUE, 'BALANCE', NULL, 'BILL_CREATION_DATE_BALANCE', NULL, 'BillAnalyticType', NULL, FALSE, FALSE),
  (14, '{BillAnalyticTypeDbBundle:box.bill.period_summary_charges.name}', 'PeriodSummaryCharges', NULL, TRUE, NULL, NULL, NULL, NULL, 'SummaryBillAnalyticType', NULL, FALSE, TRUE),
  (15, '{BillAnalyticTypeDbBundle:box.bill.period_summary_charges_with_balance_for_the_bill_creation_date.name}', 'PeriodSummaryChargesWithBalanceForTheBillCreationDate', NULL, TRUE, NULL, NULL, NULL, NULL, 'SummaryBillAnalyticType', '{BillAnalyticTypeDbBundle:box.bill.period_summary_charges_with_balance_for_the_bill_creation_date.description}', FALSE, TRUE),
  (16, '{BillAnalyticTypeDbBundle:box.bill.period_summary_charges_with_previous_period_charges_and_incomes.name}', 'PeriodSummaryChargesWithPreviousPeriodChargesAndIncomes', NULL, FALSE, NULL, NULL, NULL, NULL, 'SummaryBillAnalyticType', '{BillAnalyticTypeDbBundle:box.bill.period_summary_charges_with_previous_period_charges_and_incomes.description}', FALSE, TRUE),
  (17, '{BillAnalyticTypeDbBundle:box.bill.current_period_charges_by_recurrent.name}', 'CurrentPeriodChargesByRecurrent', NULL, TRUE, 'CHARGE', 'RECURRENT', 'CURRENT_PERIOD', TRUE, 'BillAnalyticType', NULL, TRUE, FALSE),
  (18, '{BillAnalyticTypeDbBundle:box.bill.current_period_charges_by_non_recurrent.name}', 'CurrentPeriodChargesByNonRecurrent', NULL, TRUE, 'CHARGE', 'NONRECURRENT', 'CURRENT_PERIOD', TRUE, 'BillAnalyticType', NULL, TRUE, FALSE),
  (19, '{BillAnalyticTypeDbBundle:box.bill.current_period_charges_by_usage.name}', 'CurrentPeriodChargesByUsage', NULL, TRUE, 'CHARGE', 'USAGE', 'CURRENT_PERIOD', TRUE, 'BillAnalyticType', NULL, TRUE, FALSE)
ON CONFLICT (id)
  DO UPDATE SET
    name                        = EXCLUDED.name,
    keyword                     = EXCLUDED.keyword,
    service_name                = EXCLUDED.service_name,
    available_for_custom_period = EXCLUDED.available_for_custom_period,
    analytic_category           = EXCLUDED.analytic_category,
    charges_type	              = EXCLUDED.charges_type,
    bill_date_getter            = EXCLUDED.bill_date_getter,
    round_negative_value        = EXCLUDED.round_negative_value,
    description                 = EXCLUDED.description,
    is_row                      = EXCLUDED.is_row,
    invertible                  = EXCLUDED.invertible,
    dtype                       = EXCLUDED.dtype;


INSERT INTO system.summary_type_analytic_type (summary_type_id, analytic_type_id)
VALUES (15, 13), (16, 1), (16, 3), (16, 8)
ON CONFLICT ON CONSTRAINT pk_summary_type_analytic_type
  DO UPDATE SET
    summary_type_id  = EXCLUDED.summary_type_id,
    analytic_type_id = EXCLUDED.analytic_type_id;