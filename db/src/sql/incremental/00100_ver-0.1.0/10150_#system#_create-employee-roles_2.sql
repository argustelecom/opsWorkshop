CREATE TABLE IF NOT EXISTS system.employee_roles (
  employee_id BIGINT NOT NULL,
  role_id     BIGINT NOT NULL,

  CONSTRAINT pk_employee_roles PRIMARY KEY (employee_id, role_id),
  CONSTRAINT fk_employee_roles_employee FOREIGN KEY (employee_id) REFERENCES system.employee (id),
  CONSTRAINT fk_employee_roles_role FOREIGN KEY (role_id) REFERENCES system.role (id)
);

CREATE INDEX IF NOT EXISTS ind_employee_roles_employee
  ON system.employee_roles (employee_id);

CREATE INDEX IF NOT EXISTS ind_employee_roles_role
  ON system.employee_roles (role_id);

COMMENT ON TABLE system.employee_roles IS 'Содержит назначения личных пользовательских ролей определенному сотруднику организации';
COMMENT ON COLUMN system.employee_roles.employee_id IS 'Ссылка на сотрудника, для которго определяется личная пользовательская роль';
COMMENT ON COLUMN system.employee_roles.role_id IS 'Ссылка на личную пользовательскую роль, которая опредеяется для текущего сотрудника';