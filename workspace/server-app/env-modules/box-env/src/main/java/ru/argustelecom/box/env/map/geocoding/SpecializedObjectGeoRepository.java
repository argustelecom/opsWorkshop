package ru.argustelecom.box.env.map.geocoding;

import org.geolatte.geom.G2D;
import ru.argustelecom.box.env.map.geocoding.model.ObjectGeo;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.modelbase.SuperClass;

import java.util.Set;

/**
 * Интерфейс специализированного репозитория для вычисления ObjectGeo.
 * Если у объекта нет своей позиции на карте - возвращает вычисленный
 * ObjectGeo с позицией здания/контейнера (#link {@link ObjectGeo.isNonStrict()})
 *
 * @author e.zagainov
 */
public interface SpecializedObjectGeoRepository {

	/**
	 * Возвращает фигуру указанного объекта на указанной карте
	 *
	 * @param object
	 *            объект, фигуру которого вернуть
	 * @param mapId
	 *            карта мира (MapArea) для отображения на которой вернуть фигуру. Указывай BaseLayer.mapId. Если не
	 *            находишься в контексте определённой карты, используй {@link MapRepository#getDefaultMap()}
	 */
	ObjectGeo findByObject(SuperClass object, Long mapId);

	/**
	 * Поддержка репозиторием исходного объекта.
	 *
	 * @param object
	 *            исходный объект, поддержку которого необходимо определить
	 */
	boolean objectSupported(SuperClass object);

	/**
	 * Найти ближайший объект к точке с координатой
	 * @param mapId идентификатор карты
	 * @param position координаты относительно окторой искать объект
	 * @return
	 */
	BusinessObject findNearest(Long mapId, G2D position);

	/**
	 * Поддерживает ли репозиторий получение хотя бы одной из перечисленных сущностей
	 * @param classes список классов сущностей
	 * @return истина, если репозиторий возвращает хотя бы 1 из перечисленных сущностей
	 */
	boolean supportsAnyClass(Set<Class> classes);
}