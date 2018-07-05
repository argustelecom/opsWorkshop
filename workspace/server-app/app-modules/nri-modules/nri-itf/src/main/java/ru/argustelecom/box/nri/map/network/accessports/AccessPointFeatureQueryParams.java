package ru.argustelecom.box.nri.map.network.accessports;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.resources.model.ResourceState;
import ru.argustelecom.system.inf.map.geojson.FeatureRestParams;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * Параметры
 */
public class AccessPointFeatureQueryParams extends FeatureRestParams {

	/**
	 * имя параметра спецификации
	 */
	public static final String SPEC_QUERY_PARAM_NAME = "spec";
	/**
	 * имя параметра состояния объекта в строке запроса
	 */
	public static final String OBJECT_STATES_QUERY_PARAM_NAME = "objectStates";


	@Setter
	@Getter
	@QueryParam(SPEC_QUERY_PARAM_NAME)
	private Long spec;

	@Getter
	@Setter
	@QueryParam(OBJECT_STATES_QUERY_PARAM_NAME)
	private Set<ResourceState> objectStates;
}