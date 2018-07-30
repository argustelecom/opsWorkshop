CREATE TABLE IF NOT EXISTS system.role (
  id          BIGINT NOT NULL,
  name        VARCHAR(100),
  description VARCHAR(250),
  status      VARCHAR(20)     DEFAULT 'ACTIVE',
  is_sys      BOOLEAN         DEFAULT FALSE,
  version     BIGINT NOT NULL DEFAULT 1,

  CONSTRAINT pk_business_role PRIMARY KEY (id)
);

COMMENT ON TABLE system.role IS 'Содержит перечень настраиваемых администратором пользовательских ролей. Роли определяют какими привилегиями обладает текущий пользователь';
COMMENT ON COLUMN system.role.id IS 'Уникальный идентификатор пользовательской роли, формируется автоматически';
COMMENT ON COLUMN system.role.name IS 'Понятное пользователю краткое наименование роли';
COMMENT ON COLUMN system.role.description IS 'Понятное пользователю подробное описание роли';
COMMENT ON COLUMN system.role.status IS 'Статус роли, одно из значений "ACTIVE", "DEPRECATED", "DISABLED"';
COMMENT ON COLUMN system.role.is_sys IS 'Если 1, то роль считается системной и не может быть удалена или изменена';
