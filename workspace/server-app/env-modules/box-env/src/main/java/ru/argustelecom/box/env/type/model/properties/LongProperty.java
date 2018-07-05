package ru.argustelecom.box.env.type.model.properties;

import static com.google.common.base.Preconditions.checkState;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;

@Entity
@Access(AccessType.FIELD)
public class LongProperty extends AbstractNumericProperty<Long> {

	private static final long serialVersionUID = 8960766831115743098L;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected LongProperty() {
		super();
	}

	/**
	 * Конструктор предназначен для инстанцирования свойства его холдером. Не делай этот конструктор публичным. Не делай
	 * других публичных конструкторов. Свойство должны инстанцироваться сугубо холдером или спецификацией (делегирует
	 * холдеру) для обеспецения корректного связывания холдера(спецификации) и свойства.
	 *
	 * @param holder
	 *            - владелец свойства, часть спецификации
	 * @param id
	 *            - уникальный идентификатор свойства. Получается при помощи генератора инкапсулированного в
	 *            MetadataUnit.generateId()
	 *
	 * @see TypePropertyHolder#createProperty(Class, String, Long)
	 * @see MetadataUnit#generateId()
	 * @see MetadataUnit#generateId(javax.persistence.EntityManager)
	 */
	protected LongProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	@Override
	public Class<?> getValueClass() {
		return Long.class;
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "long_min_value")
	public Long getMinValue() {
		return super.getMinValue();
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "long_max_value")
	public Long getMaxValue() {
		return super.getMaxValue();
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "long_default")
	public Long getDefaultValue() {
		return super.getDefaultValue();
	}

	@Override
	protected boolean validateRange(Long value) {
		boolean greaterThanMinimum = getMinValue() == null || Long.compare(value, getMinValue()) >= 0;
		boolean lessThanMaximum = getMaxValue() == null || Long.compare(value, getMaxValue()) <= 0;

		return greaterThanMinimum && lessThanMaximum;
	}

	@Override
	protected Long extractValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		return JsonHelper.LONG.get(propertiesRoot, qualifiedName);
	}

	@Override
	protected String extractValueAsString(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		Long value = extractValue(context, propertiesRoot, qualifiedName);
		return value != null ? formatWithPrecision(value) : null;
	}

	@Override
	protected void putValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName, Long value) {
		checkState(value != null);
		checkValue(value);
		JsonHelper.LONG.set(propertiesRoot, qualifiedName, value);
	}
}
