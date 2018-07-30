CREATE TABLE system.appointment (
  id      BIGINT,
  name    VARCHAR(128) NOT NULL,
  version BIGINT       NOT NULL DEFAULT 0,

  CONSTRAINT pk_appointment PRIMARY KEY (id),
  CONSTRAINT cc_appointment_name CHECK (name <> '')
);

COMMENT ON TABLE system.appointment IS 'Должность, которую может занимать работник';
COMMENT ON COLUMN system.appointment.id IS 'PK';
COMMENT ON COLUMN system.appointment.name IS 'Наименование должности';
COMMENT ON COLUMN system.appointment.version IS 'Счётчик версионности';