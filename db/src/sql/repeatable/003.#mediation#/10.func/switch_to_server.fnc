CREATE OR REPLACE FUNCTION mediation.switch_to_server(
  in_host        IN VARCHAR,
  in_port        IN VARCHAR,
  in_dbname      IN VARCHAR,
  in_remote_user IN VARCHAR,
  in_remote_pass IN VARCHAR
) RETURNS BOOLEAN
AS $$
DECLARE
  create_server_query       VARCHAR :=
  'CREATE SERVER mediation_fds FOREIGN DATA WRAPPER postgres_fdw OPTIONS (' ||
  'host ' || quote_nullable(in_host) || ', ' ||
  'port ' || quote_nullable(in_port) || ', ' ||
  'dbname' || quote_nullable(in_dbname) || ')';

  create_user_mapping_query VARCHAR :=
  'CREATE USER MAPPING FOR argus_sys SERVER mediation_fds OPTIONS (' ||
  'user ' || quote_nullable(in_remote_user) || ', ' ||
  'password' || quote_nullable(in_remote_pass) || ')';

BEGIN
  DROP SERVER IF EXISTS mediation_fds CASCADE;
  DROP RULE IF EXISTS update_unsuitable_data ON mediation.unsuitable_data_view;

  EXECUTE create_server_query;
  GRANT USAGE ON FOREIGN SERVER mediation_fds TO argus_sys;
  EXECUTE create_user_mapping_query;

  CREATE FOREIGN TABLE mediation.rated_outgoing_calls_buffer_fdw (
    call_id           BIGINT,
    call_date         TIMESTAMP,
    duration          NUMERIC,
    rated_duration    NUMERIC,
    rated_unit_id     VARCHAR,
    amount            NUMERIC,
    resource_number   VARCHAR,
    service_id        BIGINT,
    tariff_id         BIGINT,
    supplier_id       BIGINT,
    telephony_zone_id BIGINT,
    tariff_entry_id   BIGINT,
    tariff_entry_name VARCHAR,
    charge_job_id     VARCHAR
  )
  SERVER mediation_fds
  OPTIONS ( SCHEMA_NAME 'billing', TABLE_NAME 'rated_outgoing_calls_buffer');

  CREATE FOREIGN TABLE mediation.unsuitable_data_fdw (
    call_id            BIGINT,
    charge_job_id      VARCHAR(255),
    processing_stage   VARCHAR(255),
    call_date          TIMESTAMP,
    call_direction     VARCHAR(255),
    called_number      VARCHAR(255),
    calling_number     VARCHAR(255),
    call_duration_unit VARCHAR(255),
    call_duration      BIGINT,
    exchange           VARCHAR(255),
    foreign_id         VARCHAR(255),
    identified_by      VARCHAR(255),
    incoming_channel   VARCHAR(255),
    incoming_supplier  BIGINT,
    incoming_trunk     VARCHAR(255),
    outgoing_channel   VARCHAR(255),
    outgoing_supplier  BIGINT,
    outgoing_trunk     VARCHAR(255),
    raw_call_date      VARCHAR(255),
    raw_duration       VARCHAR(255),
    release_code       VARCHAR(255),
    service_id         BIGINT,
    tariff_id          BIGINT,
    error_type         VARCHAR(255),
    error_message      VARCHAR(255)
  )
  SERVER mediation_fds
  OPTIONS ( SCHEMA_NAME 'billing', TABLE_NAME 'unsuitable_data_view');

  CREATE OR REPLACE VIEW mediation.rated_outgoing_calls_buffer_view AS
    SELECT
      roc.call_id,
      roc.call_date,
      roc.duration,
      roc.rated_duration,
      roc.rated_unit_id AS rated_unit,
      roc.amount,
      roc.resource_number,
      roc.service_id,
      roc.tariff_id,
      roc.supplier_id,
      roc.telephony_zone_id,
      roc.tariff_entry_id,
      roc.tariff_entry_name,
      roc.charge_job_id
    FROM mediation.rated_outgoing_calls_buffer_fdw roc;

  CREATE OR REPLACE VIEW mediation.unsuitable_data_view AS
    SELECT
      ud.processing_stage,
      ud.call_id,
      ud.call_date,
      ud.raw_call_date,
      ud.call_direction,
      ud.call_duration                                 AS duration,
      (CASE ud.call_duration_unit
       WHEN 'MIN'
         THEN 'MINUTE'
       WHEN 'SEC'
         THEN 'SECOND'
       ELSE ud.call_duration_unit END) :: VARCHAR(255) AS cdr_unit,
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
      ud.exchange                                      AS source,
      ud.foreign_id,
      ud.identified_by,
      ud.charge_job_id,
      ud.error_type,
      ud.error_message                                 AS error_msg
    FROM mediation.unsuitable_data_fdw ud;

  CREATE RULE update_unsuitable_data AS
  ON UPDATE TO mediation.unsuitable_data_view DO INSTEAD NOTHING;

  RETURN TRUE;
END;
$$ LANGUAGE plpgsql
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/