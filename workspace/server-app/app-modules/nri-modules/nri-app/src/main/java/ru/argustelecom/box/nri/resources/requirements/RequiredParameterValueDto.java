package ru.argustelecom.box.nri.resources.requirements;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.schema.requirements.resources.comparators.CompareAction;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredParameterValue;

import java.io.Serializable;

/**
 * ДТО требования к значению параметра ресурса
 * Created by s.kolyada on 19.09.2017.
 */
@Setter
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class RequiredParameterValueDto extends ConvertibleDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	private Long id;

	/**
	 * Спецификация параметра
	 */
	private ParameterSpecificationDto parameterSpecification;

	/**
	 * Требуемое значение
	 */
	private String value;

	/**
	 * Тип операции сравнения
	 */
	private CompareAction compareAction;


	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param parameterSpecification спецификация
	 * @param value требуемое значение
	 * @param compareAction действие сравнения
	 */
	@Builder
	public RequiredParameterValueDto(Long id, ParameterSpecificationDto parameterSpecification, String value,
									 CompareAction compareAction) {
		this.id = id;
		this.parameterSpecification = parameterSpecification;
		this.value = value;
		this.compareAction = compareAction;
	}

	@Override
	public Class<RequiredParameterValueDtoTranslator> getTranslatorClass() {
		return RequiredParameterValueDtoTranslator.class;
	}

	@Override
	public Class<RequiredParameterValue> getEntityClass() {
		return RequiredParameterValue.class;
	}
}
