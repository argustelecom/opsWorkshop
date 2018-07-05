CREATE OR REPLACE VIEW datamart.company_info_params_vw AS
  SELECT
    keyword AS param_keyword,
    name    AS param_name,
    value   AS param_value
  FROM system.owner_parameter ci;