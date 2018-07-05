CREATE OR REPLACE FUNCTION system.synchronize(
  in_charge_job_id IN VARCHAR
) RETURNS INTEGER
AS $$
DECLARE
  l_sync_rows INTEGER;
BEGIN
  IF in_charge_job_id IS NULL || in_charge_job_id = '' THEN
    RETURN 0;
  END IF;

  WITH affected AS
  (
    INSERT INTO system.rated_outgoing_calls (
      call_id,
      call_date,
      duration,
      rated_duration,
      rated_unit,
      amount,
      resource_number,
      service_id,
      tariff_id,
      supplier_id,
      telephony_zone_id,
      tariff_entry_id,
      tariff_entry_name,
      charge_job_id
    )
      SELECT
        call_id,
        call_date,
        duration,
        rated_duration,
        rated_unit,
        amount,
        resource_number,
        service_id,
        tariff_id,
        supplier_id,
        telephony_zone_id,
        tariff_entry_id,
        tariff_entry_name,
        charge_job_id
      FROM mediation.rated_outgoing_calls_buffer_view
      WHERE charge_job_id = in_charge_job_id
    ON CONFLICT ON CONSTRAINT pk_rated_outgoing_calls DO UPDATE SET
      call_date         = EXCLUDED.call_date,
      duration          = EXCLUDED.duration,
      rated_duration    = EXCLUDED.rated_duration,
      rated_unit        = EXCLUDED.rated_unit,
      amount            = EXCLUDED.amount,
      resource_number   = EXCLUDED.resource_number,
      service_id        = EXCLUDED.service_id,
      tariff_id         = EXCLUDED.tariff_id,
      supplier_id       = EXCLUDED.supplier_id,
      telephony_zone_id = EXCLUDED.telephony_zone_id,
      tariff_entry_id   = EXCLUDED.tariff_entry_id,
      tariff_entry_name = EXCLUDED.tariff_entry_name,
      charge_job_id     = EXCLUDED.charge_job_id
    RETURNING call_id
  )
  SELECT count(call_id) INTO l_sync_rows
  FROM affected;

  RETURN l_sync_rows;
END;
$$
LANGUAGE plpgsql
SECURITY DEFINER;
/