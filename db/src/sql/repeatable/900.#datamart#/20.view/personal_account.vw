CREATE OR REPLACE VIEW datamart.personal_account_vw AS
  SELECT
    pa.id AS personal_account_id,
    pa.customer_id,
    pa.number,
    pa.currency
  FROM system.personal_account pa;