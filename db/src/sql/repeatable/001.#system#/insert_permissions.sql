-- SYSTEM

-- Сотрудники
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_PersonalView', NULL, 'Просмотр сотрудников',
        'Право позволяет просматривать информацию о сотрудниках в предоставляемых экранных формах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_EmployeeEdit', 'System_PersonalView', 'Редактирование данных о сотруднике',
        'Право позволяет создавать, редактировать информацию о сотруднике ', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_DeleteEmployee', 'System_PersonalView', 'Увольнение сотрудника',
        'Право позволяет увольнять сотрудника', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_ChangeEmployeeLogin', 'System_PersonalView', 'Редактирование учетных записей сотрудников',
        'Право доступа позволяет управлять учетными записями сотрудников', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_ChangeEmployeePermissions', 'System_PersonalView', 'Редактирование привилегий сотрудников',
        'Право доступа позволяет управлять набором прав доступа сотрудников', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Бизнес-роли
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_RoleView', NULL, 'Просмотр бизнес-ролей',
        'Право позволяет просматривать информацию о бизнес-ролях в предоставляемых экранных формах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_RoleEdit', 'System_RoleView', 'Создание и редактирование бизнес-роли',
        'Право позволяет создавать и редактировать основные атрибуты бизнес-роли', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_ChangeRolePermissions', 'System_RoleView', 'Редактирование привилегий бизнес-роли',
        'Право позволяет управлять набором прав доступа бизнес-роли', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Структура адресных объектов
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_AddressStructureEdit', NULL, 'Просмотр и редактирование структуры адресных объектов ',
        'Право позволяет просматривать справочник "Структура адресных объектов", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Типы адресных объектов
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_AddressObjectsTypesEdit', NULL, 'Просмотр и редактирование типов адресных объектов',
        'Право позволяет просматривать справочник "Типы адресных объектов", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Уровни адресных объектов
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_AddressObjectsLevelsEdit', NULL, 'Просмотр и редактирование уровней адресных объектов',
        'Право позволяет просматривать справочник "Уровни адресных объектов", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Типы товаров / услуг
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_CommodityTypeEdit', NULL, 'Просмотр, создание, редактирование и удаление типов товаров /услуг',
        'Право позволяет просматривать справочник "Типы товаров / услуг", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_CommodityTypePropertyEdit', 'System_CommodityTypeEdit',
        'Изменение перечня характеристик',
        'Право позволяет изменять перечень характеристик, описывающих спецификацию типа товара/услуги', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Сведения о компании
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_OwnerView', NULL, 'Доступ к представлению "Сведения о компании"',
        'Право доступа позволяет просматривать сведения о компании-владельце', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_OwnerEdit', 'System_OwnerView', 'Создание, изменение характеристик в сведениях о компании',
        'Право позволяет создавать, изменять и удалять характеристики в сведениях о компании', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Сегменты клиентов
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_CustomerSegmentsEdit', NULL, 'Просмотр и редактирование сегментов клиентов',
        'Право доступа позволяет просматривать справочник "Сегменты клиентов", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Типы документов
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_DocumentTypesEdit', NULL, 'Просмотр и редактирование типов документов',
        'Право позволяет просматривать справочник "Типы документов", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_DocumentTypeTypeEdit', 'System_DocumentTypesEdit', 'Редактирование характеристик типа документа',
        'Право позволяет создавать, удалять и редактировать характеристики типов документов', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_DocumentTypePatternAdd', 'System_DocumentTypesEdit', 'Добавление шаблона печатной формы',
        'Право позволяет добавлять шаблоны печатных форм документа', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Типы контактов
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_ContactsTypesEdit', NULL, 'Просмотр и редактирование типов контактов',
        'Право позволяет просматривать справочник "Типы контактов", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Должности
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_AppointmentEdit', NULL, 'Просмотр и редактирование должностей',
        'Право позволяет просматривать справочник "Должности", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Справочник единиц измерения
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_MeasureUnitEdit', NULL, 'Просмотр и редактирование справочника единиц измерения',
        'Право позволяет просматривать "Справочник единиц измерения", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Типы участников
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_PartyTypeEdit', NULL, 'Просмотр и редактирование типов участников',
        'Право позволяет просматривать справочник "Типы участников", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_PartyTypePropertyEdit', 'System_PartyTypeEdit', 'Редактирование характеристик типов участников',
        'Право позволяет создавать, удалять и редактировать характеристики типов участников', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Типы клиентов
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_CustomerTypeEdit', NULL, 'Просмотр и редактирование типов клиентов',
        'Право позволяет просматривать справочник "Типы клиентов", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_CustomerTypePropertyEdit', 'System_CustomerTypeEdit', 'Редактирование характеристик типов клиентов',
        'Право позволяет создавать, удалять и редактировать характеристики типов клиентов', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Пользовательские справочники
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_LookupDirectoryEdit', NULL, 'Просмотр и редактирование пользовательских справочников',
        'Право позволяет просматривать "Пользовательские справочники", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Зона охвата
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_CoverageEdit', NULL, 'Просмотр и редактирование зоны охвата',
        'Право позволяет просматривать справочник "Зона охвата", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Шаблоны электронных писем

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_MailTemplatesEdit', NULL, 'Просмотр и редактирование шаблонов электронных писем',
        'Право позволяет просматривать и редактировать шаблоны электронных писем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Управление очередью обработки

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_QueueManagerView', NULL, 'Просмотр очереди обработки',
        'Право позволяет просматривать очередь обработки', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_QueueManagerStatusEdit', 'System_QueueManagerView', 'Управление статусом очереди обработки',
        'Право позволяет активировать и деактивировать очередь обработки', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Задачи

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_TaskListView', NULL, 'Просмотр информации о задачах о задачах',
        'Право позволяет просматривать информацию о задачах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_TaskListAssignPerformer', 'System_TaskListView', 'Возможность назначить задачи на выбранного исполнителя',
        'Право дает возможность назначить задачи на выбранного исполнителя', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_TaskPerform', 'System_TaskListView', 'Дает возможность на завершение задачи',
        'Право дает возможность на завершение задачи', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Услуги

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_ServiceView', NULL, 'Просмотр экземпляров услуг',
        'Право позволяет просматривать информацию об услугах и параметрах услуг', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_ServiceEdit', 'System_ServiceView', 'Редактирование параметров экземпляров услуг',
        'Право позволяет редактировать значения парамтеров экземпляров услуг, которые не были заполнены на уровне спецификации', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Логические ресурсы

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_ResourceEdit', NULL, 'Просмотр и редактирование блока логических ресурсов',
        'Право позволяет смотреть и редактировать информацию о логических ресурсах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Характеристики

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_LockedTypePropertiesEdit', NULL, 'Редактирование заблокированных значений параметров',
        'Право позволяет редактировать заблокированные значения параметров экземпляра объекта', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Отчеты

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_ReportTypesEdit', NULL, 'Доступ к редактору отчетов',
        'Право предоставляет доступ к форме Редактор отчетов', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_ReportsMapView', NULL, 'Доступ к карте отчетов',
        'Право позволяет строить и выгружать в файл отчеты', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

