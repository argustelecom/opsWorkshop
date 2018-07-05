CREATE OR REPLACE VIEW datamart.contract_history_vw AS
  SELECT
    lh.lifecycle_object_id     AS contract_id,
    lh.lifecycle_object_entity AS contract_entity,
    lh.from_state,
    lh.to_state,
    lh.transition_time,
    lh.initiator_name
  FROM system.lifecycle_history lh
  WHERE lh.lifecycle_object_entity IN ('Contract', 'ContractExtension');