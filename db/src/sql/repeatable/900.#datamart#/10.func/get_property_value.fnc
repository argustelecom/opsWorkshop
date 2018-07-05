CREATE OR REPLACE FUNCTION datamart.get_property_value(
  a_type_id            IN BIGINT,
  a_property_container IN JSONB,
  a_property_keyword   IN VARCHAR
) RETURNS VARCHAR
AS $$
DECLARE
  l_property_info RECORD;

  l_value_raw     VARCHAR;
  l_value_bin     JSONB;
  l_entity_id     BIGINT;
  l_entity_raw    VARCHAR;

  l_mv_factor     INTEGER;
  l_mv_symbol     VARCHAR;
BEGIN
  SELECT
    id                 AS property_id,
    dtype              AS property_dtype,
    dtype || '-' || id AS property_qualified_name,
    keyword            AS property_keyword,
    status             AS property_status
  FROM system.type_property
  WHERE
    holder_id = a_type_id
    AND keyword = a_property_keyword
  INTO l_property_info;

  IF l_property_info IS NULL OR l_property_info.property_status != 'ACTIVE' THEN
    RETURN NULL;
  END IF;

  -- значение текстового свойства, дробного числового, целого числового
  IF l_property_info.property_dtype = ANY (ARRAY ['TextProperty', 'DoubleProperty', 'LongProperty'] :: VARCHAR []) THEN
    SELECT (a_property_container ->> l_property_info.property_qualified_name) :: VARCHAR
    INTO l_value_raw;

    RETURN trim(trim(l_value_raw), '"');
  END IF;

  -- значение свойства "массив строк"
  IF l_property_info.property_dtype = 'TextArrayProperty' THEN
    SELECT a_property_container -> l_property_info.property_qualified_name
    INTO l_value_bin;

    IF l_value_bin IS NULL OR jsonb_typeof(l_value_bin) != 'array' THEN
      RETURN NULL;
    END IF;

    SELECT string_agg(regexp_replace(a, '[\r\n]', '', 'g'), ', ')
    FROM jsonb_array_elements_text(l_value_bin) a
    INTO l_value_raw;

    RETURN l_value_raw;
  END IF;

  -- значение пользовательского справочника
  IF l_property_info.property_dtype = 'LookupProperty' THEN
    SELECT (a_property_container ->> l_property_info.property_qualified_name) :: VARCHAR
    INTO l_value_raw;

    l_entity_id := datamart_sys.extract_entity_id(l_value_raw, ARRAY ['LookupEntry-'] :: VARCHAR []);

    IF l_entity_id IS NULL THEN
      RETURN NULL;
    END IF;

    SELECT (CASE WHEN le.active THEN le.name ELSE le.name || ' *' END) INTO l_entity_raw
    FROM system.lookup_entry le
    WHERE le.id = l_entity_id;

    RETURN l_entity_raw;
  END IF;

  -- значение измеряемой величины
  IF l_property_info.property_dtype = 'MeasuredProperty' THEN
    SELECT
      (a_property_container #>> ARRAY [l_property_info.property_qualified_name, 'measureUnit']) :: VARCHAR,
      (a_property_container #>> ARRAY [l_property_info.property_qualified_name, 'storedValue']) :: NUMERIC
    INTO
      l_entity_raw,
      l_value_raw;

    IF l_entity_raw IS NULL OR l_value_raw IS NULL THEN
      RETURN NULL;
    END IF;

    l_entity_id := datamart_sys.extract_entity_id(
        l_entity_raw,
        ARRAY ['DerivedMeasureUnit-', 'BaseMeasureUnit-', 'MeasureUnit-'] :: VARCHAR []
    );

    IF l_entity_id IS NULL THEN
      RETURN NULL;
    END IF;

    SELECT
      symbol,
      factor
    INTO
      l_mv_symbol,
      l_mv_factor
    FROM system.measure_unit
    WHERE id = l_entity_id;

    IF l_mv_factor IS NOT NULL THEN
      RETURN trim(datamart.round_half_even(l_value_raw :: NUMERIC / l_mv_factor) :: VARCHAR || ' ' || l_mv_symbol);
    END IF;
  END IF;

  -- необрабатываемый тип свойства
  RETURN NULL;
END;
$$
LANGUAGE plpgsql STABLE COST 100
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/