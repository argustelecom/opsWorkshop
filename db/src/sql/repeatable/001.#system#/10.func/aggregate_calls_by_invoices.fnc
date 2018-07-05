CREATE OR REPLACE FUNCTION system.aggregate_calls_by_invoices(v_invoice_ids BIGINT [])
  RETURNS TABLE(invoice_id BIGINT, resource_number VARCHAR, telephony_zone_id BIGINT, tariff_id BIGINT, amount NUMERIC) AS
$$
SELECT
  iroc.usage_invoice_id,
  roc.resource_number,
  roc.telephony_zone_id,
  roc.tariff_id,
  sum(roc.amount)
FROM
  system.rated_outgoing_calls roc
  INNER JOIN system.usage_invoice_rated_outgoing_call iroc ON roc.call_id = iroc.call_id
WHERE iroc.usage_invoice_id = ANY (v_invoice_ids)
GROUP BY
  iroc.usage_invoice_id,
  roc.resource_number,
  roc.telephony_zone_id,
  roc.tariff_id;
$$
LANGUAGE 'sql';
/