package ru.argustelecom.box.nri.map.network.accessports;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.G2D;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Region;
import ru.argustelecom.box.nri.map.network.accessports.model.AccessPortsPosition;
import ru.argustelecom.box.nri.resources.ResourceInstanceRepository;
import ru.argustelecom.box.nri.resources.model.ResourceState;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;
import ru.argustelecom.system.inf.login.ArgusPrincipal;
import ru.argustelecom.system.inf.map.geojson.AbstractFeatureWebResource;
import ru.argustelecom.system.inf.map.geojson.Feature;
import ru.argustelecom.system.inf.map.geojson.FeatureCollection;
import ru.argustelecom.system.inf.map.geojson.FeatureUtils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * рест сервис для карты
 */
@Path(AccessPointSiteFeatureWebResource.ACCESS_PORTS_FEATURE_WEB_RESOURCE_PATH)
public class AccessPointSiteFeatureWebResource extends AbstractFeatureWebResource<AccessPointFeatureQueryParams> {

	public final static String ACCESS_PORTS_FEATURE_WEB_RESOURCE_PATH = "techservice/ptvmap/accessports";

	/**
	 * Энтити менеджер
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Репозиторий для работы с инстансами ресурсов
	 */
	@Inject
	private ResourceInstanceRepository resourceInstanceRepository;

	@Override
	protected FeatureCollection getFeatureCollection(AccessPointFeatureQueryParams params, Envelope<G2D> envelope) {
		Region region = em.find(Region.class, ArgusPrincipal.instance().getHomeRegionId());

		Set<ResourceState> statusSet = params.getObjectStates();
		ConnectionPointPositionsLoadCriteria criteria = new ConnectionPointPositionsLoadCriteria(region,em.find(ResourceSpecification.class,params.getSpec()), statusSet);

		return new FeatureCollection(
				resourceInstanceRepository.loadPositions(criteria, params.getMapId(), params.getEnvelope()).stream()
						.map(this::createFeatureFromAccessPoint).collect(Collectors.toList()));
	}

	protected Feature createFeatureFromAccessPoint(AccessPortsPosition accessPortsPosition) {
		Feature feature = new Feature(FeatureUtils.calcId(Building.class, accessPortsPosition.getBuildingId()),
				FeatureUtils.createPoint(accessPortsPosition.getPosition()));
		feature.getProperties().put(Feature.CLIENT_DATA_KEY, accessPortsPosition.getValue());
		feature.getProperties().put("title", accessPortsPosition.getBuildingName());
		return feature;
	}

	/**
	 * Делает юрлы
	 * @param criteria опции для филтра
	 * @return юрл
	 */
	public String prepareRequestUrl(ConnectionPointPositionsLoadCriteria criteria) {
		UriBuilder url = prepareUriBuilder(ACCESS_PORTS_FEATURE_WEB_RESOURCE_PATH);
		url.queryParam("spec",criteria.getRs().getId());
		criteria.getObjectStates().forEach(x -> url.queryParam("objectStates", x.toString()));
		return url.toTemplate();
	}
}