--BILLING

-- Лицевой счет
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_PersonalAccountView', NULL, 'Просмотр лицевых счетов',
        'Право позволяет просматривать информацию о лицевых счетах в предоставляемых экранных формах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_PersonalAccountEdit', 'Billing_PersonalAccountView',
        'Создание и редактирование лицевого счета',
        'Право позволяет создавать и редактировать информацию о лицевом счете', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_PersonalAccountStatusEdit', 'Billing_PersonalAccountView', 'Управление статусом лицевого счета',
        'Право позволяет изменять статус лицевого счета', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Инвойсы
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_InvoiceView', NULL, 'Просмотр инвойсов',
        'Право позволяет просматривать информацию об инвойсах в предоставляемых экранных формах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_InvoiceEdit', 'Billing_InvoiceView', 'Создание и редактирование инвойсов',
        'Право позволяет создавать, редактировать информацию об инвойсах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_InvoicesEditStatus', 'Billing_InvoiceView', 'Массовое изменение статусов инвойсов',
        'Право позволяет выполнять операции массовой смены статусов инвойсов', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Подписки
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_SubscriptionView', NULL, 'Просмотр подписок',
        'Право позволяет просматривать информацию о подписках в предоставляемых экранных формах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_SubscriptionEdit', 'Billing_SubscriptionView', 'Создание и редактирование подписки',
        'Право позволяет создавать, редактировать информацию о подписке', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Транзакции
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_TransactionView', NULL, 'Просмотр транзакций',
        'Право позволяет просматривать информацию о транзакциях в предоставляемых экранных формах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_TransactionCreate', 'Billing_TransactionView', 'Создание транзакции',
        'Право позволяет создавать транзакции', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Экспорт реестра Сальдо

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_SaldoExportView', NULL, 'Просмотр страницы с выгрузками реестра Сальдо',
        'Право позволяет просматривать и скачивать информацию о выгрузках реестра Сальдо в предоставляемых экранных формах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_SaldoExportContactListEdit', 'Billing_SaldoExportView', 'Настройка контактов для информирования о выгрузки реестра Сальдо',
        'Право позволяет настраивать перечень контактов для отправки файла выгрузки и информировании об ошибке', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_SaldoExportEdit', 'Billing_SaldoExportContactListEdit', 'Управление выгрузками реестра Сальдо',
        'Право позволяет управлять параметрами выгрузки реестра Сальдо', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Импорт реестра Сальдо
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_SaldoImportView', NULL, 'Доступ к представлению "Импорт реестров Сальдо"',
        'Право позволяет осуществлять импорт платежных документов в систему', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_PaymentDocEdit', 'Billing_SaldoImportView', 'Корректировка данных для импорта реестра Сальдо',
        'Право позволяет указывать лицевой счет и сумму платежа для необработанных автоматически записей', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Счет
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_BillView', NULL, 'Просмотр счетов на оплату',
        'Право доступа позволяет просматривать информацию счетах на оплату, выполнять экспорт печатной формы', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_BillRecalculation', 'Billing_BillView', 'Перерасчет по счету на оплату	',
        'Право позволяет указывать лицевой счет и сумму платежа для необработанных автоматически записей', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Привилегии
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_PrivilegeView', NULL, 'Просмотр информации о привилегиях клиента',
        'Право доступа позволяет просматривать информацию о привилегиях: созданных доверительных и пробных периодах предоставления', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_PrivilegeEdit', 'Billing_PrivilegeView', 'Создание и закрытие привилегий',
        'Право доступа позволяет создавать доверительные периоды предоставления по подписке, лицевому счету, клиенту, продлять действующий пробный период, а также досрочно закрывать действующий доверительный и пробный период', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Зоны телефонной нумерации
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_TelephonyZoneEdit', NULL, 'Доступ к представлению "Зоны телефонной нумерации"',
        'Право доступа позволяет просматривать, создавать, изменять зоны телефонной нумерации', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Правила формирования начислений по телефонным вызовам
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_UsageInvoiceSettingsView', NULL, 'Просмотр правил тарификации телефонных вызовов',
        'Право доступа позволяет просматривать примененные правила тарификации телефонных вызовов', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_UsageInvoiceSettingsEdit', 'Billing_UsageInvoiceSettingsView', 'Настройка правил тарификации телефонных вызовов',
        'Право позволяет изменять параметры тарификации телефонных вызовов', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- CRM

