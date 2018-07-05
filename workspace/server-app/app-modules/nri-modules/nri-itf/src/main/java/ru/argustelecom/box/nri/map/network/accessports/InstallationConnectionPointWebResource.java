package ru.argustelecom.box.nri.map.network.accessports;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.G2D;
import ru.argustelecom.system.inf.map.geojson.AbstractFeatureWebResource;
import ru.argustelecom.system.inf.map.geojson.FeatureCollection;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Path;
import java.util.Collections;

@Path(InstallationConnectionPointWebResource.ICP_FEATURE_WEB_RESOURCE_PATH)
public class InstallationConnectionPointWebResource
		extends AbstractFeatureWebResource<InstallationConnectionPointQueryParams> {

	public final static String ICP_FEATURE_WEB_RESOURCE_PATH = "techservice/ptvmap/installationconnectionpoint";

	@PersistenceContext
	private EntityManager em;

//	@Inject
//	private InstallationConnectionPointRepositoryImpl installationConnectionPointRepository;

	@Override
	protected FeatureCollection getFeatureCollection(InstallationConnectionPointQueryParams params,
			Envelope<G2D> envelope) {
//		Region region = em.find(Region.class, ArgusPrincipal.instance().getHomeRegionId());
//		Set<ObjectState> statusSet = params.getObjectStates().stream().map(x -> em.find(ObjectState.class, x)).collect(Collectors.toSet());
//		AccessTechFamily atf = em.find(AccessTechFamily.class, params.getTechFamily());
//		ConnectionPointPositionsLoadCriteria criteria = new ConnectionPointPositionsLoadCriteria(region,
//				atf, params.getMinAvailableCount(), statusSet);
//		Collection<InstallationConnectionPointsPosition> icps = installationConnectionPointRepository
//				.loadPositions(criteria, params.getMapId(), envelope);
//		return new FeatureCollection(icps.stream().map(this::createFeature).collect(Collectors.toList()));
		return new FeatureCollection(Collections.emptyList());
	}

//	protected Feature createFeature(InstallationConnectionPointsPosition icps) {
//		Feature feature = new Feature(FeatureUtils.calcId(Building.class, icps.getBuildingId()),
//				FeatureUtils.createPoint(icps.getPosition()));
//		feature.getProperties().put(Feature.BASIC_PROP_OBJECT_NAME, icps.getBuildingName());
//		feature.getProperties().put(Feature.CLIENT_DATA_KEY, icps.getAvailableCount());
//		feature.getProperties().put("title", icps.getBuildingName());
//
//		return feature;
//	}

	public String prepareRequestUrl(ConnectionPointPositionsLoadCriteria criteria) {
		String path = String.format(
				"%s/webresources/%s?mapId={mapId}&regionId={regionId}&envelope={envelope}&oldEnvelope={oldEnvelope}",
				FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath(),
				ICP_FEATURE_WEB_RESOURCE_PATH);
		StringBuilder objectStates = new StringBuilder(path);
		criteria.getObjectStates()
				.forEach(x -> objectStates.append(String.format("&objectStates=%s", x.getName())));
		return objectStates.toString();
	}

}
