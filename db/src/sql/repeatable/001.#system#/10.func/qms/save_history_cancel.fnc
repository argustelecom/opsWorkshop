CREATE OR REPLACE FUNCTION qms.save_history_cancel(
  in_queue_id       IN VARCHAR,
  in_execution_time IN TIMESTAMP
)
  RETURNS BOOLEAN AS
$$
DECLARE
  l_affected_rows INTEGER;
BEGIN
  WITH affected AS
  (
    INSERT INTO qms.execution_history
    (history_id, event_id, queue_status, scheduled_time, execution_status, execution_time)
      SELECT
        nextval('qms.gen_queue_log_id'),
        event_id,
        'CANCELLED',
        scheduled_time,
        'WORK_HANDLED',
        in_execution_time
      FROM qms.get_last_history(in_queue_id, NULL)
    RETURNING *
  )
  SELECT count(*) INTO l_affected_rows
  FROM affected;

  RETURN l_affected_rows > 0;
END;
$$ LANGUAGE plpgsql;
/