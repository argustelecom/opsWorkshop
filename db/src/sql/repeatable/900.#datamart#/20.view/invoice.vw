CREATE OR REPLACE VIEW datamart.invoice_vw AS
  SELECT
    i.id                       AS invoice_id,
    i.personal_account_id,
    ce.product_offering_id,
    i.price :: NUMERIC(19, 12) AS amount,
    i.state,
    i.subscription_id,
    i.start_date,
    i.end_date
  FROM system.invoice i
    JOIN system.subscription s ON i.subscription_id = s.id
    JOIN system.subscription_subject_cause ssc ON s.subject_cause_id = ssc.id
    JOIN system.contract_entry ce ON ssc.contract_entry_id = ce.id
  UNION
  SELECT
    i.id AS invoice_id,
    i.personal_account_id,
    ie.product_offering_id,
    ie.amount,
    i.state,
    i.subscription_id,
    i.start_date,
    i.end_date
  FROM system.invoice i
    JOIN system.invoice_entry ie ON i.id = ie.invoice_id;