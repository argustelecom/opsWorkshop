package ru.argustelecom.box.env.map.page.aspects;

import java.io.Serializable;
import java.util.Set;

import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.map.page.CurrentMapObject;
import ru.argustelecom.system.inf.map.component.model.MapModel;
import ru.argustelecom.system.inf.map.geojson.Feature;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;

/**
 * Аспект страницы карты.
 * Страница состоит из блоков: параметры (фильтры), карта, детали выбранного объекта. Страница может иметь несколько
 * аспектов, между которыми может переключаться пользователь. Аспект определяет
 * содержимое и поведение блоков. Аспект соответствует определённой тематике, такой как "охват сети", "инфраструктура сети".
 * <p>
 * Аспект не знает страницу карты. Может реюзаться на других страницах.
 * 
 * Немного необычный фрейм тем, что не имеет соответствующего Frame.xhtml - эта часть реализуется рендерингом модели
 * слоя карты, которую создает данный FrameModel.
 * <p>
 * Наследник достаточно оверрайдить только абстрактные методы.
 * 
 */
@PresentationModel
public abstract class MapAspect implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	CurrentMapObject currentMapObject;

	/**
	 * Добавляет в модель карты свои данные.
	 * Вызывается перед рендерингом карты. Рендеринг карты подразумевает полное обновление данных карты.
	 * 
	 * @param mapModel
	 */
	public abstract void populateMapModel(MapModel mapModel);

	/**
	 * Опциональная реакция на смену текущего объекта карты. Заботится об адекватном отображении текущего объекта в
	 * данном аспекте.
	 * <p>
	 * Обсервит ивент кто-то снаружи.
	 */
	public void mapObjectChanged() {
	}

	/**
	 * Опциональная реакция на редактирование объекта карты.
	 * Вызывается из метода onFeatureEdited модели карты {@link ru.argustelecom.box.env.map.page.MapViewModel}
	 */
	public void onFeatureEdited(String layerId, Feature feature) {

	}

	/**
	 * Опциональные действия для присваивания состояния блоком детальных сведений.
	 * Создавалось чтобы задавать состояние блокам и по применению настроек и по открытию с уже примененными настройками.
	 * Это состояние следует не из выбранного объекта (для этого лучше mapObjectChanged), а из Settings.
	 */
	public void preRenderDetailInfo() {
	}

	/**
	 * Класс бина настроек, чтобы понимать как их десериализовать и пр.
	 */
	public abstract Class<?> getSettingsBeanClass();

	protected boolean isSupportedObject() {
		if (currentMapObject.getValue() == null) {
			return false;
		}

		// проверяем, что текущий аспект поддерживает отоюражение секущего выбранного обхекта
		// для этого получаем список отоюражаемых аспектом классов и проверяем, есть ли среди них
		// класс текущего выбранного объекта
		// т.к. аспект может отображать родительские классы, то проверяем не является ли какой-либо из
		// отоюражаемых аспектом классов родительсик к классу выбранного объекта
		Set<Class> renderedClasses = rendersEntities();
		return renderedClasses.stream()
				.filter(cls -> cls.isAssignableFrom(currentMapObject.getValue().getClass()))
				.findAny()
				.isPresent();
	}

	/**
	 * Список сущностей, которые отображаются данным аспектом
	 * @return список отоюражаемых аспкетом сущностей
	 */
	public abstract Set<Class> rendersEntities();

	public void checkCurrentMapObject() {
		if (!isSupportedObject()) {
			currentMapObject.setValue(null);
		}
	}
}
