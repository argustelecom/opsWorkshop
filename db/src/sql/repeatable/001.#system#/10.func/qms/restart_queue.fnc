CREATE OR REPLACE FUNCTION qms.restart_queue(
  a_queue_id IN VARCHAR
)
  RETURNS BOOLEAN
AS $$
DECLARE
  l_event_id BIGINT;
  l_result   BOOLEAN := FALSE;
BEGIN

  SELECT e.id
  FROM qms.queue_event e
    JOIN qms.queue q ON e.queue_id = q.id
  WHERE e.queue_id = a_queue_id
        AND q.status = ANY (ARRAY ['INACTIVE', 'FAILED'])
  ORDER BY
    e.queue_id,
    e.id ASC
  LIMIT 1
  INTO l_event_id;

  IF l_event_id IS NOT NULL THEN

    DELETE FROM qms.queue_event_error
    WHERE event_id = l_event_id;

    UPDATE qms.queue
    SET
      status         = 'ACTIVE',
      scheduled_time = now()
    WHERE id = a_queue_id;

    INSERT INTO qms.queue_log
    (id, status, queue_id, queue_status, event_id, scheduled_time, execution_time, handler_name, context)
      SELECT
        nextval('qms.gen_queue_log_id') AS id,
        'RESTORED'                      AS status,
        q.id                            AS queue_id,
        q.status                        AS queue_status,
        e.id                            AS event_id,
        now()                           AS scheduled_time,
        now()                           AS execution_time,
        e.handler_name,
        e.context
      FROM qms.queue q
        JOIN qms.queue_event e ON q.id = e.queue_id
      WHERE e.id = l_event_id;

    l_result := TRUE;
  END IF;

  RETURN l_result;
END;
$$
LANGUAGE plpgsql
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/