-- Клиенты
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_CustomerView', NULL, 'Просмотр клиентов',
        'Право позволяет просматривать информацию о клиентах в предоставляемых экранных формах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_CustomerEdit', 'CRM_CustomerView', 'Создание и редактирование клиента',
        'Право позволяет создавать и редактировать данные о клиенте', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Контактные лица
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_ContactPersonsView', NULL, 'Просмотр контактных лиц',
        'Позволяет просматривать имеющуюся информацию о контактных лицах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_ContactPersonsEdit', 'CRM_ContactPersonsView', 'Управление контактными лицами',
        'Позволяет управлять сведениями о контактных лицах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Заявки
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_OrderView', NULL, 'Просмотр заявок',
        'Право позволяет просматривать информацию о заявках в предоставляемых экранных формах',
        1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_OrderEdit', 'CRM_OrderView', 'Создание и редактирование заявки',
        'Право позволяет создавать и редактировать информацию о заявке', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_OrderStatusEdit', 'CRM_OrderView', 'Изменение статуса заявки',
        'Право позволяет изменять состояние жизненного цикла заявки', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Договоры
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_ContractView', NULL, 'Просмотр договоров',
        'Право позволяет просматривать информацию о договорах в предоставляемых экранных формах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_ContractEdit', 'CRM_ContractView', 'Создание и редактирование договора',
        'Право позволяет создавать и редактировать основные атрибуты договора, а также изменять состав продуктов', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_ContractStatusEdit', 'CRM_ContractView', 'Изменение статуса договора',
        'Право позволяет  изменять состояние жизненного цикла договора', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_ContractDelete', 'CRM_ContractView', 'Удаление договора',
        'Право позволяет удалять договоры', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Доп. соглашения
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_ContractExtensionView', NULL, 'Просмотр доп. соглашений',
        'Право позволяет просматривать информацию о доп. соглашениях в предоставляемых экранных формах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_ContractExtensionEdit', 'CRM_ContractExtensionView', 'Создание и редактирование доп. соглашения',
        'Право позволяет создавать и редактировать данные о доп. соглашениях, а также изменять состав продуктов (добавлять и исключать позиции)',
        1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_ContractExtensionStatusEdit', 'CRM_ContractExtensionView', 'Изменение статуса доп. соглашения',
        'Право позволяет изменять состояние жизненного цикла доп. соглашения', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_ContractExtensionDelete', 'CRM_ContractExtensionView', 'Удаление доп. соглашения',
        'Право позволяет удалять доп. соглашения', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Личный кабинет
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_PersonalAreaAccountView', NULL, 'Просмотр личного кабинета',
        'Право позволяет просматривать информацию о личном кабинете в предоставляемых экранных формах', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('CRM_PersonalAreaAccountCreateDelete', 'CRM_PersonalAreaAccountView', 'Создание и удаление учетной записи личного кабинета',
        'Право позволяет создавать и удалять учетные записи личного кабинета',
        1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;


-- PRODUCTMANAGMENT

-- Каталог продуктов
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CatalogEdit', NULL, 'Просмотр и редактирование каталога продуктов',
        'Право позволяет просматривать справочник "Каталог продуктов", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_ProductTypeEdit', 'ProductManagment_CatalogEdit', 'Редактирование состава продукта',
        'Позволяет редактировать состав продукта: перечень включенных товаров/услуг и значение характеристик, а также перечень простых спецификаций продуктов в составных спецификациях',
        1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Прайс-листы
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_PriceListView', NULL, 'Просмотр прайс-листов и предложений продуктов',
        'Право позволяет просматривать прайс-листы и предложения продуктов', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CommonPriceListEdit', 'ProductManagment_PriceListView', 'Создание публичных предложений продуктов',
        'Право позволяет создавать и редактировать публичные прайс-листы и предложения продуктов', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CommonPriceListStatusEdit', 'ProductManagment_CommonPriceListEdit', 'Изменение статуса публичного прайс-листа',
        'Право позволяет изменять статус публичного прайс-листа', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CommonPriceListDelete', 'ProductManagment_CommonPriceListEdit', 'Удаление публичного прайс-листа',
        'Право позволяет удалить публичный прайс-лист в статусе "Аннулирован"', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CustomPriceListEdit', 'ProductManagment_PriceListView', 'Создание индивидуальных предложений продуктов',
        'Право позволяет создавать и редактировать индивидуальные прайс-листы и предложения продуктов', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CustomPriceListStatusEdit', 'ProductManagment_CustomPriceListEdit', 'Изменение статуса индивидуального прайс-листа',
        'Право позволяет изменять статус индивидуального прайс-листа', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CustomPriceListDelete', 'ProductManagment_CustomPriceListEdit', 'Удаление индивидуального прайс-листа',
        'Право позволяет удалить индивидуальный прайс-лист в статусе "Аннулирован"', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Условия предоставления продукта
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_ProvidingTermsEdit', NULL, 'Просмотр и редактирование условий предоставления продукта',
        'Право позволяет просматривать справочник "Условия предоставления продукта", а также создавать, редактировать и удалять записи в нем', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Правила нумерации объектов системы
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_NumRulesView', NULL, 'Просмотр масок номеров и последовательностей',
        'Позволяет просматривать информацию о заданных масках и последовательностях', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_NumMaskEdit', 'System_NumRulesView', 'Создание и изменение масок номеров',
        'Позволяет редактировать маски номеров', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('System_NumSequenceEdit', 'System_NumRulesView', 'Создание и изменение масок номеров',
        'Позволяет создавать редактировать, удалять последовательности номеров', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Поставщики
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('PartnerManagement_SuppliersView', NULL, 'Просмотр информации о поставщиках',
        'Право доступа позволяет просматривать информацию о поставщиках', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('PartnerManagement_SuppliersEdit', 'PartnerManagement_SuppliersView', 'Создание и редактирование поставщиков',
        'Право позволяет создавать и редактировать информацию о поставщиках', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Тарифные планы
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_TariffView', NULL, 'Просмотр тарифных планов телефонии',
        'Право позволяет просматривать тарифные планы телефонии', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CommonTariffEdit', 'ProductManagment_TariffView', 'Создание публичных тарифных планов',
        'Право позволяет создавать и редактировать публичные тарифные планы телефонии', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CommonTariffStatusEdit', 'ProductManagment_CommonTariffEdit', 'Изменение статуса публичного тарифного плана',
        'Право позволяет изменять статус публичного тарифного плана телефонии', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CommonTariffDelete', 'ProductManagment_CommonTariffEdit', 'Удаление публичного тарифного плана',
        'Право позволяет удалить публичный тарифный план телефонии', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_ActiveCommonTariffEntryEdit', 'ProductManagment_CommonTariffEdit', 'Изменение классов трафика активного публичного тарифного плана',
        'Право позволяет добавлять/удалять/редактировать классы трафика публичного тарифного плана, находящегося в статусе "Действует"', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CustomTariffEdit', 'ProductManagment_TariffView', 'Создание индивидуальных тарифных планов',
        'Право позволяет создавать и редактировать индивидуальные тарифные планы телефонии', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CustomTariffStatusEdit', 'ProductManagment_CustomTariffEdit', 'Изменение статуса индивидуального тарифного плана',
        'Право позволяет изменять статус индивидуального тарифного плана телефонии', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_CustomTariffDelete', 'ProductManagment_CustomTariffEdit', 'Удаление индивидуального тарифного плана',
        'Право позволяет удалить индивидуальный тарифный план телефонии', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('ProductManagment_ActiveCustomTariffEntryEdit', 'ProductManagment_CustomTariffEdit', 'Изменение классов трафика активного индивидуального тарифного плана',
        'Право позволяет добавлять/удалять/редактировать классы трафика индивидуального тарифного плана, находящегося в статусе "Действует"', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Задания повторной тарификации
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_ChargeJobView', NULL, 'Просмотр заданий на тарификацию вызовов',
        'Право доступа позволяет просматривать представление список и карточка задания на тарификацию', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_ChargeJobCreate', 'Billing_ChargeJobView', 'Создание заданий на повторную тарификацию вызовов',
        'Право позволяет создавать задания на повторную тарификацию вызовов', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Необработанные вызовы
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Mediation_UnsuitableCallsView', NULL, 'Просмотр непротарифицированных вызовов',
        'Право доступа позволяет просматривать представления инструментов, содержищие непротарифицированные в предбиллинге телефонные вызовы', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Mediation_UnsuitableCallsEdit', 'Mediation_UnsuitableCallsView', 'Обработка непротарифицированных вызовов',
        'Право позволяет создавать задания на повторную обработку вызовов, а также редактировать информацию о вызове на этапах "Конвертация" и "Анализ направления"', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Формирование начислений
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_RatedCallsWithoutInvoiceView', NULL, 'Просмотр представления "Формирование начислений"',
        'Право доступа позволяет просматривать сведения о протарифицированных телефонных вызовах не отнесенных к инвойсу', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Billing_RatedCallsWithoutInvoiceRepeated', NULL, 'Запуск повторной обработки вызовов без инвойса',
        'Право позволяет создавать задания на повторную обработку отсева протарифицированных вызовов в АСР', 1)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;