package ru.argustelecom.box.env.type.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryAbstractFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@MappedSuperclass
@Access(AccessType.FIELD)
@TypeDef(name = "jsonb", defaultForType = com.fasterxml.jackson.databind.JsonNode.class, typeClass = ru.argustelecom.box.inf.hibernate.types.JsonbType.class)
public class TypeInstance<T extends Type> extends BusinessObject {

	private static final long serialVersionUID = 1912832975020029698L;

	@Transient
	private T type;

	// Персистентный атрибут не может называться properties, т.к. это ключевое слово в HQL. Использование атрибута
	// с таким именем приводило к ошибке парсинга HQL
	// Ошибка долгое время была спрятана, так как мы ни разу не пытались наложить фильтр на этот атрибут или каким-либо
	// другим способом задействовать в HQL или CriteriaAPI
	@Column(name = "properties")
	@org.hibernate.annotations.Type(type = "jsonb")
	private JsonNode props = JsonNodeFactory.instance.objectNode();

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected TypeInstance() {
	}

	/**
	 * Конструктор предназначен для инстанцирования спецификацией. Не делай этот конструктор публичным. Не делай других
	 * публичных конструкторов. Экземпляры спецификаций должны инстанцироваться сугубо спецификацией для обеспечения
	 * корректной инициализации пользовательских свойств или отношений между спецификацией и ее экземпляром.
	 * 
	 * @param id
	 *            - идентификатор экземпляра спецификации, должен быть получен при помощи соответствующего генератора
	 *            через сервис IdSequence
	 */
	protected TypeInstance(Long id) {
		super(id);
	}

	public T getType() {
		return EntityManagerUtils.initializeAndUnproxy(type);
	}

	protected void setType(T type) {
		this.type = type;
	}

	protected JsonNode getProperties() {
		return props;
	}

	protected void setProperties(JsonNode props) {
		checkArgument(props instanceof ObjectNode);
		this.props = props;
	}

	public <V, P extends TypeProperty<V>> V getPropertyValue(P property) {
		if (getType().hasProperty(property)) {
			return property.getValue(this);
		}
		return null;
	}

	public Object getPropertyValue(String propertyKeyword) {
		TypeProperty<?> property = getType().getProperty(propertyKeyword);
		if (property != null) {
			return property.getValue(this);
		}
		return null;
	}

	public <V, P extends TypeProperty<V>> void setPropertyValue(P property, V value) {
		if (getType().hasProperty(property)) {
			property.setValue(this, value);
		}
	}

	public void setPropertyValue(String propertyKeyword, Object value) {
		TypeProperty<?> property = getType().getProperty(propertyKeyword);
		if (property != null) {
			if (value == null) {
				property.setValue(this, null);
			} else {
				usafeSetPropertyValue(property, value);
			}
		}
	}

	protected <V, P extends TypeProperty<V>> void usafeSetPropertyValue(P property, Object value) {
		Class<?> valueClass = property.getValueClass();
		if (!valueClass.isAssignableFrom(value.getClass())) {
			throw new SystemException(MessageFormat.format("Incompatible types. Passed {0}, expected {1}",
					value.getClass().getName(), valueClass.getName()));
		}

		@SuppressWarnings("unchecked")
		V typedValue = (V) valueClass.cast(value);
		property.setValue(this, typedValue);
	}

	public Map<String, String> getPropertyValueMap() {
		Map<String, String> propertyValueMap = new HashMap<>();
		getType().getProperties().forEach(prop -> propertyValueMap.put(prop.getKeyword(), prop.getAsString(this)));
		return propertyValueMap;
	}

	public abstract static class TypeInstanceQuery<T extends Type, I extends TypeInstance<T>> extends EntityQuery<I> {

		private EntityQueryEntityFilter<I, ? super T> type;
		private EntityQueryPropertiesFilter<I> properties;

		public TypeInstanceQuery(Class<I> entityClass) {
			super(entityClass);
			type = createTypeFilter();
			properties = createPropertiesFilter();
		}

		protected abstract EntityQueryEntityFilter<I, ? super T> createTypeFilter();

		EntityQueryPropertiesFilter<I> createPropertiesFilter() {
			return new EntityQueryPropertiesFilter<I>(this, TypeInstance_.props);
		}

		public EntityQueryEntityFilter<I, ? super T> type() {
			return type;
		}

		/**
		 * Фильтр по произвольным свойствам пользовательских типов
		 * 
		 * @return фильтр по пользовательским свойствам
		 */
		public EntityQueryPropertiesFilter<I> properties() {
			return properties;
		}
	}

	public static class EntityQueryPropertiesFilter<E extends Identifiable>
			extends EntityQueryAbstractFilter<E, JsonNode> {

		public EntityQueryPropertiesFilter(EntityQuery<E> masterQuery,
				SingularAttribute<? super E, JsonNode> attribModel) {
			super(masterQuery, attribModel);
		}

		public EntityQueryPropertiesFilter(EntityQuery<E> masterQuery, Path<JsonNode> attribPath,
				SingularAttribute<?, JsonNode> attribModel) {
			super(masterQuery, attribPath, attribModel);
		}

		/**
		 * Создает предикат, проверяющий соответствие пользовательских характеристик текущего экземпляра типа указанному
		 * jsquery выражению. Если выражение отсутствет, то в результате метод вернет null.
		 * 
		 * @param propertyQuery
		 *            выражение jsquery, позволяющее выполнить индексированный поиск по пользовательским атрибутам
		 * 
		 * @return сформированный предикат или Null, если выражение jsquery не валидно
		 */
		public Predicate satisfied(TypePropertyQuery propertyQuery) {
			if (propertyQuery == null) {
				return null;
			}

			String jsonQuery = propertyQuery.render();
			if (isNullOrEmpty(jsonQuery)) {
				return null;
			}

			ParameterExpression<?> param = masterQuery().createParam(String.class, jsonQuery);
			return criteriaBuilder().equal(criteriaBuilder().function("jsq", Boolean.class, attribPath(), param), true);
		}

		/**
		 * @deprecated Не поддерживается в <code>EntityQueryPropertiesFilter</code>, используй метод
		 *             {@link #satisfied(TypePropertyQuery)}
		 */
		@Override
		@Deprecated
		public <X extends JsonNode> Predicate equal(X value) {
			throw new UnsupportedOperationException("Operation 'equal' is not supported by PropertiesFilter");
		}

		/**
		 * @deprecated Не поддерживается в <code>EntityQueryPropertiesFilter</code>, используй метод *
		 *             {@link #satisfied(TypePropertyQuery)}
		 */
		@Override
		@Deprecated
		public <X extends JsonNode> Predicate notEqual(X value) {
			throw new UnsupportedOperationException("Operation 'notEqual' is not supported by PropertiesFilter");
		}
	}
}
