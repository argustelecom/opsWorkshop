CREATE OR REPLACE VIEW mediation.tariff_entry_view AS
  SELECT
    id      AS tariff_entry_id,
    tariff_id,
    name    AS tariff_entry_name,
    p       AS prefix,
    charge_per_unit,
    zone_id AS telephony_zone_id
  FROM system.tariff_entry, unnest(prefix) AS p;