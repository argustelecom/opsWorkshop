CREATE TABLE system.party (
  id          BIGINT,
  dtype       VARCHAR(32) NOT NULL,
  prefix      VARCHAR(16),
  first_name  VARCHAR(64),
  second_name VARCHAR(64),
  last_name   VARCHAR(64),
  suffix      VARCHAR(16),
  birthday    DATE,
  legal_name  VARCHAR(256),
  brand_name  VARCHAR(256),
  version     BIGINT      NOT NULL DEFAULT 0,
  CONSTRAINT pk_party PRIMARY KEY (id)
);

COMMENT ON TABLE system.party IS 'Участники: физ. лица и организации. Данные хранятся в Single Table';
COMMENT ON COLUMN system.party.id IS 'PK';
COMMENT ON COLUMN system.party.dtype IS 'Тип участника(дискриминатор) определяющийся посредством наследования сущностей';
COMMENT ON COLUMN system.party.prefix IS 'Префикс, который будет добавлен перед именем (заполняется только для физ. лица)';
COMMENT ON COLUMN system.party.first_name IS 'Имя (заполняется только для физ. лица)';
COMMENT ON COLUMN system.party.second_name IS 'Отчество (заполняется только для физ. лица)';
COMMENT ON COLUMN system.party.last_name IS 'Фамилия (заполняется только для физ. лица)';
COMMENT ON COLUMN system.party.suffix IS 'Cуффикс, который будет добавлен после имени (заполняется только для физ. лица)';
COMMENT ON COLUMN system.party.birthday IS 'Дата дня рождения (заполняется только для физ. лица)';
COMMENT ON COLUMN system.party.legal_name IS 'Официальное/Юридическое наименование организации (заполняется только для организации)';
COMMENT ON COLUMN system.party.brand_name IS 'Наименование торговой марки (заполняется только для организации)';
COMMENT ON COLUMN system.party.version IS 'Счётчик версионности';