CREATE OR REPLACE VIEW mediation.tariff_view AS
  SELECT
    id         AS tariff_id,
    name       AS tariff_name,
    rated_unit AS rated_unit_id,
    rounding_policy
  FROM system.tariff;