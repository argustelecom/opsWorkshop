package ru.argustelecom.box.nri.building;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;
import ru.argustelecom.box.env.address.map.LocationGeoRepository;
import ru.argustelecom.box.env.address.map.model.LocationGeo;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.map.geocoding.model.ObjectGeo;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.map.component.LMapWidget;
import ru.argustelecom.system.inf.map.component.event.FeatureEvent;
import ru.argustelecom.system.inf.map.component.model.MapModel;
import ru.argustelecom.system.inf.map.component.model.MapViewport;
import ru.argustelecom.system.inf.map.component.model.MapViewportFactory;
import ru.argustelecom.system.inf.map.component.model.overlayer.OverLayer;
import ru.argustelecom.system.inf.map.component.model.overlayer.feature.FeatureLayer;
import ru.argustelecom.system.inf.map.crs.CrsUtils;
import ru.argustelecom.system.inf.map.geojson.Feature;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Фрейм информации о расположении строения
 *
 * @author s.kolyada
 * @since 07.03.2018
 */
@Named(value = "buildingResLocationFM")
@PresentationModel
@Getter
public class BuildingResourceLocationFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String BUILDING_LAYER_ID = "building_layer";

	private static final Logger log = Logger.getLogger(BuildingResourceLocationFrameModel.class);

	/**
	 * Адрес строения
	 */
	@Getter
	private Building buildingLocation;

	/**
	 * Вьюпорт карты
	 */
	@Getter
	@Setter
	private MapViewport viewport;

	/**
	 * Карта
	 */
	@Getter
	@Inject
	private MapModel map;

	@Inject
	private LocationGeoRepository locationGeoRepository;

	/**
	 * Редактируется ли информация о расположении
	 */
	@Getter
	private boolean geoEditing;

	/**
	 * Расположение изменено
	 */
	private boolean buildingGeoUpdated;

	/**
	 * Информация о расположении строения
	 */
	private LocationGeo buildingGeo;

	/**
	 * Слой адресов
	 */
	private FeatureLayer addressLayer;

	/**
	 * Маркер адреса
	 */
	private Feature addressMarker;

	/**
	 * Фабрика вьюпортов
	 */
	@Inject
	private MapViewportFactory viewportFactory;

	/**
	 * Обработка перед выводом страницы
	 *
	 * @param location строение
	 */
	public void preRender(Building location) {
		buildingLocation = location;

		// TODO понять, надо ли перезагружать всю карту
		// тк элемент отоюражается на паспорте строения и только для 1 элемента , при этом адрес не изменяется
		// т.е. кажется, что можно загрузить 1 раз при первом рендеринге, а далее просто отоюражать текущее состояние
		configureMapModel();
		refresh();
	}

	/**
	 * Обновить данные
	 */
	private void refresh() {
		// TODO получать данные из БД
		buildingGeo = locationGeoRepository.findByLocation(buildingLocation, map.getActiveBaseLayer().getMapId());

		addressLayer.clear();
		if (buildingGeo != null) {
			addressMarker = createAddressMarker(buildingGeo, addressLayer);
			addressMarker.getProperties().put("title", buildingLocation.getObjectName());
		}
		configureViewport();

	}

	/**
	 * Создать маркер адреса
	 * @param geo расположение
	 * @param layer слой
	 * @return фича с маркером
	 */
	private Feature createAddressMarker(ObjectGeo geo, FeatureLayer layer) {
		Geometry<G2D> center = new Point<>(geo.getPosition(), CrsUtils.WGS84_CRS);
		Feature result = new Feature(buildingLocation.getId().toString(), center);

		if (layer != null) {
			layer.addFeature(result);
		}

		return result;
	}

	/**
	 * Настройка вьюпорта
	 */
	private void configureViewport(){
		viewport = null;
		if (buildingLocation != null){
			viewport = viewportFactory.createViewportToShow(buildingLocation, map.getActiveBaseLayer());
		}
	}

	/**
	 * Настройка модели карты
	 */
	private void configureMapModel() {
		map.setEditable(true);

		if (map.getOverLayer(BUILDING_LAYER_ID) == null) {
			addressLayer = new FeatureLayer(BUILDING_LAYER_ID, "Адрес здания");
			addressLayer.setLayerOptionsName("buildingLayerOptions");
			map.addOverLayer(addressLayer);
		}
	}

	/**
	 * Получить виджет карты
	 * @return виджет карты
	 */
	public LMapWidget getMapWidget(){
		return new LMapWidget("addressMap");
	}

	/**
	 * Действия при создании фичи
	 * @param event событие
	 */
	public void onFeatureCreated(FeatureEvent event) {
		geoEditing = false;
		createAddressGeo(event.getFeature());
		RequestContext.getCurrentInstance().update("address_map_toolbar");
	}

	/**
	 * Действия при редактировании фичи
	 * @param event собучние
	 */
	public void onFeatureEdited(FeatureEvent event) {
		buildingGeoUpdated = true;
	}

	/**
	 * Создание локации строения
	 * @param marker фича
	 */
	private void createAddressGeo(Feature marker) {
		if (ObjectUtils.anyNotNull(buildingGeo, addressMarker)) {
			log.error("Попытка создать новую гео-отметку для строения, у которого она уже присутствует");
			throw new BusinessException("Попытка создать новую гео-отметку для строения, у которого она уже присутствует");
		}

		addressMarker = marker;

		Point<G2D> markerPoint = (Point) marker.getGeometry();

		buildingGeo = locationGeoRepository.createGeoLocation(buildingLocation, markerPoint.getPosition(),
				map.getActiveBaseLayer().getMapId());
	}

	/**
	 * Присутствует ли маркер строения на карте
	 * @return истина если присутствует, иначе ложь
	 */
	public boolean isBuildingOnMap() {
		return buildingGeo != null;
	}

	/**
	 * Начать позиционирование маркера строения
	 */
	public void startAddressPositioning() {
		geoEditing = true;
		if (!isBuildingOnMap()) {
			getMapWidget().startMarker( addressLayer.getId(), null);
		} else {
			getMapWidget().editFeaturesOn( addressLayer);
		}
	}

	/**
	 * Завершить позиционирование маркера строения
	 */
	public void stopAddressPositioning() {
		geoEditing = false;
		if (!isBuildingOnMap()) {
			getMapWidget().stopMarker();
		} else {
			getMapWidget().editFeaturesOff(addressLayer);
			commitAddressGeo();
		}
	}

	/**
	 * Сохранит изменения позиции строения
	 */
	private void commitAddressGeo() {
		if (buildingGeoUpdated) {
			buildingGeo = locationGeoRepository.updatePosition(buildingGeo, ((Point<G2D>) addressMarker.getGeometry()).getPosition());
		}
		buildingGeoUpdated = false;
	}

	/**
	 * Отоюражается ли слой с домами на карте
	 * @return истина елси отоюражается, иначе ложь
	 */
	public boolean isAddressLayerVisible() {
		OverLayer builingLayer = map.getOverLayer(BUILDING_LAYER_ID);
		if (builingLayer != null) {
			return builingLayer.isVisible();
		}
		return false;
	}
}