package ru.argustelecom.box.env.type.model.properties;

import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.env.type.model.lookup.LookupCategory;
import ru.argustelecom.box.env.type.model.lookup.LookupEntry;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.system.inf.validation.ValidationResult;

@Entity
@Access(AccessType.FIELD)
public class LookupProperty extends AbstractLookupProperty<LookupEntry> {

	private static final long serialVersionUID = 509878306236274786L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lkp_default_entry_id")
	private LookupEntry defaultValue;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected LookupProperty() {
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
	protected LookupProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	@Override
	protected void checkDefaultCategoryAndClearWhenConflicts(LookupCategory newCategory) {
		if (!isSameCategory(newCategory, defaultValue)) {
			defaultValue = null;
		}
	}

	@Override
	public Class<?> getValueClass() {
		return LookupEntry.class;
	}

	@Override
	public LookupEntry getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(LookupEntry defaultValue) {
		if (defaultValue == null) {
			this.defaultValue = null;
			return;
		}

		checkValue(defaultValue);
		if (!Objects.equals(this.defaultValue, defaultValue)) {
			if (getCategory() == null) {
				super.setCategory(defaultValue.getCategory(), false);
			}
			this.defaultValue = defaultValue;
		}
	}

	@Override
	public String getDefaultValueAsString() {
		return defaultValue != null ? defaultValue.getObjectName() : null;
	}

	@Override
	public ValidationResult<TypeProperty<LookupEntry>> validateValue(LookupEntry value) {
		return validateEntry(value);
	}

	@Override
	protected LookupEntry extractValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		LookupEntry value = JsonHelper.ENTITY.get(propertiesRoot, qualifiedName, LookupEntry.class,
				getEntityConverter());

		return !isSameCategory(getCategory(), value) ? null : value;
	}

	@Override
	protected String extractValueAsString(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		LookupEntry value = extractValue(context, propertiesRoot, qualifiedName);
		return value != null ? value.getObjectName() : null;
	}

	@Override
	protected void putValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName,
			LookupEntry value) {
		checkState(value != null);
		checkValue(value);

		JsonHelper.ENTITY.set(propertiesRoot, qualifiedName, value, getEntityConverter());
	}

	protected boolean isSameCategory(LookupCategory category, LookupEntry value) {
		return category == null || value == null || Objects.equals(category, value.getCategory());
	}
}
