package ru.argustelecom.box.env.type.model.properties;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.findList;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.env.stl.NamedObjectArrayList;
import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.env.type.model.lookup.LookupCategory;
import ru.argustelecom.box.env.type.model.lookup.LookupEntry;
import ru.argustelecom.box.inf.hibernate.types.BigintArrayType;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;
import ru.argustelecom.system.inf.validation.ValidationResult;

@Entity
@Access(AccessType.FIELD)
@TypeDef(name = "bigint-array", typeClass = BigintArrayType.class)
public class LookupArrayProperty extends AbstractLookupProperty<List<LookupEntry>> {

	private static final long serialVersionUID = 5198837540825591265L;

	@Type(type = "bigint-array")
	@Column(name = "lkparr_default", nullable = false, columnDefinition = "bigint[]")
	private List<Long> defaultValue = new ArrayList<>();

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected LookupArrayProperty() {
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
	protected LookupArrayProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	@Override
	public boolean isFiltered() {
		// Временно, пока нет фильтра для массива
		return false;
	}

	@Override
	public Class<?> getValueClass() {
		return List.class;
	}

	@Override
	public List<LookupEntry> getDefaultValue() {
		return findList(ServerRuntimeProperties.instance().lookupEntityManager(), LookupEntry.class, defaultValue);
	}

	@Override
	public void setDefaultValue(List<LookupEntry> defaultValue) {
		this.defaultValue.clear();
		if (defaultValue != null) {
			defaultValue.stream().map(LookupEntry::getId).collect(toCollection(() -> this.defaultValue));
		}
	}

	@Override
	public String getDefaultValueAsString() {
		return arrayToString(getDefaultValue());
	}

	@Override
	public ValidationResult<TypeProperty<List<LookupEntry>>> validateValue(List<LookupEntry> value) {
		ValidationResult<TypeProperty<List<LookupEntry>>> result = ValidationResult.success();
		for (LookupEntry item : value) {
			ValidationResult<TypeProperty<List<LookupEntry>>> localResult = validateEntry(item);
			if (!localResult.isSuccess()) {
				result.add(localResult);
			}
		}
		return result;
	}

	@Override
	protected void checkDefaultCategoryAndClearWhenConflicts(LookupCategory newCategory) {
		boolean inconsistent = getDefaultValue().stream().anyMatch(e -> !isSameCategory(newCategory, e));
		if (inconsistent) {
			defaultValue.clear();
		}
	}

	@Override
	protected List<LookupEntry> extractValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		EntityConverter ec = getEntityConverter();
		List<LookupEntry> value = JsonHelper.ENTITY_ARRAY.get(propertiesRoot, qualifiedName, LookupEntry.class, ec);
		return value.stream().filter(e -> isSameCategory(getCategory(), e))
				.collect(toCollection(NamedObjectArrayList::new));
	}

	@Override
	protected String extractValueAsString(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		return arrayToString(extractValue(context, propertiesRoot, qualifiedName));
	}

	@Override
	protected void putValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName,
			List<LookupEntry> value) {
		checkState(value != null);
		JsonHelper.ENTITY_ARRAY.set(propertiesRoot, qualifiedName, value, getEntityConverter());
	}

	@Override
	protected void putNullValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		propertiesRoot.putArray(qualifiedName);
	}

	private String arrayToString(List<LookupEntry> values) {
		return values != null && !values.isEmpty()
				? values.stream().map(LookupEntry::getObjectName).collect(joining(", "))
				: null;
	}
}
