package ru.argustelecom.box.nri.loading;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.loading.model.ResourceLoading;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Транслятор нагрузки на русурс
 * Created by s.kolyada on 06.02.2018.
 */
@DtoTranslator
public class ResourceLoadingDtoTranslator  implements DefaultDtoTranslator<ResourceLoadingDto, ResourceLoading> {

	/**
	 * Транслятор логических ресурсов
	 */
	@Inject
	private LogicalResourceDtoTranslator logicalResourceDtoTranslator;

	@Override
	public ResourceLoadingDto translate(ResourceLoading businessObject) {
		if (businessObject == null) {
			return null;
		}

		return ResourceLoadingDto.builder()
				.id(businessObject.getId())
				.loadingName(businessObject.getLoadingName())
				.resources(processResources(businessObject.getLoadedLogicalResource()))
				.build();
	}

	/**
	 * Обработать ресурс
	 * @param loadedLogicalResource логический ресурсы
	 * @return список дто логических ресурсов
	 */
	private List<LogicalResourceDto> processResources(Set<LogicalResource> loadedLogicalResource) {
		return loadedLogicalResource.stream()
				.map(logicalResourceDtoTranslator::translate)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}
