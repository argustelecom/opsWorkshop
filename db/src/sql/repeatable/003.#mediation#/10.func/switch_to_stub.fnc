CREATE OR REPLACE FUNCTION mediation.switch_to_stub() RETURNS BOOLEAN
AS $$
BEGIN
  CREATE OR REPLACE VIEW mediation.rated_outgoing_calls_buffer_view AS
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
    FROM mediation.rated_outgoing_calls_buffer_stub;

  DROP RULE IF EXISTS update_unsuitable_data ON mediation.unsuitable_data_view;

  CREATE OR REPLACE VIEW mediation.unsuitable_data_view AS
    SELECT
      ud.processing_stage,
      ud.call_id,
      ud.call_date,
      ud.raw_call_date,
      ud.call_direction,
      ud.call_duration      AS duration,
      ud.call_duration_unit AS cdr_unit,
      ud.raw_duration,
      ud.release_code,
      ud.called_number,
      ud.calling_number,
      ud.outgoing_channel,
      ud.outgoing_trunk,
      ud.outgoing_supplier,
      ud.incoming_channel,
      ud.incoming_trunk,
      ud.incoming_supplier,
      ud.service_id,
      ud.tariff_id,
      ud.exchange           AS source,
      ud.foreign_id,
      ud.identified_by,
      ud.charge_job_id,
      ud.error_type,
      ud.error_msg
    FROM mediation.unsuitable_data_stub ud;

  CREATE OR REPLACE RULE update_unsuitable_data AS
  ON UPDATE TO mediation.unsuitable_data_view DO INSTEAD
    UPDATE mediation.unsuitable_data_stub SET
      call_date          = new.call_date,
      call_duration      = new.duration,
      call_duration_unit = new.cdr_unit,
      called_number      = new.called_number,
      calling_number     = new.calling_number,
      incoming_channel   = new.incoming_channel,
      incoming_trunk     = new.incoming_trunk,
      outgoing_channel   = new.outgoing_channel,
      outgoing_trunk     = new.outgoing_trunk,
      release_code       = new.release_code,
      call_direction     = new.call_direction
    WHERE unsuitable_data_stub.call_id = new.call_id;

  DROP SERVER IF EXISTS mediation_fds CASCADE;

  RETURN TRUE;
END;
$$ LANGUAGE plpgsql
SECURITY DEFINER;
/