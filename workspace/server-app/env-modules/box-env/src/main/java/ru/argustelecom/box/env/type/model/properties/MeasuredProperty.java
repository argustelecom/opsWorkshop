package ru.argustelecom.box.env.type.model.properties;

import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.measure.model.MeasuredValue;
import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;

@Entity
@Access(AccessType.FIELD)
public class MeasuredProperty extends AbstractMeasuredProperty<MeasuredValue> {

	private static final long serialVersionUID = -2482003195180929137L;

	protected static final String STORED_VALUE_TOKEN = "storedValue";

	@Embedded
	@AttributeOverride(name = "storedValue", column = @Column(name = "measure_default"))
	@AssociationOverride(name = "storedUnit", joinColumns = { @JoinColumn(name = "measure_default_unit_id") })
	private MeasuredValue defaultValue;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected MeasuredProperty() {
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
	protected MeasuredProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	@Override
	protected void escapeDefaultValue() {
		defaultValue = null;
	}

	@Override
	public Class<?> getValueClass() {
		return MeasuredValue.class;
	}

	@Override
	public MeasuredValue getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(MeasuredValue defaultValue) {
		if (defaultValue == null) {
			this.defaultValue = null;
			return;
		}

		checkValue(defaultValue);
		if (!Objects.equals(this.defaultValue, defaultValue)) {
			if (getMeasureUnit() == null) {
				directsetMeasureUnit(defaultValue.getMeasureUnit());
			}
			this.defaultValue = defaultValue;
		}
	}

	@Override
	public String getDefaultValueAsString() {
		return defaultValue != null ? defaultValue.toString() : null;
	}

	@Override
	protected MeasuredValue extractValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		JsonNode valueNode = propertiesRoot.get(qualifiedName);
		if (valueNode == null || valueNode.isNull()) {
			return null;
		}

		checkState(valueNode instanceof ObjectNode);
		ObjectNode valueRoot = (ObjectNode) valueNode;

		Long storedValue = JsonHelper.LONG.get(valueRoot, STORED_VALUE_TOKEN);
		MeasureUnit unit = JsonHelper.ENTITY.get(valueRoot, MEASURE_UNIT_TOKEN, MeasureUnit.class, getEntityConverter());

		return new MeasuredValue(storedValue, unit);
	}

	@Override
	protected String extractValueAsString(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		MeasuredValue value = extractValue(context, propertiesRoot, qualifiedName);
		return value != null ? value.toString() : null;
	}

	@Override
	protected void putValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName,
			MeasuredValue value) {

		checkState(value != null);

		ObjectNode valueRoot = propertiesRoot.putObject(qualifiedName);
		JsonHelper.LONG.set(valueRoot, STORED_VALUE_TOKEN, value.getStoredValue());
		JsonHelper.ENTITY.set(valueRoot, MEASURE_UNIT_TOKEN, value.getMeasureUnit(), getEntityConverter());
	}
}
