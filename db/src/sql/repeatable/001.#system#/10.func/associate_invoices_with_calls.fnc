CREATE OR REPLACE FUNCTION system.associate_invoices_with_calls(v_invoice_ids_raw VARCHAR,
                                                                v_charge_job_id   VARCHAR DEFAULT '')
  RETURNS TABLE(invoice_id BIGINT, resource_number VARCHAR, telephony_zone_id BIGINT, tariff_id BIGINT, amount NUMERIC) AS
$$
DECLARE
  invoice_ids   BIGINT [] = CAST(v_invoice_ids_raw AS BIGINT []);
  charge_job_id VARCHAR;
BEGIN
  IF (v_charge_job_id != '')
  THEN
    charge_job_id = v_charge_job_id;
  END IF;
  EXECUTE system.tie_invoices_with_calls(v_invoice_ids := invoice_ids, v_charge_job_id := charge_job_id);

  RETURN QUERY SELECT *
               FROM SYSTEM.aggregate_calls_by_invoices(invoice_ids);
END;
$$
LANGUAGE plpgsql;
/