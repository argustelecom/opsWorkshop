package ru.argustelecom.box.env.address.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface LocationMessagesBundle {

	// *****************************************************************************************************************
	// CRUD Location
	// *****************************************************************************************************************

	@Message(value = "Адресный объект создан")
	String locationCreated();

	@Message(value = "Страна")
	String country();

	@Message(value = "Создание страны")
	String countryCreation();

	@Message(value = "Страна '%s' успешно создана")
	String countrySuccessfullyCreated(String countryName);

	@Message(value = "Регион")
	String region();

	@Message(value = "Создание региона")
	String regionCreation();

	@Message(value = "Регион '%s' успешно создан")
	String regionSuccessfullyCreated(String regionName);

	@Message(value = "Район")
	String district();

	@Message(value = "Улица")
	String street();

	@Message(value = "Создание улицы")
	String streetCreation();

	@Message(value = "Улица '%s' успешно создана")
	String streetSuccessfullyCreated(String streetName);

	@Message(value = "Создание здания")
	String buildingCreation();

	@Message(value = "Редактирование здания")
	String buildingEditing();

	@Message(value = "Помещение")
	String lodging();

	@Message(value = "Невозможно удалить адресный объект '%s'")
	String cannotRemoveLocation(String locationName);

	@Message(value = "Есть зависимые объекты")
	String locationHaveChildObjects();

	@Message(value = "Адресный объект удалён")
	String locationRemoved();

	@Message(value = "Адресный объект '%s' успешно удалён")
	String locationSuccessfullyRemoved(String locationName);

	@Message(value = "Невозможно создать страну")
	String cannotCreateCountry();

	@Message(value = "Страна с названием: '%s' уже есть")
	String countryAlreadyExist(String country);

	@Message(value = "Невозможно создать регион")
	String cannotCreateRegion();

	@Message(value = "Регион: '%s. %s' уже есть")
	String regionAlreadyExist(String typeShortName, String name);

	@Message(value = "Невозможно создать улицу")
	String cannotCreateStreet();

	@Message(value = "Улица: '%s. %s' в регионе '%s' уже есть")
	String streetAlreadyExist(String typeShortName, String name, String parentFullName);

	@Message(value = "Невозможно изменить адресный объект")
	String cannotEditLocation();

	@Message(value = "Здание '%s' уже есть")
	String buildingAlreadyExist(String buildingName);

	// *****************************************************************************************************************
	// CRUD Location Type
	// *****************************************************************************************************************

	@Message(value = "Создание типа адресного объекта")
	String locationTypeCreation();

	@Message(value = "Редактирование типа адресного объекта")
	String locationTypeEditing();

	@Message(value = "Тип адресного объекта создан")
	String locationTypeCreated();

	@Message(value = "Тип адресного объекта '%s' успешно создан")
	String locationTypeSuccessfullyCreated(String locationTypeName);

	@Message(value = "Тип адресного объекта удалён")
	String locationTypeRemoved();

	@Message(value = "Тип адресного объекта '%s' успешно удалён")
	String locationTypeSuccessfullyRemoved(String locationTypeName);

	// *****************************************************************************************************************
	// CRUD Location Type Level
	// *****************************************************************************************************************

	@Message(value = "Создание уровня адресного объекта")
	String locationLevelCreation();

	@Message(value = "Редактирование уровня адресного объекта")
	String locationLevelEditing();

	@Message(value = "Уровень адресного объекта создан")
	String locationLevelCreated();

	@Message(value = "Уровень адресного объекта '%s' успешно создан")
	String locationLevelSuccessfullyCreated(String locationTypeName);

	@Message(value = "Уровень адресного объекта удалён")
	String locationLevelRemoved();

	@Message(value = "Уровень адресного объекта '%s' успешно удалён")
	String locationLevelSuccessfullyRemoved(String locationTypeName);

	@Message(value = "стр")
	String wingShortName();

	@Message(value = "к")
	String corpusShortName();
}