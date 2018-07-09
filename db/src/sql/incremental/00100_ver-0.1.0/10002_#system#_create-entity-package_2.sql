CREATE TABLE IF NOT EXISTS system.entity_package (
  entity_package_id            BIGINT NOT NULL,
  entity_package_name          VARCHAR(100),
  package_desc                 VARCHAR(250),
  scheme_name                  VARCHAR(32),
  depends_on_entity_package_id BIGINT,
  is_sys                       BOOLEAN DEFAULT FALSE,
  appserver_project            VARCHAR(64),
  CONSTRAINT pk_entity_package PRIMARY KEY (entity_package_id),
  CONSTRAINT fk_entity_package_depends_on FOREIGN KEY (depends_on_entity_package_id) REFERENCES system.entity_package (entity_package_id)
);

CREATE INDEX IF NOT EXISTS ind_module_depends_on
  ON system.entity_package (depends_on_entity_package_id);

ALTER TABLE system.entity_package
  ALTER COLUMN appserver_project SET NOT NULL;

CREATE UNIQUE INDEX uc_entity_package_appserver_project
  ON system.entity_package (appserver_project);

COMMENT ON TABLE system.entity_package IS 'Содержит перечень всех развернутых на текущий момент модулей системы';
COMMENT ON COLUMN system.entity_package.entity_package_id IS 'Уникальный идентификатор модуля, определяется разработчиками';
COMMENT ON COLUMN system.entity_package.entity_package_name IS 'Системное наименования модуля';
COMMENT ON COLUMN system.entity_package.package_desc IS 'Понятное пользователю описание модуля';
COMMENT ON COLUMN system.entity_package.scheme_name IS 'Наименование схемы, содержащей структуры данных для текущего модуля';
COMMENT ON COLUMN system.entity_package.depends_on_entity_package_id IS 'Ссылка на модуль, от которого зависит текущий модуль';
COMMENT ON COLUMN system.entity_package.is_sys IS 'TRUE, если системный. Системный пакет всегда развёрнут, не является опцией поставки.';
