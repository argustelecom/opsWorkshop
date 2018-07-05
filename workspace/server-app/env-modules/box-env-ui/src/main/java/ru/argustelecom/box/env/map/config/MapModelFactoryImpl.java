package ru.argustelecom.box.env.map.config;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.G2D;

import ru.argustelecom.box.env.address.model.Region;
import ru.argustelecom.box.env.map.geocoding.MapRepository;
import ru.argustelecom.box.env.map.geocoding.ObjectGeoRepository;
import ru.argustelecom.box.env.map.geocoding.model.MapArea;
import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.login.ArgusPrincipal;
import ru.argustelecom.system.inf.map.component.model.MapModel;
import ru.argustelecom.system.inf.map.component.model.MapModelFactory;
import ru.argustelecom.system.inf.map.component.model.baselayer.BaseLayer;
import ru.argustelecom.system.inf.map.component.model.baselayer.GoogleLayer;
import ru.argustelecom.system.inf.map.component.model.baselayer.GoogleLayer.LayerType;
import ru.argustelecom.system.inf.map.component.model.baselayer.OsmLayer;
import ru.argustelecom.system.inf.map.component.model.baselayer.StamenLayer;
import ru.argustelecom.system.inf.map.component.model.baselayer.StamenLayer.LayerStyle;

/**
 * Возвращает множество базовых слоёв исходя из имеющихся карт мира ({@link MapArea}).
 */
@SessionScoped
public class MapModelFactoryImpl implements MapModelFactory, Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private MapRepository mapRepository;
	@Inject
	private ObjectGeoRepository geoRep;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ArgusPrincipal currentUser;

	/**
	 * Разрешенные текущему пользователю для просмотра подложки и границы. Значение null значит подложка разрешена без
	 * границ.
	 */
	private Map<MapArea, Envelope<G2D>> permittedMapArea = new LinkedHashMap<>(); // Порядок важен

	@PostConstruct
	public void postConstruct() {
		List<MapArea> maps = mapRepository.getMaps();
		Region homeRegion = em.find(Region.class, currentUser.getHomeRegion().getId());

		for (MapArea map : maps) {
			permittedMapArea.put(map, null);
//			if (map.getRegion() == null
//					|| (map.getRegion().containsRegion(homeRegion) || homeRegion.containsRegion(map.getRegion()))) {

//				Envelope<G2D> bounds = null;
//				ObjectGeo homeRegionGeo = geoRep.findByObject(homeRegion, map.getId());
//				if (homeRegionGeo != null && homeRegionGeo.getMbr() != null) {
//					bounds = homeRegionGeo.getMbr();
//				}
//				permittedMapArea.put(map, bounds);
//			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Produces
	@Dependent
	public MapModel createMapModel() {
		MapModel mapModel = new MapModel();

		addBaseLayers(mapModel);
		if (mapModel.getBaseLayersContainer().isEmpty()) {
			throw new BusinessException("Невозможно отобразить карту, т.к. в конфигурации карт не указано ни одной "
					+ "подложки, доступной тонкому клиенту.");
		}
		mapModel.setActiveBaseLayer(mapModel.getBaseLayersContainer().iterator().next());

		return mapModel;
	}

	/**
	 * Добавляет в mapModel базовые слои исходя из конфигурации подложек.
	 */
	@SuppressWarnings("unchecked")
	private void addBaseLayers(MapModel mapModel) {

		for (MapArea map : permittedMapArea.keySet()) {
			Class<BaseLayer> baseLayerClass;
			try {
				baseLayerClass = (Class<BaseLayer>) Class.forName(map.getBaseLayerClassName());
				// Некоторые карты, например гугл, можем показывать в нескольких видах.
				if (baseLayerClass.equals(GoogleLayer.class)) {
					addBaseLayer(mapModel, new GoogleLayer(map.getId(), LayerType.TERRAIN), map);
					addBaseLayer(mapModel, new GoogleLayer(map.getId(), LayerType.HYBRID), map);
				} else {
					addBaseLayer(mapModel, baseLayerClass.getConstructor(Long.class).newInstance(map.getId()), map);
				}
				if (baseLayerClass.equals(OsmLayer.class)) {
					// для слоя OSM доп. вариант в тонах серого (TASK-77453)
					addBaseLayer(mapModel, new OsmLayer(map.getId(), true), map);
				}
			} catch (ClassNotFoundException e) {
				throw new SystemException("Не могу загрузить класс, указанный в MapArea.baseLayerClassName: "
						+ map.getBaseLayerClassName(), e);
			} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
				throw new SystemException("Не могу инстанциировать класс, указанный в MapArea.baseLayerClassName: "
						+ map.getBaseLayerClassName(), e);
			}
			// Надо придумывать возможность указывать несколько baseLayer для MapArea.
			// В её отсутсвии хардкодно добавляю необходимый второй слой
			addBaseLayer(mapModel, new StamenLayer(map.getId(), LayerStyle.TONER), map);
			addBaseLayer(mapModel, new StamenLayer(map.getId(), LayerStyle.TONER_LITE), map);
		}
		// На БД разработки временно хочу иметь несколько базовых слоёв чтобы
		// видеть проблемы "подходящести" координат к разным слоям
//		if (ServerRuntimeProperties.instance().getAppDebugMode() && !permittedMapArea.isEmpty()) {
//			MapArea defMap = permittedMapArea.keySet().iterator().next();
//			// 2Гис отломался при переходе на leaflet 1.0 (TASK-79023)
//			// addBaseLayer(mapModel, new DGisLayer(defMap.getId()), defMap);
//			addBaseLayer(mapModel, new GoogleLayer(defMap.getId(), LayerType.TERRAIN), defMap);
//			addBaseLayer(mapModel, new GoogleLayer(defMap.getId(), LayerType.HYBRID), defMap);
//		}

	}

	private void addBaseLayer(MapModel mapModel, BaseLayer baseLayer, MapArea forMap) {
		Envelope<G2D> permittedBounds = permittedMapArea.get(forMap);
		/*
		 * TODO: устанавливать bounds. сейчас mbr регионов посчитаны по зданиям, а настоящие поэтому не правильно
		 * ограничивать просмотр по ним. Ведь тогда пользователь не увидит область своего региона, где ещё нет зданий.
		 * 
		 * if (permittedBounds != null) { baseLayer.setBounds(permittedBounds); }
		 */

		if (permittedBounds != null) {
			baseLayer.setDefaultViewportBounds(permittedBounds);
		}
		mapModel.addBaseLayer(baseLayer);
	}

}
