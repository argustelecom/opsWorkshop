package ru.argustelecom.box.nri.resources.inst;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.resources.model.ParameterValue;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;

import java.io.Serializable;

/**
 * ДТО значения параметра
 * @author a.wisniewski
 * @since 19.09.2017
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class ParameterValueDto extends ConvertibleDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private Long id;

	/**
	 * Значение
	 */
	@Setter
	private String value;

	/**
	 * Спецификация параметра
	 */
	private ParameterSpecificationDto specification;

	/**
	 * Конструктор
	 * @param id id
	 * @param value значение
	 * @param specification спецификация
	 */
	@Builder(toBuilder = true)
	public ParameterValueDto(Long id, String value, ParameterSpecificationDto specification) {
		this.id = id;
		this.value = value;
		this.specification = specification;
	}

	@Override
	public Class<ParameterValueDtoTranslator> getTranslatorClass() {
		return ParameterValueDtoTranslator.class;
	}

	@Override
	public Class<ParameterValue> getEntityClass() {
		return ParameterValue.class;
	}
}
