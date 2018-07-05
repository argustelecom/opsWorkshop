package ru.argustelecom.box.nri.map.network.coverage;

import org.geolatte.geom.G2D;
import org.jboss.logging.Logger;
import ru.argustelecom.box.env.address.map.model.LocationGeo;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.map.geocoding.ObjectGeoRepository;
import ru.argustelecom.box.env.map.geocoding.model.ObjectGeo;
import ru.argustelecom.box.env.map.page.CurrentMapObject;
import ru.argustelecom.box.env.map.page.CurrentMapViewport;
import ru.argustelecom.box.env.map.page.aspects.MapAspect;
import ru.argustelecom.box.env.map.page.aspects.MapWidget;
import ru.argustelecom.box.nri.coverage.ResourceInstallationRepository;
import ru.argustelecom.system.inf.map.component.model.MapModel;
import ru.argustelecom.system.inf.map.component.model.overlayer.dotdistribution.DotDistributionLayer;
import ru.argustelecom.system.inf.map.component.model.overlayer.dotdistribution.dots.Dot;
import ru.argustelecom.system.inf.map.component.model.overlayer.feature.FeatureLayer;
import ru.argustelecom.system.inf.map.geojson.Feature;
import ru.argustelecom.system.inf.map.geojson.FeatureUtils;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.resource.ColorUtils;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Аспект для отображения на карте точек монтирования
 *
 * @author s.kolyada
 * @since 14.03.2018
 */
@PresentationModel
public class ResourceInstallationsAspect extends MapAspect {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(ResourceInstallationsAspect.class);

	private static final String BUILDING_MARKER_LAYER_ID = "building_marker_layer";

	/**
	 * Радиус точки поумолчанию
	 */
	public static final double DEFAULT_DOT_RADIUS = 20d;

	/**
	 * Прозрачность точки поумолчанию
	 */
	public static final double DEFAULT_DOT_OPACITY = 0.50;

	private static final Set<Class> RENDERED_ENTITIES;

	static {
		RENDERED_ENTITIES = new HashSet<>(2);
		RENDERED_ENTITIES.add(Location.class);
		RENDERED_ENTITIES.add(Building.class);
	}

	/**
	 * Текущий обхект карты
	 */
	@Inject
	private CurrentMapObject currentMapObject;

	/**
	 * Репозиторий доступа к общим координатам объектов
	 */
	@Inject
	private ObjectGeoRepository objectGeoRepository;

	/**
	 * Текущая область видимости карты
	 */
	@Inject
	private CurrentMapViewport currentMapViewport;

	/**
	 * Карта
	 */
	@Inject
	private MapWidget mapWidget;

	/**
	 * Репозиторий доступа к установкам
	 */
	@Inject
	private ResourceInstallationRepository installationRepository;

	/**
	 * Заполнение модели данными
	 * @param mapModel модель карты
	 */
	@Override
	public void populateMapModel(MapModel mapModel) {
		FeatureLayer buildingMarkerLayer = new FeatureLayer(BUILDING_MARKER_LAYER_ID, "Адрес здания");
		buildingMarkerLayer.setLayerOptionsName("buildingLayerOptions");
		mapModel.addOverLayer(buildingMarkerLayer);

		showMarkerForCurrentMapObjectIfAppropriate();

		// сначала почистим слои, если они были добавлены предыдущим обращением:
		mapModel.removeOverLayer(CoverageLayerType.PRESENCE.getLayerId(null));

		Long mapId = mapModel.getActiveBaseLayer().getMapId();

		Collection<LocationGeo> coverage = installationRepository.findLocationsOfInstallations(mapId);
		CoverageLayerType.PRESENCE.createLayer(mapModel, coverage);
	}

	/**
	 * Хреновина, которая описывает разновидность слоя на карте
	 *
	 */
	public enum CoverageLayerType {
		/**
		 * Просто охват, то есть факт присутствия. Может рисоваться для конкретной технологии, или для всех (tech ==
		 * null)
		 */
		PRESENCE("coverage_%s_layer", "Охват сети %s") {
			@Override
			public void createLayer(MapModel mapModel, Collection<LocationGeo> data) {
				Set<Dot> dots = data.stream().map(coord -> Dot.of(coord.getPosition())).collect(Collectors.toSet());
				CoverageLayerType.createLayer(mapModel, getLayerId(null), getLayerName(null),
						222, dots);
			}
		};

		private String layerIdFormat;
		private String layerNameFormat;

		CoverageLayerType(String layerIdFormat, String layerNameFormat) {
			this.layerIdFormat = layerIdFormat;
			this.layerNameFormat = layerNameFormat;
		}

		private static DotDistributionLayer createLayer(MapModel mapModel, String id, String name, Integer color,
														Set<Dot> data) {
			DotDistributionLayer layer = new DotDistributionLayer(id, name);
			layer.setColor(ColorUtils.colorToHex(color));
			layer.setRadius(DEFAULT_DOT_RADIUS);
			layer.setOpacity(DEFAULT_DOT_OPACITY);
			layer.setDots(data);
			log.debugv("Создан слой \"{0}\", количество точек: {1}", layer.getName(), data.size());
			mapModel.addOverLayer(layer);
			return layer;
		}

		public String getLayerId(String tech) {
			return String.format(layerIdFormat, tech == null ? "anytech" : tech.replace(' ', '_'));
		}

		public String getLayerName(String tech) {
			return String.format(layerNameFormat, tech == null ? "(все технологии)" : tech);
		}

		/**
		 * Самый полезный метод. Создает и настраивает слой в карте
		 *
		 * @param mapModel
		 *            - карта
		 * @param data
		 *            - данные по точкам охвата, загруженные из БД
		 */
		public abstract void createLayer(MapModel mapModel, Collection<LocationGeo> data);
	}

	@Override
	public void mapObjectChanged() {
		// кривость: "рендеринг" делается слишком рано
		mapWidget.get().clearFeatureLayer(BUILDING_MARKER_LAYER_ID);
		showMarkerForCurrentMapObjectIfAppropriate();
	}

	private void showMarkerForCurrentMapObjectIfAppropriate() {
		if (currentMapObject.getValue() instanceof Building) {
			ObjectGeo geo = objectGeoRepository.findByObject(currentMapObject.getValue(), currentMapViewport.getValue()
					.getBaseLayer().getMapId());
			if (geo == null) {
				log.debugv("Нет ObjectGeo для здания {0}", currentMapObject.getValue());
				return;
			}
			G2D position = geo.getPosition();
			log.debugv("Отображаю маркер для здания по координатам {0}", position);
			Feature buildingFeature = new Feature(currentMapObject.getValue(), FeatureUtils.createPoint(position));
			mapWidget.get().createFeature(BUILDING_MARKER_LAYER_ID, buildingFeature);
			mapWidget.get().showOverLayer(BUILDING_MARKER_LAYER_ID);
		} else {
			log.debugv("Выбрано не здание - ничего не отображаю");
		}
	}

	@Override
	public void preRenderDetailInfo() {
		super.preRenderDetailInfo();
	}


	@Override
	public Class<?> getSettingsBeanClass() {
		return null;
	}

	/**
	 * Список классов, которые отображаются данным аспектом
	 * @return список отоюражаемых сущностей
	 */
	@Override
	public Set<Class> rendersEntities() {
		return RENDERED_ENTITIES;
	}
}