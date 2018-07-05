INSERT INTO system.commodity_type_group (id, name, keyword, parent_id)
VALUES
  (nextval('system.gen_object_id'), 'Товары', 'GOODS', NULL),
  (nextval('system.gen_object_id'), 'Услуги', 'SERVICES', NULL)
ON CONFLICT ON CONSTRAINT uc_commodity_type_group_keyword
  DO UPDATE SET
    name      = excluded.name,
    parent_id = excluded.parent_id;