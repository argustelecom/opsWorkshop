package ru.argustelecom.box.nri.integration;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.box.integration.nri.service.ServiceInfoService;
import ru.argustelecom.box.integration.nri.service.model.ResourceRepresentation;
import ru.argustelecom.box.integration.nri.service.model.ResourceType;
import ru.argustelecom.box.nri.loading.ResourceLoadingAppService;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса получения информации об услуге из ТУ
 * см. BOX-2738
 * Created by s.kolyada on 11.04.2018.
 */
@DomainService
@Alternative
@Priority(Interceptor.Priority.APPLICATION)
public class ServiceInfoServiceImpl implements ServiceInfoService {

	private static final Map<LogicalResourceType, ResourceType> logicalResourceTypesCorrelation = ImmutableMap
			.<LogicalResourceType, ResourceType>builder()
			.put(LogicalResourceType.IP_ADDRESS, ResourceType.IP_ADDRESS)
			.put(LogicalResourceType.PHONE_NUMBER, ResourceType.PHONE_NUMBER)
			.build();

	@Inject
	private ResourceLoadingAppService resourceLoadingAppService;

	@Override
	public Set<ResourceRepresentation> allLoadedResourcesByService(Service service) {

		List<LogicalResourceDto> loadings = resourceLoadingAppService.loadAllLoadedResourcesByService(service);

		if (CollectionUtils.isEmpty(loadings)) {
			return Collections.emptySet();
		}

		return loadings.stream()
				.map(this::convertLogicalResource)
				.collect(Collectors.toSet());
	}

	/**
	 * Преобразорвание логического ресурса ТУ в интеграционное представление
	 * @param lrDto логический ресурс
	 * @return логический ресурс в понятиях интеграции
	 */
	private ResourceRepresentation convertLogicalResource(LogicalResourceDto lrDto) {
		if (lrDto == null) {
			return null;
		}
		// получаем интеграционный тип логического ресурса, если его нет,
		// значит такой тип не поддерэивается данной интеграцией и нас не интересует
		ResourceType resourceType = logicalResourceTypesCorrelation.get(lrDto.getType());
		if (resourceType == null) {
			return null;
		}
		return new ResourceRepresentation(resourceType, lrDto.getId(), lrDto.getObjectName());
	}
}
