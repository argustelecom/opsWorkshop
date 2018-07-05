package ru.argustelecom.box.nri.resources.spec;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Iterables.isEmpty;

/**
 * ДТО спецификации ресурса
 * Created by s.kolyada on 19.09.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class ResourceSpecificationDto extends ConvertibleDto implements Serializable, Identifiable {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	private Long id;

	/**
	 * Имя спецификации
	 */
	private String name;

	/**
	 * Класс иконки спецификации
	 */
	private String image;

	/**
	 * Независимая
	 */
	private Boolean isIndependent;

	/**
	 * Поддерживаемые логические ресурсы
	 */
	private Set<LogicalResourceType> supportedLogicalResources;

	/**
	 * Поддерживаемые типы портов
	 */
	private Set<PortType> supportedPortTypes;

	/**
	 * Параметры ресурса
	 */
	private List<ParameterSpecificationDto> parameters = new ArrayList<>();

	/**
	 * Дочерние спецификации, для которых эта спецификация может являться контейнером
	 */
	private List<ResourceSpecificationDto> childSpecifications = new ArrayList<>();

	/**
	 * Конструктор
	 * @param id идентификатор
 	 * @param name имя
	 * @param parameters параметры
	 * @param childSpecifications дочерние спецификации
	 * @param image класс иконки спецификации
	 * @param isIndependent независимый
	 * @param supportedLogicalResources поддерживаемые логические ресурсы
	 * @param supportedPortTypes поддерживаемые типы портов
	 */
	@Builder
	public ResourceSpecificationDto(Long id, String name, List<ParameterSpecificationDto> parameters
			, List<ResourceSpecificationDto> childSpecifications, String image, Boolean isIndependent,
			Set<LogicalResourceType> supportedLogicalResources, Set<PortType> supportedPortTypes) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.isIndependent = isIndependent;
		this.parameters = Optional.ofNullable(parameters).orElse(new ArrayList<>());
		this.childSpecifications = Optional.ofNullable(childSpecifications).orElse(new ArrayList<>());
		this.supportedLogicalResources = Optional.ofNullable(supportedLogicalResources).orElse(new HashSet<>());
		this.supportedPortTypes = Optional.ofNullable(supportedPortTypes).orElse(Collections.emptySet());
	}

	@Override
	public Class<ResourceSpecificationDtoTranslator> getTranslatorClass() {
		return ResourceSpecificationDtoTranslator.class;
	}

	@Override
	public Class<ResourceSpecification> getEntityClass() {
		return ResourceSpecification.class;
	}

	/**
	 * Проверяет, поддерживается ли логический ресурс данной спецификацией
	 * @param logicalResourceType тип логичческого ресурса
	 * @return истина если поддерживается, иначе ложь
	 */
	public boolean supportsLogicalResource(LogicalResourceType logicalResourceType) {
		return !isEmpty(supportedLogicalResources)
				&& supportedLogicalResources.contains(logicalResourceType);
	}
}
