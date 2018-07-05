CREATE TABLE IF NOT EXISTS system.http_session_history
(
  http_session_number BIGINT NOT NULL,
  http_session_id     VARCHAR(1024),
  user_name           VARCHAR(50),
  login_id            BIGINT,
  worker_id           BIGINT,
  logon_time          TIMESTAMP,
  logoff_time         TIMESTAMP,

  CONSTRAINT pk_http_session_history PRIMARY KEY (http_session_number)
);

CREATE INDEX IF NOT EXISTS ind_http_ses_hist_http_sess_id
  ON system.http_session_history (http_session_id);
CREATE INDEX IF NOT EXISTS ind_http_ses_hist_login
  ON system.http_session_history (login_id);
CREATE INDEX IF NOT EXISTS ind_http_ses_hist_logoff_time
  ON system.http_session_history (logoff_time);
CREATE INDEX IF NOT EXISTS ind_http_ses_hist_logon_time
  ON system.http_session_history (logon_time);
CREATE INDEX IF NOT EXISTS ind_http_ses_hist_user
  ON system.http_session_history (user_name);
CREATE INDEX IF NOT EXISTS ind_http_ses_hist_worker
  ON system.http_session_history (worker_id);

COMMENT ON TABLE system.http_session_history IS 'Таблица содержит историю http сессий со временем входа и выхода пользователей';
COMMENT ON COLUMN system.http_session_history.http_session_number IS 'ID записи в таблице';
COMMENT ON COLUMN system.http_session_history.http_session_id IS 'Идентификатор http сессии';
COMMENT ON COLUMN system.http_session_history.user_name IS 'Пользователь системы';
COMMENT ON COLUMN system.http_session_history.login_id IS 'ID логина';
COMMENT ON COLUMN system.http_session_history.worker_id IS 'ID работника';
COMMENT ON COLUMN system.http_session_history.logon_time IS 'Дата выхода пользователя';