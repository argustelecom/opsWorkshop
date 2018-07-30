package ru.argustelecom.box.env.type.model.properties;

import static com.google.common.base.Preconditions.checkState;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.env.datetime.model.DateIntervalValue;
import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;

@Entity
@Access(AccessType.FIELD)
public class DateIntervalProperty extends AbstractDateProperty<DateIntervalValue> {

	private static final long serialVersionUID = -552551635464492794L;

	protected static final String START_DATE_TOKEN = "startDate";
	protected static final String END_DATE_TOKEN = "endDate";

	//@formatter:off
	@Embedded
	@AttributeOverrides({ 
		@AttributeOverride(name = START_DATE_TOKEN, column = @Column(name = "date_default_start") ),
		@AttributeOverride(name = END_DATE_TOKEN, column = @Column(name = "date_default_end") ) 
	})//@formatter:on
	private DateIntervalValue defaultValue;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected DateIntervalProperty() {
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
	protected DateIntervalProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	@Override
	public Class<?> getValueClass() {
		return DateIntervalValue.class;
	}

	@Override
	public DateIntervalValue getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(DateIntervalValue defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String getDefaultValueAsString() {
		return formatIntervalValueWithPattern(defaultValue);
	}

	@Override
	protected DateIntervalValue extractValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		JsonNode valueNode = propertiesRoot.get(qualifiedName);
		if (valueNode == null || valueNode.isNull()) {
			return null;
		}

		checkState(valueNode instanceof ObjectNode);
		Date startDate = JsonHelper.DATE.get((ObjectNode) valueNode, START_DATE_TOKEN);
		Date endDate = JsonHelper.DATE.get((ObjectNode) valueNode, END_DATE_TOKEN);

		return new DateIntervalValue(startDate, endDate);
	}

	@Override
	protected String extractValueAsString(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		return formatIntervalValueWithPattern(extractValue(context, propertiesRoot, qualifiedName));
	}

	@Override
	protected void putValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName,
			DateIntervalValue value) {
		checkState(value != null);
		ObjectNode valueNode = propertiesRoot.putObject(qualifiedName);
		JsonHelper.DATE.set(valueNode, START_DATE_TOKEN, value.getStartDate());
		JsonHelper.DATE.set(valueNode, END_DATE_TOKEN, value.getEndDate());
	}
}
