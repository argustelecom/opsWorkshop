CREATE OR REPLACE FUNCTION qms.get_last_history(
  in_queue_id VARCHAR,
  in_group_id VARCHAR,
  in_limit    INTEGER DEFAULT 50
)
  RETURNS SETOF qms.T_HISTORY_ITEM AS
$$
BEGIN
  RETURN QUERY (
    SELECT
      qh.event_id :: BIGINT                 AS event_id,
      qh.queue_id :: VARCHAR                AS queue_id,
      qh.group_id :: VARCHAR                AS group_id,
      qh.handler_name :: VARCHAR            AS handler_name,
      qh.context :: VARCHAR                 AS context,
      last_exec.queue_status :: VARCHAR     AS queue_status,
      last_exec.scheduled_time :: TIMESTAMP AS scheduled_time,
      last_exec.history_id :: BIGINT        AS execution_id,
      last_exec.execution_status :: VARCHAR AS scheduled_time,
      last_exec.execution_time :: TIMESTAMP AS execution_time
    FROM qms.queue_history qh, LATERAL ((
      SELECT
        eh.history_id,
        (
          CASE
          WHEN eh.queue_status = 'ACTIVE' AND eh.execution_status = 'WORK_HANDLED' THEN
            'COMPLETED'
          WHEN eh.queue_status = 'INACTIVE' THEN
            'SUSPENDED'
          WHEN eh.queue_status = 'ACTIVE' THEN
            'PENDING'
          ELSE
            eh.queue_status
          END
        ) AS queue_status,
        eh.scheduled_time,
        eh.execution_time,
        eh.execution_status
      FROM qms.execution_history eh
      WHERE eh.event_id = qh.event_id
      ORDER BY
        eh.history_id DESC
      LIMIT 1
    )) last_exec
    WHERE
      (in_group_id IS NULL OR qh.group_id = in_group_id)
      AND
      (in_queue_id IS NULL OR qh.queue_id = in_queue_id)
    ORDER BY
      qh.event_id DESC
    LIMIT coalesce(in_limit, 999999999999)
  );

  RETURN;
END;
$$ LANGUAGE plpgsql;
/