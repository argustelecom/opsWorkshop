package ru.argustelecom.box.env.type.model.properties;

import static com.google.common.base.Preconditions.checkState;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;

@Entity
@Access(AccessType.FIELD)
public class LogicalProperty extends TypeProperty<Boolean> {

	private static final long serialVersionUID = 1351805227716334856L;

	@Column(name = "bool_default", nullable = false)
	private Boolean defaultValue;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected LogicalProperty() {
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
	protected LogicalProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	@Override
	public Class<?> getValueClass() {
		return Boolean.class;
	}

	@Override
	public Boolean getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(Boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String getDefaultValueAsString() {
		return defaultValue != null ? defaultValue.toString() : null;
	}

	@Override
	protected Boolean extractValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		return JsonHelper.BOOLEAN.get(propertiesRoot, qualifiedName, Boolean.FALSE);
	}

	@Override
	protected String extractValueAsString(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		Boolean value = extractValue(context, propertiesRoot, qualifiedName);
		return value != null ? value.toString() : null;
	}

	@Override
	protected void putValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName, Boolean value) {
		checkState(value != null);
		JsonHelper.BOOLEAN.set(propertiesRoot, qualifiedName, value);
	}

}
