package ru.argustelecom.box.nri.resources.inst;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.model.ResourceStatus;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * ДТО ресурса
 * @author a.wisniewski
 * @since 19.09.2017
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class ResourceInstanceDto extends ConvertibleDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private Long id;

	/**
	 * Имя ресурса
	 */
	@Setter
	private String name;

	/**
	 * Спецификация ресруса
	 */
	@Setter
	private ResourceSpecificationDto specification;

	/**
	 * Параметры ресурса
	 */
	private List<ParameterValueDto> parameterValues = new ArrayList<>();

	/**
	 * Стутус
	 */
	@Setter
	private ResourceStatus status = ResourceStatus.defaultStatus();

	/**
	 * Дочерние ресурсы
	 */
	private List<ResourceInstanceDto> children = new ArrayList<>();

	/**
	 * Номера
	 */
	private List<LogicalResourceDto> logicalResources = new ArrayList<>();

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param specification спецификация
	 * @param parameterValues значения параметров
	 * @param status  статус
	 * @param children дочерние ресурсы
	 * @param services услуги
	 * @param name имя
	 * @param logicalResources логические ресурсы
	 */
	@Builder
	public ResourceInstanceDto(Long id, ResourceSpecificationDto specification, List<ParameterValueDto> parameterValues,
							   ResourceStatus status, List<ResourceInstanceDto> children, List<Service> services,
							   String name, List<LogicalResourceDto> logicalResources) {
		this.id = id;
		this.specification = specification;
		this.parameterValues = Optional.ofNullable(parameterValues).orElse(new ArrayList<>());
		this.status = status;
		this.children = Optional.ofNullable(children).orElse(new ArrayList<>());
		this.logicalResources = Optional.ofNullable(logicalResources).orElse(new ArrayList<>());
		this.name = name;
	}

	@Override
	public Class<ResourceInstanceDtoTranslator> getTranslatorClass() {
		return ResourceInstanceDtoTranslator.class;
	}

	@Override
	public Class<ResourceInstance> getEntityClass() {
		return ResourceInstance.class;
	}

	/**
	 * Добавить дочерний ресурс
	 * @param child дочерний ресурс
	 * @return истина. если успешно, иначе ложь
	 */
	public Boolean addChild(ResourceInstanceDto child) {
		return this.children.add(child);
	}

	/**
	 * Добавить параметр
	 * @param value параметр
	 * @return истина, если успех, иначе ложь
	 */
	public Boolean addParameterValue(ParameterValueDto value) {
		return this.parameterValues.add(value);
	}

	/**
	 * Добавить параметры
	 * @param values параметры
	 * @return истина, если успех, иначе ложь
	 */
	public Boolean addParameterValues(Collection<ParameterValueDto> values) {
		return this.parameterValues.addAll(values);
	}

	@Override
	public String getObjectName() {
		return name;
	}

	/**
	 * Проверить есть среди логических ресурсов ресурс с идентификатором
	 * @param id идентификатор
	 * @return истина, если есть, иначе ложь
	 */
	public boolean containsLogicalResourceWithId(Long id) {
		if (id == null) {
			return false;
		}
		return logicalResources.stream()
				.filter(lr -> lr.getId().equals(id))
				.findAny()
				.isPresent();
	}
}