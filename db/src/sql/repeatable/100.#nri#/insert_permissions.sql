-- NRI


-- Просмотр ресурсов тех. учета
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Nri_ResourceSearchView', NULL, 'Просмотр ресурсов тех. учета',
        'Право позволяет просматривать страницу ресурсов тех. учета', 777)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Просмотр справочника спецификаций услуг
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Nri_ServiceSpecificationView', NULL, 'Просмотр справочника спецификаций услуг',
        'Право позволяет просматривать страницы: Справочник спецификаций услуг, Спецификация услуг', 777)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;
	
-- Редактирование спецификаций услуг
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Nri_ServiceSpecificationEdit', 'Nri_ServiceSpecificationView', 'Редактирование спецификаций услуг',
        'Право позволяет редактировать спецификации услуг', 777)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Просмотр справочника спецификаций логических ресурсов
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Nri_LogicalResourceSpecView', NULL, 'Просмотр справочника логических ресурсов',
        'Право дает возможность просматривать справочник спецификаций логических ресурсов', 777)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;
	
-- Редактирование спецификаций логических ресурсов
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Nri_LogicalResourceSpecEdit', 'Nri_LogicalResourceSpecView', 'Редактирование спецификаций логических ресурсов',
        'Право позволяет редактировать спецификации логических ресурсов', 777)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;
	
-- Просмотр справочника ЖЦ физических ресурсов
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Nri_ResourceLifecycleView', NULL, 'Просмотр справочника ЖЦ физических ресурсов',
        'Право дает возможность просматривать справочник ЖЦ физических ресурсов', 777)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;
	
-- Редактирование ЖЦ физических ресурсов
INSERT INTO system.permission (id, parent_id, name, description, entity_package_id)
VALUES ('Nri_ResourceLifecycleEdit', 'Nri_ResourceLifecycleView', 'Редактирование ЖЦ физических ресурсов',
        'Право позволяет редактировать ЖЦ физических ресурсов', 777)
ON CONFLICT (id)
  DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
    description           = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

-- Добавить пермишен
PREPARE addPermission(varchar(100), varchar(100), varchar(100), varchar(250), bigint) AS
  INSERT INTO system.permission(id, parent_id, name, description, entity_package_id)
  VALUES ($1, $2, $3, $4, $5)
  ON CONFLICT(id)
    DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
      description             = EXCLUDED.description, entity_package_id = EXCLUDED.entity_package_id;

EXECUTE addPermission('Nri_PhoneNumberPoolView', NULL, 'Просмотр справочника пулов номеров телефонов',
  'Право дает возможность просматривать справочник пулов номеров телефонов', 777);
EXECUTE addPermission('Nri_PhoneNumberPoolEdit', 'Nri_PhoneNumberPoolView', 'Редактирование справочника пулов номеров телефонов',
  'Право позволяет редактировать сравочник пулов номеров телефонов' , 777);
EXECUTE addPermission('Nri_PhoneNumberEdit', 'Nri_PhoneNumberPoolView', 'Редактирование номера телефона',
  'Право позволяет редактировать номер телефона' , 777);
EXECUTE addPermission('Nri_PhoneNumberListEdit', 'Nri_PhoneNumberPoolView', 'Редактирование списка телефонных номеров',
  'Право дает возможность редактировать список телефонных номеров', 777);
  
EXECUTE addPermission('Nri_ResourceInfoView', NULL, 'Просмотр информации о ресурсах и поиск ресурсов',
  'Право позволяет просматривать страницы: Ресурсы, Структура ресурса, Информация об установке ресурса, а также производить поиск на странице Ресурсы.', 777);
EXECUTE addPermission('Nri_ResourceInfoEdit', 'Nri_ResourceInfoView', 'Редактирование ресурса',
  'Право позволяет создавать/удалять ресурс на страницах: Ресуры, Структура ресурса, а также редактировать данные на странице Структура ресурса', 777);  

EXECUTE addPermission('Nri_ResourceInstallationEdit', 'Nri_ResourceInfoView', 'Редактирование точки установки и зоны охвата ресурса',
  'Право позволяет создавать/удалять точку установки на странице Структура ресурса, перемещать точку установки и редактировать зону охвата на странице Информация об установке ресурса', 777);  
EXECUTE addPermission('Nri_IpAddressesView', NULL, 'Просмотр IP адресов и подсетей',
'Право позволяет просматривать страницы:IP подсети, IP адрес, Список IP адресов/подсетей; а также производить поиск на странице Список IP адресов/подсетей.', 777);
EXECUTE addPermission('Nri_IpAddressEdit', 'Nri_IpAddressesView', 'Редактирование IP-адреса',
'Право позволяет редактировать данные на странице IP адрес', 777);
EXECUTE addPermission('Nri_IpSubnetEdit', 'Nri_IpAddressesView', 'Редактирование IP подсети',
'Право позволяет создавать/удалять/редактировать данные на странице IP подсети', 777);
  -- Структура строений
EXECUTE addPermission('Nri_BuildingStructureView', Null, 'Просмотр структуры строения',
  'Право позволяет просматривать страницу Структура строения', 777);  

EXECUTE addPermission('Nri_BuildingStructureEdit', 'Nri_BuildingStructureView', 'Редактирование структуры строения',
  'Право позволяет создавать/удалять/редактировать данные на странице Структура строения', 777);    

-- Типы элементов строений
EXECUTE addPermission('Nri_BuildingAreaTypeView', NULL, 'Просмотр справочника типов элементов строений',
  'Право позволяет просматривать страницу Справочник типов элементов строений', 777);    

EXECUTE addPermission('Nri_BuildingAreaTypeEdit', 'Nri_BuildingAreaTypeView', 'Редактирование справочника типов элементов строений',
  'Право позволяет создавать/удалять/редактировать типы элементов строений на странице Справочник типов элементов строений', 777);    

EXECUTE addPermission('Nri_NetworkMapView', NULL, 'Просмотр карты сети',
  'Право позволяет просматривать карту сети', 777);    
  
