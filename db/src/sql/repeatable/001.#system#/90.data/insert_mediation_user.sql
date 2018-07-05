-- Супер-пользователь
INSERT INTO system.party (id, dtype, first_name, last_name, version)
VALUES (3, 'Person', 'Очередь', 'обработки', 3)
ON CONFLICT (id)
  DO NOTHING;

INSERT INTO system.party_role (id, dtype, party_id, version)
VALUES (3, 'Employee', 3, 1)
ON CONFLICT (id)
  DO NOTHING;

INSERT INTO system.employee (id, personnel_number) VALUES (3, '0002')
ON CONFLICT (id)
  DO NOTHING;

--queue/dutyFr33!
INSERT INTO system.login (uid, username, password, salt, description, employee_id, time_zone, is_sys)
VALUES (nextval('system.gen_login_id'), 'queue',
        '55df807398d1c3e0372d0fa5d269978b3fe05b25e4bc9f12813e54fd43757f23d4a1f38fb936c72eb942819e1a34f879fac37283b65ead4c380692e8d02dae0e',
        '3ocdzbPuBxRyj/81l8E/xsmfhlJv.k5lGFWVmgk.eJ48BNtgKybobmsnhBrW2NcWsw09o6Gkjtook0SN2mlmRW', 'Очередь обработки', 3,
        'Europe/Moscow', TRUE)
ON CONFLICT (username)
  DO NOTHING;

INSERT INTO system.role (id, name, description, status, version, is_sys)
VALUES (3, 'Очередь обработки', NULL, 'ACTIVE', 1, TRUE)
ON CONFLICT (id)
  DO NOTHING;

INSERT INTO system.employee_roles (employee_id, role_id)
VALUES (3, 3)
ON CONFLICT (employee_id, role_id)
  DO NOTHING;