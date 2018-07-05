CREATE OR REPLACE VIEW datamart.service_properties_vw AS
  SELECT
    ct.dtype                        AS holder_category,
    ct.name                         AS holder_name,
    tp.name                         AS property_name,
    tp.keyword                      AS property_user_keyword,
    concat_ws('-', tp.dtype, tp.id) AS property_system_keyword
  FROM
    system.type_property tp
    JOIN system.commodity_type ct ON tp.holder_id = ct.property_holder_id;