CREATE TABLE IF NOT EXISTS system.permission (
  id                VARCHAR(100) NOT NULL,
  parent_id         VARCHAR(100),
  name              VARCHAR(100),
  description       VARCHAR(250),
  entity_package_id BIGINT,

  CONSTRAINT pk_permission PRIMARY KEY (id),
  CONSTRAINT fk_permission_parent FOREIGN KEY (parent_id) REFERENCES system.permission (id),
  CONSTRAINT fk_permission_entity_package FOREIGN KEY (entity_package_id) REFERENCES system.entity_package (entity_package_id)
);

CREATE INDEX IF NOT EXISTS ind_permission_parent
  ON system.permission (parent_id);

CREATE INDEX IF NOT EXISTS ind_permission_module
  ON system.permission (entity_package_id);

COMMENT ON TABLE system.permission IS 'Содержит основные системные привилегии, регулирующие доступ пользователя к представлениям, фреймам и функциям. Записи в эту таблицу могут быть добавлены только разработчиками при реализации соответсвтующей функциональности';
COMMENT ON COLUMN system.permission.id IS 'Уникальный строковый идентификатор привилегии';
COMMENT ON COLUMN system.permission.parent_id IS 'Ссылка на родительскую привилегию, без которой текущая привилегия не имеет смысла. Если назначена текущая привилегия, а родительская нет, то родительская привилегия назначается неявно. Подобное правило применяется для всего дерева привилегий';
COMMENT ON COLUMN system.permission.name IS 'Понятное пользователю наименование привилегии';
COMMENT ON COLUMN system.permission.description IS 'Понятное пользователю описание привилегии';
COMMENT ON COLUMN system.permission.entity_package_id IS 'Ссылка на модуль, в котором реализована функция, защищаемая данной привилегией';