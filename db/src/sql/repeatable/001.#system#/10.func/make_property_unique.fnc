--Создает индексы для характеристики
--Возвращает идентификаторы экземпляров типа с повторяющимися значениями, иначе NULL, означающее, индексы были созданы
CREATE OR REPLACE FUNCTION system.make_property_unique(index_schema               VARCHAR,
                                                       index_table_name           VARCHAR,
                                                       sequence_name              VARCHAR,
                                                       instance_schema            VARCHAR,
                                                       instance_table             VARCHAR,
                                                       instance_id_column         VARCHAR,
                                                       instance_properties_column VARCHAR,
                                                       property_id                BIGINT,
                                                       property_qualifier         VARCHAR)
  RETURNS SETOF BIGINT
AS $$
DECLARE
  duplicate_ids BIGINT [];
BEGIN
  SELECT array_agg(result)
  FROM system.find_instance_with_duplicate_values(instance_schema, instance_table, instance_id_column,
                                                  instance_properties_column, property_qualifier) result
  INTO duplicate_ids;

  IF duplicate_ids IS NULL
  THEN
    BEGIN
      EXECUTE format('INSERT INTO %I.%I(id, instance_id, property_id, value)
                        SELECT
                          nextval($1),
                          %I,
                          $2,
                          %I ->> $3
                        FROM %I.%I
                        WHERE jsonb_strip_nulls(%I) -> $3 IS NOT NULL'
      , index_schema, index_table_name, instance_id_column, instance_properties_column,
                     instance_schema, instance_table, instance_properties_column)
      USING sequence_name, property_id, property_qualifier;
    END;
  END IF;
  RETURN QUERY SELECT *
               FROM unnest(duplicate_ids);
END;
$$
LANGUAGE plpgsql;
/