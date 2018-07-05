INSERT INTO system.pref_table (pref_name, pref_value, pref_comment, pref_display_name) VALUES (
  'db_version', '0.15.0', 'Версия БД', 'Версия БД')
ON CONFLICT (pref_name)
  DO UPDATE SET pref_value = EXCLUDED.pref_value;

INSERT INTO system.pref_table (pref_name, pref_value, pref_comment, pref_display_name) VALUES (
  'db_title', 'База разработки', 'Имя/назначение БД для отображения в web-меню (не более 32 символов)',
  'Имя/назначение БД')
ON CONFLICT (pref_name)
  DO UPDATE SET pref_value = EXCLUDED.pref_value;