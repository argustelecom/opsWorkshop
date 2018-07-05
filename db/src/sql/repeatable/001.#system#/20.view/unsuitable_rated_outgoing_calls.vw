CREATE OR REPLACE VIEW system.unsuitable_rated_outgoing_calls_view AS
  SELECT
    roc.call_id as id,
    roc.call_date,
    roc.duration,
    roc.rated_unit,
    roc.amount,
    roc.tariff_id,
    roc.resource_number,
    roc.service_id,
    roc.supplier_id,
    roc.telephony_zone_id
  FROM system.rated_outgoing_calls roc
    JOIN system.unsuitable_rated_outgoing_call uroc ON roc.call_id = uroc.call_id;