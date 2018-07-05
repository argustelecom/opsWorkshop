CREATE OR REPLACE VIEW datamart.ip_address_vw AS
  SELECT
    adr.id,
    adr.name AS ip_address,
    adr.is_static,
    adr.is_private,
    adr.transfer_type,
    adr.purpose,
    adr.ip_address_state,
    adr.loading_id,
    lo.service_id
  FROM
    nri.ip_address adr
    LEFT JOIN nri.resource_loading lo ON adr.loading_id = lo.id;