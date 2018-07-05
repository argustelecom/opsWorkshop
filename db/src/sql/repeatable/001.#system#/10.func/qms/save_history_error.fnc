CREATE OR REPLACE FUNCTION qms.save_history_error(
  in_history_id  IN BIGINT,
  in_error_class IN VARCHAR,
  in_error_text  IN VARCHAR,
  in_error_stack IN VARCHAR
)
  RETURNS BIGINT AS
$$
BEGIN
  INSERT INTO qms.execution_history_error
  (history_id, error_class, error_text, error_stack)
  VALUES
    (
      in_history_id,
      in_error_class,
      in_error_text,
      in_error_stack
    )
  ON CONFLICT ON CONSTRAINT pk_execution_history_error
    DO NOTHING;

  RETURN in_history_id;
END;
$$ LANGUAGE plpgsql;
/