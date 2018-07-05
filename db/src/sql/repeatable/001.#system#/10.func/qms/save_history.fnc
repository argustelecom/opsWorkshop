CREATE OR REPLACE FUNCTION qms.save_history(
  in_event_id         IN BIGINT,
  in_queue_id         IN VARCHAR,
  in_group_id         IN VARCHAR,
  in_handler_name     IN VARCHAR,
  in_context          IN VARCHAR,
  in_queue_status     IN VARCHAR,
  in_scheduled_time   IN TIMESTAMP,
  in_execution_status IN VARCHAR,
  in_execution_time   IN TIMESTAMP
)
  RETURNS BIGINT AS
$$
DECLARE
  l_history_id BIGINT;
BEGIN
  INSERT INTO qms.queue_history
  (event_id, queue_id, group_id, handler_name, context)
  VALUES
    (
      in_event_id,
      in_queue_id,
      in_group_id,
      in_handler_name,
      in_context :: JSONB
    )
  ON CONFLICT ON CONSTRAINT pk_queue_history
    DO NOTHING;

  INSERT INTO qms.execution_history
  (history_id, event_id, queue_status, scheduled_time, execution_status, execution_time)
  VALUES
    (
      nextval('qms.gen_queue_log_id'),
      in_event_id,
      in_queue_status,
      in_scheduled_time,
      in_execution_status,
      in_execution_time
    )
  RETURNING history_id
    INTO l_history_id;

  RETURN l_history_id;
END;
$$ LANGUAGE plpgsql;
/