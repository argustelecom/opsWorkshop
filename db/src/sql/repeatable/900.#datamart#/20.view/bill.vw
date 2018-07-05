CREATE OR REPLACE VIEW datamart.bill_vw AS
  SELECT
    b.id              AS bill_id,
    b.document_number AS bill_number,
    b.document_date   AS bill_date,
    b.bill_type_id,
    b.start_date      AS bill_period_start,
    b.end_date        AS bill_period_end,
    b.creation_date,
    b.customer_id,

    (CASE b.grouping_method
     WHEN 'CONTRACT'
       THEN 'По договору'
     ELSE 'По лицевому счету'
     END)             AS gruping_method,

    (CASE b.grouping_method
     WHEN 'CONTRACT'
       THEN b.group_id
     ELSE NULL
     END)             AS contract_id,

    (CASE b.grouping_method
     WHEN 'PERSONAL_ACCOUNT'
       THEN b.group_id
     ELSE NULL
     END)             AS personal_account_id,

    (CASE b.payment_condition
     WHEN 'PREPAYMENT'
       THEN 'Предоплата'
     ELSE 'Постоплата'
     END)             AS payment_condition,

    b.amount_with_tax,
    b.amount_without_tax,
    b.tax_amount      AS amount_tax,
    b.total_amount    AS amount_total,
    b.discount_amount AS amount_discount
  FROM system.bill b;