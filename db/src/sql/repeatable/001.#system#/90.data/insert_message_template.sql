INSERT INTO system.message_template (id, name, content)
VALUES (2, 'Выгрузка реестра сальдо',
        'Здравствуйте!

Реестр сальдо, сформированный за период ${saldoExportIssue.from} - ${saldoExportIssue.to} во вложении к письму.')
ON CONFLICT (id)
  DO UPDATE SET name = EXCLUDED.name,
    content          = EXCLUDED.content;

INSERT INTO system.message_template (id, name, content)
VALUES (3, 'Ошибка при выгрузке реестра сальдо',
        'Здравствуйте!

При формировании реестра сальдо за период ${saldoExportIssue.from} - ${saldoExportIssue.to} возникла ошибка: ${saldoExportIssue.lastEvent.description}.')
ON CONFLICT (id)
  DO UPDATE SET name = EXCLUDED.name,
    content          = EXCLUDED.content;