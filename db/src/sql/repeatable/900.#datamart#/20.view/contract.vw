CREATE OR REPLACE VIEW datamart.contract_vw AS
  SELECT
    c.id              AS contract_id,
    c.dtype           AS contract_entity,
    c.customer_id,
    c.document_number AS contract_number,
    c.document_date   AS contract_date,
    c.creation_date,
    c.contract_type_id,
    ct.name           AS contract_type_name,
    c.valid_from,
    c.valid_to,
    c.state,
    c.contract_id     AS parent_contract_id
  FROM system.contract c
    JOIN system.contract_type ct ON c.contract_type_id = ct.id;