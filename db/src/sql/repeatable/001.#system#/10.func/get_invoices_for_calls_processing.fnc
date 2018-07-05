CREATE OR REPLACE FUNCTION system.get_invoices_for_call_processing(v_invoice_ids BIGINT [])
  RETURNS TABLE(id BIGINT, start_date TIMESTAMP, end_date TIMESTAMP, service_id BIGINT, provider_id BIGINT, zones_ids BIGINT [], without_contract BOOLEAN) AS
$$
SELECT
  i.id,
  i.start_date,
  i.end_date,
  i.service_id,
  i.provider_id,
  ARRAY
  (SELECT zone_id
   FROM system.commodity o, system.telephony_option_type_telephony_zone otz
   WHERE i.option_id = o.id AND o.type_id = otz.option_type_id) AS zones_ids,
  i.without_contract
FROM system.invoice i
WHERE id = ANY (v_invoice_ids)
$$
LANGUAGE 'sql';
/