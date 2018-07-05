package ru.argustelecom.box.nri.map.network.accessports;

import ru.argustelecom.system.inf.map.geojson.FeatureRestParams;

import javax.ws.rs.QueryParam;
import java.util.Set;

public class InstallationConnectionPointQueryParams extends FeatureRestParams {

	public static final String TECH_FAMILY_QUERY_PARAM_NAME = "techFamily";
	public static final String MIN_AVAILABLE_COUNT_QUERY_PARAM_NAME = "minAvailableCount";
	public static final String OBJECT_STATES_QUERY_PARAM_NAME = "objectStates";

	@QueryParam(TECH_FAMILY_QUERY_PARAM_NAME)
	private Long techFamily;

	@QueryParam(MIN_AVAILABLE_COUNT_QUERY_PARAM_NAME)
	private Integer minAvailableCount;

	@QueryParam(OBJECT_STATES_QUERY_PARAM_NAME)
	private Set<Long> objectStates;

	public Long getTechFamily() {
		return techFamily;
	}

	public Integer getMinAvailableCount() {
		return minAvailableCount;
	}

	public Set<Long> getObjectStates() {
		return objectStates;
	}

}
