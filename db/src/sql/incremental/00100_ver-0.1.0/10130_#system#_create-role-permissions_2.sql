CREATE TABLE IF NOT EXISTS system.role_permissions (
  role_id       BIGINT       NOT NULL,
  permission_id VARCHAR(100) NOT NULL,

  CONSTRAINT pk_role_permission PRIMARY KEY (role_id, permission_id),
  CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES system.role (id),
  CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES system.permission (id)
);

CREATE INDEX IF NOT EXISTS ind_role_permission_role
  ON system.role_permissions (role_id);

CREATE INDEX IF NOT EXISTS ind_role_permission_permission
  ON system.role_permissions (permission_id);

COMMENT ON TABLE system.role_permissions IS 'Содержит назначения привилегии определенной роли';
COMMENT ON COLUMN system.role_permissions.role_id IS 'Ссылка на роль, для которой определяется привилегия';
COMMENT ON COLUMN system.role_permissions.permission_id IS 'Ссылка на привилегию, которая опредеяется для текущей роли';
