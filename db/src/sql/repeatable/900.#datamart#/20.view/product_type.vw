CREATE OR REPLACE VIEW datamart.product_type_vw AS
  SELECT
    pt.id   AS product_id,
    pt.name AS product_name,
    pt.status
  FROM system.product_type pt;