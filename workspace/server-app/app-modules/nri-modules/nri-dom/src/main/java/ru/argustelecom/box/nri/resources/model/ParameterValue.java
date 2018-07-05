package ru.argustelecom.box.nri.resources.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.resources.model.nls.ParameterValueMessagesBundle;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Значение параметра
 * Created by s.kolyada on 18.09.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "parameter_value")
@Getter
@Setter
public class ParameterValue extends BusinessObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Значение
	 */
	@Column(name = "value")
	private String value;

	/**
	 * Ресурс, к которому относится данный параметр
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "resource_id", nullable = false)
	private ResourceInstance resource;

	/**
	 * Спецификация параметра
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parameter_specification_id", nullable = false)
	private ParameterSpecification specification;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ParameterValue() {
		super();
	}

	/**
	 * Конструктор
	 * @param id id
	 * @param value значение параметра
	 * @param resource ресурс, которому параметр принадлежит
	 * @param specification спецификация параметра
	 */
	@Builder
	public ParameterValue(Long id, String value, ResourceInstance resource, ParameterSpecification specification) {
		super(id);
		this.value = value;
		this.resource = resource;
		this.specification = specification;
	}

	public void setValue(String value) {
		Validate.notNull(specification,LocaleUtils.getMessages(ParameterValueMessagesBundle.class).specificationIsNull());
		if (specification.validate(value)) {
			this.value = value;
		} else {
			throw new IllegalArgumentException(LocaleUtils.getMessages(ParameterValueMessagesBundle.class).invalidParameter());
		}
	}
}
