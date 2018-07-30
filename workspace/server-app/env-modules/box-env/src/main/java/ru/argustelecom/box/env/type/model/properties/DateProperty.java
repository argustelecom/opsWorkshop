package ru.argustelecom.box.env.type.model.properties;

import static com.google.common.base.Preconditions.checkState;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;

@Entity
@Access(AccessType.FIELD)
public class DateProperty extends AbstractDateProperty<Date> {

	private static final long serialVersionUID = 2569206978631748323L;

	@Column(name = "date_default")
	@Temporal(TemporalType.TIMESTAMP)
	private Date defaultValue;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected DateProperty() {
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
	protected DateProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	@Override
	public Class<?> getValueClass() {
		return Date.class;
	}

	@Override
	public Date getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(Date defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String getDefaultValueAsString() {
		return formatDateValueWithPattern(defaultValue);
	}

	@Override
	protected Date extractValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		return JsonHelper.DATE.get(propertiesRoot, qualifiedName);
	}

	@Override
	protected String extractValueAsString(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		return formatDateValueWithPattern(extractValue(context, propertiesRoot, qualifiedName));
	}

	@Override
	protected void putValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName, Date value) {
		checkState(value != null);
		JsonHelper.DATE.set(propertiesRoot, qualifiedName, value);
	}
}
