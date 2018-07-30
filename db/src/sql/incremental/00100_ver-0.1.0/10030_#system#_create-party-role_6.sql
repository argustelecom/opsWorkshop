CREATE TABLE system.party_role (
  id       BIGINT,
  dtype    VARCHAR(32) NOT NULL,
  party_id BIGINT      NOT NULL,
  version  BIGINT      NOT NULL DEFAULT 0,

  CONSTRAINT pk_party_role PRIMARY KEY (id),
  CONSTRAINT fk_party_role_party FOREIGN KEY (party_id) REFERENCES system.party (id)
);

CREATE INDEX IF NOT EXISTS ind_party_role_party
  ON system.party_role (party_id);

COMMENT ON TABLE system.party_role IS 'Роли участников: работник, клиент, представительно компании и др.';
COMMENT ON COLUMN system.party_role.id IS 'PK';
COMMENT ON COLUMN system.party_role.dtype IS 'Тип роли(дискриминатор) определяющийся посредством наследования сущностей';
COMMENT ON COLUMN system.party_role.version IS 'Счётчик версионности';


