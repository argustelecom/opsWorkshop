CREATE OR REPLACE VIEW datamart.subscription_history_vw AS
  SELECT
    lh.lifecycle_object_id AS subscription_id,
    lh.lifecycle,
    lh.from_state,
    lh.to_state,
    lh.transition_time,
    lh.initiator_name
  FROM system.lifecycle_history lh
  WHERE lh.lifecycle_object_entity = 'Subscription';