package ru.argustelecom.box.nri.schema.requirements.resources.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.nri.schema.requirements.resources.comparators.CompareAction;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Требование к значению параметра ресурса
 * Created by s.kolyada on 18.09.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "required_parameter_value")
@Getter
@Setter
public class RequiredParameterValue extends BusinessObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Спецификация параметра
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parameter_specification_id", nullable = false)
	private ParameterSpecification parameterSpecification;

	/**
	 * Требуемый ресурс
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "required_item_id", nullable = false)
	private RequiredItem requiredItem;

	/**
	 * Требуемое значение
	 */
	@Column(name = "required_value", nullable = false)
	private String requiredValue;

	/**
	 * Тип операции сравнения
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "action", nullable = false)
	private CompareAction compareAction;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected RequiredParameterValue() {
		super();
	}

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param parameterSpecification спека
	 * @param requiredValue требуемое значение
	 * @param compareAction действие сравнения
	 * @param requiredItem  требуемый ресурс
	 */
	@Builder
	public RequiredParameterValue(Long id, ParameterSpecification parameterSpecification, String requiredValue,
								  CompareAction compareAction, RequiredItem requiredItem) {
		this.id = id;
		this.parameterSpecification = parameterSpecification;
		this.requiredValue = requiredValue;
		this.compareAction = compareAction;
		this.requiredItem = requiredItem;
	}
}
