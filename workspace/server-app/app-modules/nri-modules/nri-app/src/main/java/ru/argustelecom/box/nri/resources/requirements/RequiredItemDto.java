package ru.argustelecom.box.nri.resources.requirements;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredItem;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ДТО требования
 * Created by s.kolyada on 19.09.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class RequiredItemDto extends ConvertibleDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	protected Long id;

	/**
	 * Спецификация требуемого ресурса
	 */
	private ResourceSpecificationDto resourceSpecification;

	/**
	 * Дочерние требования
	 */
	private List<RequiredItemDto> children = new ArrayList<>();

	/**
	 * Требуемые параметры
	 */
	private List<RequiredParameterValueDto> requiredParameters = new ArrayList<>();

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param resourceSpecification спецификация ресурса
	 * @param children дочерние трелования
	 * @param requiredParameters требуемые параметры
	 */
	@Builder
	public RequiredItemDto(Long id, ResourceSpecificationDto resourceSpecification, List<RequiredItemDto> children,
						   List<RequiredParameterValueDto> requiredParameters) {
		this.id = id;
		this.resourceSpecification = resourceSpecification;
		if (CollectionUtils.isEmpty(children)) {
			this.children = new ArrayList<>();
		} else {
			this.children = children;
		}
		if (CollectionUtils.isEmpty(requiredParameters)) {
			this.requiredParameters = new ArrayList<>();
		} else {
			this.requiredParameters = requiredParameters;
		}
	}

	@Override
	public Class<RequiredItemDtoTranslator> getTranslatorClass() {
		return RequiredItemDtoTranslator.class;
	}

	@Override
	public Class<RequiredItem> getEntityClass() {
		return RequiredItem.class;
	}

	/**
	 * Имя для ноды
	 * @return
	 */
	public String getName(){
		return resourceSpecification.getName();
	}

	@Override
	public String getObjectName() {
		return resourceSpecification.getName();
	}
}
