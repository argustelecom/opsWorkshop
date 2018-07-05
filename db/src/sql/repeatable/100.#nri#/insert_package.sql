INSERT INTO system.entity_package (entity_package_id, entity_package_name, package_desc, scheme_name, depends_on_entity_package_id, is_sys, appserver_project)
VALUES (777, 'box-nri', 'Технический учёт', 'nri', 1, FALSE, 'nri')
ON CONFLICT (entity_package_id)
  DO NOTHING;