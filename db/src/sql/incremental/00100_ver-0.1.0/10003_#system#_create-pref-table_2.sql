CREATE TABLE system.pref_table
(
  pref_name         VARCHAR(32),
  pref_value        VARCHAR(256),
  pref_comment      VARCHAR(2048),
  pref_display_name VARCHAR(64),
  pref_category_id  INTEGER,
  pref_data_type    INTEGER,

  CONSTRAINT pk_pref_table PRIMARY KEY (pref_name)
);

COMMENT ON TABLE system.pref_table IS 'Импортировано из инфраструктуры Аргус
--
таблица содержит  настройки базы
поля:
pref_name - название опции
pref_value - значение опции
pref_comment  - коментарий опции
pref_display_name  - отображаемое имя опции
Типы настроек (влияют на то, как клиент будет их редактировать)
1 - integer
2 - string
3 - boolean
4 - double
5 - color';

COMMENT ON COLUMN system.pref_table.pref_name IS 'название опции';
COMMENT ON COLUMN system.pref_table.pref_value IS 'значение опции';
COMMENT ON COLUMN system.pref_table.pref_comment IS 'коментарий опции';
COMMENT ON COLUMN system.pref_table.pref_display_name IS 'отображаемое имя опции';