CREATE OR REPLACE VIEW datamart.task_vw AS
  SELECT
    t.id,
    t.number,
    t.state,
    t.task_type,
    t.create_date_time,
    s.id AS subscription_id,
    ssc.contract_entry_id
  FROM
    system.task t
    JOIN system.subscription s ON t.subscription_id = s.id
    JOIN system.subscription_subject_cause ssc ON s.subject_cause_id = ssc.id;