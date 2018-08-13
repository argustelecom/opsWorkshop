INSERT INTO system.entity_package (entity_package_id, entity_package_name, package_desc, scheme_name, depends_on_entity_package_id, is_sys, appserver_project)
VALUES (1, 'System', 'Основные системные понятия', 'system', NULL, TRUE, 'ops-inf')
ON CONFLICT (entity_package_id)
  DO NOTHING;