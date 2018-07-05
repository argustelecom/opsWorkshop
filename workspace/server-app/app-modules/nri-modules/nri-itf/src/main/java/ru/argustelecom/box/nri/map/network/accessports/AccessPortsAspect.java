package ru.argustelecom.box.nri.map.network.accessports;

import org.jboss.logging.Logger;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.map.page.CurrentMapObject;
import ru.argustelecom.box.env.map.page.aspects.MapAspect;
import ru.argustelecom.box.env.map.page.aspects.MapWidget;
import ru.argustelecom.box.nri.resources.ResourceInstanceRepository;
import ru.argustelecom.box.nri.resources.model.ResourceState;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;
import ru.argustelecom.system.inf.login.ArgusPrincipal;
import ru.argustelecom.system.inf.map.component.model.MapModel;
import ru.argustelecom.system.inf.map.component.model.overlayer.feature.MarkerClusterLayerAjax;
import ru.argustelecom.system.inf.map.geojson.FeatureUtils;
import ru.argustelecom.system.inf.page.PresentationModel;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Аспект "Порты доступа".
 */
@PresentationModel
public class AccessPortsAspect extends MapAspect {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(AccessPortsAspect.class);


	@PersistenceContext
	private EntityManager em;
	@Inject
	private MapWidget mapWidget;

	@Inject
	private AccessPortsSettingsFrameState settingsFrameState;

	@Inject
	private AccessPointSiteFeatureWebResource accessPointSiteFeatureWebResource;

	@Inject
	private CurrentMapObject currentMapObject;

	private static final String ACCESS_PORT_LAYER_NAME = "Порты доступа";
	private static final String ACCESS_PORT_LAYER_ID = "ap_layer";

	@Override
	public void populateMapModel(MapModel mapModel) {
		MarkerClusterLayerAjax overlayer;
		// удалим наши слои из карты, если есть
		mapModel.removeOverLayer(ACCESS_PORT_LAYER_ID);

		overlayer = createMarkerClusterLayer(ACCESS_PORT_LAYER_ID, ACCESS_PORT_LAYER_NAME,
				"b2cAccessPortsLayerOptions", mapModel.getActiveBaseLayer().getMapId());
		String[] urls = new String[]{accessPointSiteFeatureWebResource
				.prepareRequestUrl(settingsToFilters(settingsFrameState.getSettings()))};
		overlayer.setUrls(urls);
		mapModel.addOverLayer(overlayer);

		mapObjectChanged();
	}

	private MarkerClusterLayerAjax createMarkerClusterLayer(String id, String name, String layerOptionsName, Long mapId) {
		log.debugv("Добавляю слой {0}", id);
		MarkerClusterLayerAjax layer = new MarkerClusterLayerAjax(id, name);
		layer.setRequestParamRegionId(ArgusPrincipal.instance().getHomeRegionId());
		layer.setRequestParamMapId(mapId);
		layer.setLayerOptionsName(layerOptionsName);
		return layer;
	}

	private ConnectionPointPositionsLoadCriteria settingsToFilters(AccessPortsSettingsFrameState.Settings settings) {
		Collection<ResourceState> objectStates = settingsFrameState.getSettings().getObjectStates();
		if (!settingsFrameState.isAllObjectStates()) {
			checkState(!settings.getObjectStates().isEmpty());
			objectStates.removeIf(os -> !settings.getObjectStates().contains(os));
		}
		return new ConnectionPointPositionsLoadCriteria(null, em.find(ResourceSpecification.class, settings.getSpecId()), objectStates);
	}

	@Override
	public void mapObjectChanged() {
		// кривость: "рендеринг" делается слишком рано
		showMarkerForCurrentMapObjectIfAppropriate();
	}

	private void showMarkerForCurrentMapObjectIfAppropriate() {
		mapWidget.get().deselectAllFeatures();
		Building building = currentMapObject.getValueAsBuilding();
		if (building != null) {
			String featureId = FeatureUtils.calcId(building);
			mapWidget.get().selectMapObjectFeatures(featureId);
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
		return AccessPortsSettingsFrameState.Settings.class;
	}

	@Override
	public Set<Class> rendersEntities() {
		return Collections.emptySet();
	}

}
