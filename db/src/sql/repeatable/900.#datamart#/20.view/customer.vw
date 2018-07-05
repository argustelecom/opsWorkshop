-- datamart.customer_vw
CREATE OR REPLACE VIEW datamart.customer_vw AS
  SELECT
    c.id                              AS customer_id,
    cti.customer_type_id,
    ct.name                           AS customer_type_name,
    p.id                              AS party_id,
    pti.party_type_id,
    pt.name                           AS party_type_name,
    datamart_sys.format_party_name(p) AS customer_full_name,
    p.first_name,
    p.last_name,
    p.second_name,
    p.brand_name,
    p.legal_name,
    cti.properties                    AS customer_props,
    pti.properties                    AS party_props
  FROM system.customer c
    JOIN system.party_role pr ON c.id = pr.id
    JOIN system.party p ON pr.party_id = p.id
    JOIN system.party_type_instance pti ON p.type_instance_id = pti.id
    JOIN system.party_type pt ON pti.party_type_id = pt.id
    JOIN system.customer_type_instance cti ON c.type_instance_id = cti.id
    JOIN system.customer_type ct ON cti.customer_type_id = ct.id;