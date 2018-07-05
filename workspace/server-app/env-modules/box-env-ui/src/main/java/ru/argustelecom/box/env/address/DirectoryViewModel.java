package ru.argustelecom.box.env.address;

import static ru.argustelecom.system.inf.modelbase.NamedObject.BY_OBJECT_NAME;

import java.util.Collections;
import java.util.List;

import ru.argustelecom.system.inf.modelbase.Directory;

/**
 * Интерфейс, который должны наследовать все простые справочники. Он вводит общую сигнатуру методов, внося таким образом
 * большую строгость в код.
 * 
 * @param <T>
 *            класс, для которого делается справочник.
 */
public interface DirectoryViewModel<T extends Directory> {

	/**
	 * @return Список всех записей справочника.
	 */
	public List<T> getTypes();

	/**
	 * Создание новой записи в справочнике.
	 */
	public void create();

	/**
	 * Удаление записи из справочника.
	 * 
	 * @param directory
	 *            удаляемая запись.
	 */
	public void remove(T directory);

	public List<T> getSelectedTypes();

	public default void removeSelectedTypes() {
		getSelectedTypes().forEach(this::remove);
	}

	/**
	 * Очистка всех параметров, которые используются для создания новых записей.
	 */
	public void cleanCreationParams();

	/**
	 * Сортировка записей справочника.
	 */
	public default void sortData() {
		Collections.sort(getTypes(), BY_OBJECT_NAME);
	}

}