--Создает запись в таблице с индексами характеристик.
--В случае конфликта (instance_id, property_id), происходит обновление индекса.
--Если значение равно NULL, не делаем запись и удаляем старый индекс
CREATE OR REPLACE FUNCTION system.property_index(index_schema  VARCHAR,
                                                 index_table   VARCHAR,
                                                 sequence_name VARCHAR,
                                                 instance_id   BIGINT,
                                                 property_id   BIGINT,
                                                 value         VARCHAR)
  RETURNS BOOLEAN
AS $$
DECLARE
  inserted BOOLEAN DEFAULT FALSE;
BEGIN
  IF value IS NOT NULL
  THEN
    BEGIN
      EXECUTE format('INSERT INTO %I.%I(id, instance_id, property_id, value) VALUES (nextval($1), $2, $3, $4)
                     ON CONFLICT (instance_id, property_id) DO UPDATE SET value = EXCLUDED.value',
                     index_schema, index_table)
      USING sequence_name, instance_id, property_id, value;
      inserted := TRUE;
    END;
  ELSE
    BEGIN
      EXECUTE format('DELETE FROM %I.%I WHERE instance_id = $1 AND property_id = $2', index_schema, index_table)
      USING instance_id, property_id;
    END;
  END IF;
  RETURN inserted;
END;
$$
LANGUAGE plpgsql;
/