CREATE TABLE system.employee (
  id               BIGINT,
  appointment_id   BIGINT,
  personnel_number VARCHAR(64) NOT NULL,
  fired            BOOLEAN     NOT NULL DEFAULT FALSE,

  CONSTRAINT pk_employee PRIMARY KEY (id),
  CONSTRAINT fk_employee_ancestor FOREIGN KEY (id) REFERENCES system.party_role (id),
  CONSTRAINT fk_employee_appointment FOREIGN KEY (appointment_id) REFERENCES system.appointment (id),
  CONSTRAINT uc_employee_personnel_number UNIQUE (personnel_number)
);

CREATE INDEX IF NOT EXISTS ind_employee_appointment
  ON SYSTEM.employee (appointment_id);

COMMENT ON TABLE system.employee IS 'Роль описывающая участника как работника';
COMMENT ON COLUMN system.employee.id IS 'PK';
COMMENT ON COLUMN system.employee.appointment_id IS 'Должность, которую занимает работник';
COMMENT ON COLUMN system.employee.personnel_number IS 'Табельный номер работника';
COMMENT ON COLUMN system.employee.fired IS 'Флаг определяющий уволен работник или нет';