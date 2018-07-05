package ru.argustelecom.box.env.type.model.properties;

import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.measure.model.MeasuredIntervalValue;
import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;

@Entity
@Access(AccessType.FIELD)
public class MeasuredIntervalProperty extends AbstractMeasuredProperty<MeasuredIntervalValue> {

	private static final long serialVersionUID = 82835272109848631L;

	protected static final String START_STORED_VALUE_TOKEN = "startStoredValue";
	protected static final String END_STORED_VALUE_TOKEN = "endStoredValue";

	//@formatter:off
	@Embedded
	@AssociationOverride(name = "storedUnit", joinColumns = { @JoinColumn(name = "measure_default_unit_id") })
	@AttributeOverrides({
		@AttributeOverride(name = "startStoredValue", column = @Column(name = "measure_default_start")),
		@AttributeOverride(name = "endStoredValue", column = @Column(name = "measure_default_end"))
	})
	//@formatter:on
	private MeasuredIntervalValue defaultValue;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected MeasuredIntervalProperty() {
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
	protected MeasuredIntervalProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	@Override
	protected void escapeDefaultValue() {
		defaultValue = null;
	}

	@Override
	public Class<?> getValueClass() {
		return MeasuredIntervalValue.class;
	}

	@Override
	public MeasuredIntervalValue getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(MeasuredIntervalValue defaultValue) {
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
	protected MeasuredIntervalValue extractValue(TypeInstance<?> context, ObjectNode propertiesRoot,
			String qualifiedName) {

		JsonNode valueNode = propertiesRoot.get(qualifiedName);
		if (valueNode == null || valueNode.isNull()) {
			return null;
		}

		checkState(valueNode instanceof ObjectNode);
		ObjectNode valueRoot = (ObjectNode) valueNode;

		Long startStoredValue = JsonHelper.LONG.get(valueRoot, START_STORED_VALUE_TOKEN);
		Long endStoredValue = JsonHelper.LONG.get(valueRoot, END_STORED_VALUE_TOKEN);
		MeasureUnit unit = JsonHelper.ENTITY.get(valueRoot, MEASURE_UNIT_TOKEN, MeasureUnit.class, getEntityConverter());

		return new MeasuredIntervalValue(startStoredValue, endStoredValue, unit);
	}

	@Override
	protected String extractValueAsString(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		MeasuredIntervalValue value = extractValue(context, propertiesRoot, qualifiedName);
		return value != null ? value.toString() : null;
	}

	@Override
	protected void putValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName,
			MeasuredIntervalValue value) {

		checkState(value != null);
		checkValue(value);

		ObjectNode valueRoot = propertiesRoot.putObject(qualifiedName);
		JsonHelper.LONG.set(valueRoot, START_STORED_VALUE_TOKEN, value.getStartStoredValue());
		JsonHelper.LONG.set(valueRoot, END_STORED_VALUE_TOKEN, value.getEndStoredValue());
		JsonHelper.ENTITY.set(valueRoot, MEASURE_UNIT_TOKEN, value.getMeasureUnit(), getEntityConverter());
	}
}
