CREATE TABLE IF NOT EXISTS system.login (
  uid         BIGINT      NOT NULL,
  username    VARCHAR(50) NOT NULL,
  password    VARCHAR(128),
  salt        VARCHAR(128),
  description VARCHAR(250),
  email       VARCHAR(100),
  employee_id BIGINT,
  logon_time  TIMESTAMP,
  expiry_date TIMESTAMP,
  lock_date   TIMESTAMP,
  created     TIMESTAMP   DEFAULT current_timestamp,
  time_zone   VARCHAR(64) DEFAULT 'Europe/Moscow',
  locale      VARCHAR(64) DEFAULT 'ru-RU', /*локаль в формате LanguageTag*/
  is_sys      BOOLEAN     DEFAULT FALSE,

  CONSTRAINT pk_login PRIMARY KEY (uid),
  CONSTRAINT fk_login_employee FOREIGN KEY (employee_id) REFERENCES system.employee (id),
  CONSTRAINT uc_login_username UNIQUE (username)
);

CREATE INDEX IF NOT EXISTS ind_login_employee
  ON system.login (employee_id);

COMMENT ON TABLE system.login IS 'Учетные данные пользователей, зарегистрированных в системе';
COMMENT ON COLUMN system.login.uid IS 'Уникальное идентификатор пользователя';
COMMENT ON COLUMN system.login.username IS 'Уникальное имя пользователя';
COMMENT ON COLUMN system.login.password IS 'Захешированный пароль пользователя';
COMMENT ON COLUMN system.login.description IS 'Описание текущего пользователя';
COMMENT ON COLUMN system.login.email IS 'Основной email пользователя, используемый для оповещения об изменении в его учетных данных';
COMMENT ON COLUMN system.login.employee_id IS 'Ссылка на сотрудника, для которго определен текущий логин';
COMMENT ON COLUMN system.login.logon_time IS 'Дата и время последнего логина текущего пользователя';
COMMENT ON COLUMN system.login.expiry_date IS 'Дата устаревания пароля текущего пользователя. После достижения этой даты пользователь должен поменять пароль на новый. Если null, то пароль не устареет никогда';
COMMENT ON COLUMN system.login.is_sys IS 'Если 1, то пользователь считается системным и не может быть удален при помощи инструмента администрирования';
COMMENT ON COLUMN system.login.created IS 'Дата и время создания логина';
COMMENT ON COLUMN system.login.lock_date IS 'Дата и время блокирования логина';