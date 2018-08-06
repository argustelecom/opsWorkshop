-- роль с id = 1 считается системной и создается в скрипте insert-superuser.sql
INSERT INTO system.role_permissions (role_id, permission_id)
  SELECT
    1,
    id
  FROM system.permission
  WHERE id NOT IN ('System_FullLifecycleEdit', 'Bpms_ProcessCancel')
ON CONFLICT (role_id, permission_id)
  DO NOTHING;

