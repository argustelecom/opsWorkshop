package ru.argustelecom.box.nri.resources.spec;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.nri.resources.spec.model.ParameterDataType;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import java.io.Serializable;

/**
 * ДТО спецификации параметра
 * Created by s.kolyada on 19.09.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class ParameterSpecificationDto extends ConvertibleDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	private Long id;

	/**
	 * Имя параметра
	 */
	private String name;

	/**
	 * Обязательность параметра
	 */
	private Boolean required = false;

	/**
	 * Регулярно выражение для валидации
	 */
	private String regex;

	/**
	 * Значение по умолчанию
	 */
	private String defaultValue;

	/**
	 * Тип данных
	 */
	private ParameterDataType dataType;

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param name имя
	 * @param required обязательность заполнения
	 * @param regex регулярное выражение
	 * @param defaultValue значение поумолчанию
	 * @param dataType тип данных
	 */
	@Builder
	public ParameterSpecificationDto(Long id, String name, Boolean required, String regex, String defaultValue,
									 ParameterDataType dataType) {
		this.id = id;
		this.name = name;
		this.required = required;
		this.regex = regex;
		this.defaultValue = defaultValue;
		this.dataType = dataType;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return ParameterSpecificationDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return ParameterSpecification.class;
	}
}
