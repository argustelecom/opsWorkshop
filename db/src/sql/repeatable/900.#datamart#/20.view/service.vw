CREATE OR REPLACE VIEW datamart.service_vw  AS
  SELECT
    co.id,
    co.type_id AS service_type_id,
    ct.name    AS service_type,
    co.state,
    ce.id      AS contract_entry_id,
    ce.contract_id,
    co.properties,
    s.id       AS subscription_id
  FROM system.commodity co
    JOIN system.commodity_type ct ON co.type_id = ct.id
    JOIN system.contract_entry ce ON co.subject_id = ce.id
    JOIN system.subscription_subject_cause ssc ON ce.id = ssc.contract_entry_id
    JOIN system.subscription s ON ssc.id = s.subject_cause_id
  WHERE co.dtype = 'Service';