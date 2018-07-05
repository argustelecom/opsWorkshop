package ru.argustelecom.box.nri.resources.spec.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.regex.Pattern;

/**
 * Спецификация параметра
 * Created by s.kolyada on 18.09.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "parameter_specification")
@Getter
@Setter
public class ParameterSpecification extends BusinessObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Имя параметра
	 */
	@Column(name = "name", nullable = false, scale = 64)
	private String name;

	/**
	 * Обязательность параметра
	 */
	@Column(name = "required", nullable = false)
	private Boolean required = false;

	/**
	 * Регулярное выражение для валидации
	 */
	@Column(name = "regex")
	private String regex;

	/**
	 * Значение по умолчанию
	 */
	@Column(name = "default_value")
	private String defaultValue;

	/**
	 * Спецификация ресурса, к которой относится данный параметр
	 */
	@ManyToOne
	@JoinColumn(name = "resource_specification_id", nullable = false)
	private ResourceSpecification resourceSpecification;

	/**
	 * Тип данных
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "data_type", nullable = false)
	private ParameterDataType dataType;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ParameterSpecification() {
		super();
	}

	/**
	 * Конструктор
	 *
	 * @param id                    идентификатор
	 * @param name                  имя
	 * @param required              обязательность
	 * @param regex                 регулярное выражение для валидации
	 * @param defaultValue          значение поумолчанию
	 * @param resourceSpecification спецификация ресурса
	 * @param dataType              тип данных
	 */
	@Builder
	public ParameterSpecification(Long id, String name, Boolean required, String regex, String defaultValue,
								  ResourceSpecification resourceSpecification, ParameterDataType dataType) {
		super(id);
		this.name = name;
		this.required = required;
		this.regex = regex;
		this.defaultValue = defaultValue;
		this.resourceSpecification = resourceSpecification;
		this.dataType = dataType;
	}

	/**
	 * Валидация значения
	 *
	 * @param value значение
	 * @return истина если валидно, иначе ложь
	 */
	public Boolean validate(String value) {
		if (StringUtils.isBlank(value)) {
			return !required;
		}
		if (StringUtils.isEmpty(regex)) {
			return dataType.validate(value);
		} else {
			return Pattern.compile(regex).matcher(value).matches();
		}
	}

	/**
	 * Запрос к данному типу
	 */
	public static class ParameterSpecificationTypeQuery extends EntityQuery<ParameterSpecification> {

		/**
		 * Конструктор запроса
		 */
		public ParameterSpecificationTypeQuery() {
			super(ParameterSpecification.class);
		}
	}
}
