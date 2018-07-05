package ru.argustelecom.box.nri.coverage;

import org.springframework.util.Assert;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.building.BuildingElementDto;
import ru.argustelecom.box.nri.building.BuildingElementDtoTranslator;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDtoTranslator;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Транслятор точки монтирования
 * Created by s.kolyada on 31.08.2017.
 */
@DtoTranslator
public class ResourceInstallationDtoTranslator
		implements DefaultDtoTranslator<ResourceInstallationDto, ResourceInstallation> {

	@Inject
	private BuildingElementDtoTranslator elementDtoTranslator;

	@Inject
	private ResourceInstanceDtoTranslator resourceDtoTranslator;

	@Override
	public ResourceInstallationDto translate(ResourceInstallation businessObject) {
		if (businessObject == null)
			return null;
		ResourceInstallationDto dto =  ResourceInstallationDto.builder()
				.id(businessObject.getId())
				.comment(businessObject.getComment())
				.build();
		initInstallationElement(dto, businessObject);
		initCoverage(dto, businessObject);
		initResource(dto,businessObject);
		return dto;
	}

	/**
	 * Доинициализировать ресурс
	 * @param dto дто
	 * @param businessObject сущность
	 * @return дто с указанием ресурса
	 */
	public ResourceInstallationDto initResource(ResourceInstallationDto dto, ResourceInstallation businessObject){
		Assert.notNull(dto);
		Assert.notNull(businessObject);
		if (businessObject.getResource() != null) {
			dto.setResource(resourceDtoTranslator.translate(businessObject.getResource()));
		}
		return dto;
	}
	/**
	 * Доинициализировать точку монтирования
	 * @param dto дто
	 * @param businessObject сущность
	 * @return дто с указанием точки монтирования
	 */
	public ResourceInstallationDto initInstallationElement(ResourceInstallationDto dto, ResourceInstallation businessObject) {
		Assert.notNull(dto);
		Assert.notNull(businessObject);
		if (businessObject.installedAt != null) {
			dto.setInstalledAt(elementDtoTranslator.translate(businessObject.installedAt));
		}
		return dto;
	}

	/**
	 * Доинициализировать покрытие
	 * @param dto дто
	 * @param businessObject сущность
	 * @return
	 */
	public ResourceInstallationDto initCoverage(ResourceInstallationDto dto, ResourceInstallation businessObject) {
		Assert.notNull(dto);
		Assert.notNull(businessObject);
		List<BuildingElementDto> coveredElements = Optional.ofNullable(businessObject.cover)
				.orElseGet(ArrayList::new)
				.stream()
				.map(elementDtoTranslator::translate)
				.collect(Collectors.toList());
		dto.setCover(coveredElements);
		return dto;
	}
}
