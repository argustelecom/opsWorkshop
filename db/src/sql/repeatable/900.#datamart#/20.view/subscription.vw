CREATE OR REPLACE VIEW datamart.subscription_vw AS
  SELECT
    s.id AS subscription_id,
    s.personal_account_id,
    pa.customer_id,
    s.creation_date,
    s.close_date,
    s.valid_from,
    s.valid_to,
    s.state,
    ce.contract_id,
    ce.product_offering_id
  FROM system.subscription s
    JOIN system.subscription_subject_cause ssc ON s.subject_cause_id = ssc.id
    JOIN system.personal_account pa ON s.personal_account_id = pa.id
    LEFT JOIN system.contract_entry ce ON ssc.contract_entry_id = ce.id;