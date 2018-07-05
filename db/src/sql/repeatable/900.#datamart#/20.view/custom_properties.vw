CREATE OR REPLACE VIEW datamart.custom_properties_vw AS
  WITH property_holder AS (
    SELECT
      property_holder_id,
      category,
      name,
      'CustomerProperty' AS property_category
    FROM system.customer_type
    UNION ALL
    SELECT
      property_holder_id,
      category,
      name,
      'PartyProperty' AS property_category
    FROM system.party_type)
  SELECT
    ph.property_category            AS property_category,
    ph.category                     AS holder_category,
    ph.name                         AS holder_name,
    tp.name                         AS property_name,
    tp.keyword                      AS property_user_keyword,
    concat_ws('-', tp.dtype, tp.id) AS property_system_keyword
  FROM system.type_property tp
    JOIN property_holder ph ON tp.holder_id = ph.property_holder_id
  WHERE tp.status = 'ACTIVE'
  ORDER BY 1;