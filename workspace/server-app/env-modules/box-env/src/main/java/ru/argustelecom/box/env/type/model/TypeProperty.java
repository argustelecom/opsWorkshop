package ru.argustelecom.box.env.type.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.stl.json.JsonAccessor;
import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.type.model.properties.DoubleProperty;
import ru.argustelecom.box.env.type.model.properties.LongProperty;
import ru.argustelecom.box.env.type.model.properties.TextProperty;
import ru.argustelecom.box.env.type.nls.TypeMessagesBundle;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;
import ru.argustelecom.system.inf.validation.ValidationResult;

@Entity
@Access(AccessType.FIELD)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(schema = "system", name = "type_property", uniqueConstraints = {
		@UniqueConstraint(name = "uc_type_property_keyword", columnNames = { "holder_id", "keyword" }) })
public abstract class TypeProperty<V> extends MetadataUnit<Long> implements Identifiable, Ordinal {

	private static final long serialVersionUID = 1650896079026743873L;

	protected static final String OPTIONS_TOKEN = "options";
	protected static final String OPT_LOCKED_VALUE_TOKEN = "locked";
	private static final Integer INITIAL_ORDINAL_NUMBER = 1;
	public static final Set<Class<? extends TypeProperty>> SUPPORT_UNIQUE_PROPERTY_CLASSES;

	static {
		Set<Class<? extends TypeProperty>> validPropertyClasses = new HashSet<>();
		validPropertyClasses.add(DoubleProperty.class);
		validPropertyClasses.add(LongProperty.class);
		validPropertyClasses.add(TextProperty.class);
		SUPPORT_UNIQUE_PROPERTY_CLASSES = unmodifiableSet(validPropertyClasses);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "holder_id", updatable = false, nullable = false)
	private TypePropertyHolder holder;

	/**
	 * Группа, которой пренадлежит характеристика
	 */
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private TypePropertyGroup group;

	/**
	 * Порядковый номер в группе
	 */
	@Getter
	@Setter
	@Column(nullable = false)
	private Integer ordinalNumber;

	@Column(length = 256)
	private String hint;

	@Column(nullable = false)
	private boolean required;

	@Column(nullable = false)
	private boolean readonly;

	@Column(nullable = false)
	private boolean secured;

	@Column(nullable = false)
	private boolean indexed;

	@Column(nullable = false)
	private boolean filtered;

	@Column(nullable = false)
	private boolean statical;

	@Column(nullable = false)
	private boolean sys;

	@Column(name = "uniq", nullable = false)
	private boolean unique;

	@Transient
	private EntityConverter entityConverter;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected TypeProperty() {
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
	protected TypeProperty(TypePropertyHolder holder, Long id) {
		super(id);
		this.holder = checkNotNull(holder);
	}

	@Id
	@Override
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return super.getId();
	}

	/**
	 * FIXME Возвращает спецификацию, для которой определено текущее свойство.
	 * 
	 * @return {@link Type}, всегда не null
	 */
	protected TypePropertyHolder getHolder() {
		return holder;
	}

	public TypePropertyRef getType() {
		return TypePropertyRef.forClass(getClass());
	}

	/**
	 * Возвращает подсказку текущего свойства. Подсказка указывается при конфигурировании свойства и предназначена для
	 * пояснения пользователю о назначении текущего свойства, об ожидаемых значениях и других особенностях, на которых
	 * администратор решит акцентировать внимание пользователя, работающего с текущим свойством
	 * 
	 * @return строковая подсказа текущего свойства
	 */
	public String getHint() {
		return hint;
	}

	/**
	 * Устанавливает новое значение подсказки текущего свойства.
	 * 
	 * @param hint
	 *            - новое значение подсказки текущего свойства
	 */
	public void setHint(String hint) {
		this.hint = hint;
	}

	/**
	 * Возвращает флаг "обязательности" текущего свойства. Если отмечен данный флаг, то пользователь обязан указать
	 * значение этого свойства при создании нового объекта спецификации. Также при смене спецификации для экземпляра,
	 * обязательные свойства должны быть "довведены" (является частью функциональности по смене спецификации).
	 * 
	 * @return true если текущее свойство обязательно для заполнения
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Устанавливает новое значение обязательности текущего свойства
	 * 
	 * @param required
	 *            - новое значение флага
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Возвращает флаг, сигнализирующий о том, что пользователь не может менять значение текущего свойства. Введено для
	 * обеспечения совместимости с ValueEngine
	 * 
	 * @return true если пользователь не может менять значение текущего свойства
	 */
	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * Устанавливает новое значение флага, запрещающего редактирование свойства
	 * 
	 * @param readonly
	 *            - новое значение флага
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	/**
	 * Возвращает флаг, сигнализирующий, что текущее свойство защищено для просмотра для всех пользователей, которые не
	 * обладают определенной привилегией. Предназначено для возможности моделирования таких данных, как номера
	 * удостоверений личности, персональные данные, номера счетов и т.д. Если флаг установлен, то пользователь в UI
	 * должен видеть только часть значения этого свойства, например "номер паспорта: **** 0890". Значения защищенных
	 * свойств на уровне хранилища (БД) должны быть представлены в зашифрованном виде.
	 * 
	 * @return true если текущее свойство защищено
	 */
	public boolean isSecured() {
		return secured;
	}

	/**
	 * Устанавливает новое значение защищенности текущего свойства
	 * 
	 * @param secured
	 *            - новое значение флага
	 */
	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	/**
	 * Возвращает флаг, сигнализирующий, что текущее свойство должно быть проиндексировано для использования в
	 * полнотекстовом поиске (Search Everywhere, частные поиски) с наименьшим приоритетом ранжирования. На это свойство
	 * должен опираться генератор триггера по обновлению индекса полнотекстового поиска
	 * 
	 * @return true, если свойство помечено как индексируемое
	 */
	public boolean isIndexed() {
		return indexed;
	}

	/**
	 * Устанавливает новое значение флага индексирования в полнотекстовом поиске
	 * 
	 * @param indexed
	 *            - новое значение флага
	 */
	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	/**
	 * Возвращает флаг, сигнализирующий о том, что по текущему свойству можно фильтровать значения экземпляров
	 * спецификаций в списках и других специализированных инструментах. Технически, весь документ jsonb, хранящий
	 * значения свойств, проиндексирован, однако не каждое свойство целесообразно использовать для фильтрации.
	 * <p>
	 * ВАЖНО!!! На текущий момент, используя стандартные механизмы и операторы PostgreSQL, можно делать быстрые фильтры
	 * только на вхождение точного значения. Не поддерживается индексированный поиск по интервалам значений (значение
	 * попадает в интервал от .. до), поиск с использованием операторов сравнений (=, >, < ...). Для устранения этого
	 * дефекта нужно мигрировать на сборки БД PostgresPro от отечественных разработчиков. PostgresPro из коробки
	 * поддерживает расширение jsquery, реализующее недостающие операторы и существенно упрощающее синтаксис поиска в
	 * документе jsonb
	 * 
	 * @return true, если свойство можно использовать в фильтрах
	 */
	public boolean isFiltered() {
		return filtered;
	}

	/**
	 * Устанавливает новое значение флага доступности для фильтрации в инструментах
	 * 
	 * @param filtered
	 *            - новое значение флага
	 */
	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	/**
	 * Возвращает флаг, сигнализирующий, что текущее свойство "статическое", т.е. характерно для всей спецификации в
	 * целом, но не для ее отдельных экземпляров. Значение статического свойства берется из поля defaultValue. При
	 * попытке присвоения свойства на уровне экземпляра должно бросаться соответствующее исключение
	 * 
	 * @return true, если текущее свойствой является статическим
	 */
	public boolean isStatical() {
		return statical;
	}

	/**
	 * Устанавливает новое значение флага "стаическое свойство"
	 * 
	 * @param statical
	 *            - новое значение флага
	 */
	public void setStatical(boolean statical) {
		this.statical = statical;
	}

	/**
	 * Возвращает флаг системности текущего свойство. Системные свойства определены на момент разработки или
	 * конфигурирования поставки системы и не могут быть изменены или удалены пользователями. Флаг должен быть доступен
	 * только в специализированных инструментах по конфигурированию системы, возможно, в специализированном языке
	 * описания метаданных
	 * 
	 * @return true, если свойство системное
	 */
	public boolean isSys() {
		return sys;
	}

	/**
	 * Устанавливает новое значение флага системности.
	 * 
	 * @param sys
	 *            - новое значение флага
	 */
	public void setSys(boolean sys) {
		this.sys = sys;
	}

	/**
	 * Возвращает true, если текущее свойство может быть удалено. Удаление здесь весьма условная операция, т.к. под
	 * удалением понимается деактивация, а не физическое удаление. Деактивированный объект по прежнему участвует в
	 * уникальности, таким образом, невозможно создать новое свойство с таким же кейвордом, но возможно восстановить
	 * старое свойство, деактивированное ранее.
	 * 
	 * @return true, если текущее свойство может быть "удалено"
	 */
	public boolean isDeletable() {
		return !isSys() && getStatus() == MetadataUnitStatus.ACTIVE;
	}

	/**
	 * Возвращает полное квалифицированное имя свойства. Для определения квалифицированного имени свойства используется
	 * следущий паттерн "[PropertyClass]-[PropertyId]". По квалифицированному имени свойства можно полностью
	 * восстановить как холдера, так и само свойство
	 * 
	 * @return строку, представляющую полное квалифицированное имя свойства
	 */
	public String getQualifiedName() {
		return getEntityConverter().convertToString(this);
	}

	/**
	 * Восстанавливает экземпляр свойства по его квалифицированному имени. Если в качестве квалифицированного имени
	 * указана бессмысленная белиберда, но в правильном формате, то пользователь данного метода рискует получить
	 * отложенное исключение EntityNotFoundException, обусловленное использованием метода
	 * {@link EntityManager#getReference(Class, Object)}
	 * 
	 * @param em
	 *            - EntityManager, в контексте которого требуется произвести восстановление. Если не указан, то будет
	 *            использован EntityManager, полученный программным инжектом
	 * @param qualifiedName
	 *            - квалифицированное имя восстанавливаемого свойства
	 * 
	 * @return JPA Proxy восстановленного свойства или само свойство, если оно нашлось в контексте персистенции
	 */
	public static TypeProperty<?> fromQualifiedName(EntityManager em, String qualifiedName) {
		return new EntityConverter(em).convertToObject(TypeProperty.class, qualifiedName);
	}

	/**
	 * Возвращает тип занчения для текущего свойства. Определяется в потомках
	 * 
	 * @return тип значения свойства
	 */
	public abstract Class<?> getValueClass();

	/**
	 * Проверяет, установлено ли для свойства значение по умолчанию
	 * 
	 * @return true, если значение по умолчанию установлено
	 */
	public boolean hasDefaultValue() {
		return getDefaultValue() != null;
	}

	/**
	 * Возвращает значение по-умолчанию для текущего свойства. Указывается при конфигурировании свойства. При создании
	 * нового экземпляра спецификации дефолтное значение записывается в документ, после этого значение по-умолчанию
	 * никак не контролируется в документе и может быть изменено: установлено в другое значение, сброшено в null и т.д.
	 * 
	 * @return значение по-умолчанию
	 */
	public abstract V getDefaultValue();

	/**
	 * Устанавливает значение по-умолчанию для текущего свойства
	 * 
	 * @param defaultValue
	 *            - значение по умолчанию
	 */
	public abstract void setDefaultValue(V defaultValue);

	/**
	 * Возвращает строковое представление значения по-умолчанию. Для некоторых типов свойств его корректное строковое
	 * представление может отличаться от простого toString(), поэтому способ получения этого представления делегирован
	 * потомкам
	 * 
	 * @return строковое представление значения по умолчанию
	 */
	public abstract String getDefaultValueAsString();

	/**
	 * Проверяет, является ли значение текущего свойства в указанном экземпляре зачением по умолчанию
	 * 
	 * @param instance
	 *            - экземпляр, для которого выполняется проверка
	 * 
	 * @return true, если значение текущего свойства в указанном экземпляре является значением по умолчанию
	 */
	public boolean isValueDefault(TypeInstance<?> instance) {
		if (!hasDefaultValue()) {
			return false;
		}

		V currentValue = getValue(instance);
		return Objects.equals(currentValue, getDefaultValue());
	}

	/**
	 * Проверяет, имеется ли значение текущее свойство на уровне указанного экземпляра
	 * 
	 * @param instance
	 *            - экземпляр, для которого выполняется проверка
	 * 
	 * @return true, если значение указано
	 */
	public boolean isValuePresent(TypeInstance<?> instance) {
		return getValue(instance) != null;
	}

	/**
	 * Определяет, изменяется ли значение текущего свойства в указанном экземпляре типа
	 * 
	 * @param instance
	 *            - экземпляр типа, в котором потенциально может поменяться значение текущего свойства
	 * @param newValue
	 *            - предполагаемое новое значение текущего свойства для экземпляра типа
	 * 
	 * @return true, если предполагаемое значение этого свойства отличается от текущего в указанном экземпляре
	 */
	public boolean isValueChanges(TypeInstance<?> instance, V newValue) {
		V currentValue = getValue(instance);
		return !Objects.equals(currentValue, newValue);
	}

	/**
	 * Проверяет, является ли уникальным значение данного свойства
	 *
	 * @return true, если значение свойства уникально
	 */
	public boolean isUnique() {
		return unique;
	}

	/**
	 * Устанавилвает новое значение флага "Уникальное значение свойства". Перед изменением флага проиходит проверка, что
	 * текущее свойство поддерживает уникальность. <br/>
	 * НЕ ВЫЗЫВАТЬ САМОСТОЯТЕЛЬНО!!! Изменение происходит через
	 * {@link ru.argustelecom.box.env.type.TypeFactory#makePropertyUnique(Class, TypeProperty)} для задания
	 * уникальности, или {@link ru.argustelecom.box.env.type.TypeFactory#unmakePropertyUnique(Class, TypeProperty)} для
	 * ее снятия.
	 *
	 * @param unique
	 *            задает уникальность свойства
	 */
	protected void setUnique(boolean unique) {
		checkState(SUPPORT_UNIQUE_PROPERTY_CLASSES.contains(getClass()), "Unsupported property");
		this.unique = unique;
	}

	/**
	 * Определяет, заблокировано ли возможность изменения значения текущего свойства в указанном экземпляре типа
	 * 
	 * @param instance
	 *            - экземпляр типа
	 * 
	 * @return true, если возможность изменения значения заблокирована
	 */
	public final boolean isValueLocked(TypeInstance<?> instance) {
		return getOption(instance, OPT_LOCKED_VALUE_TOKEN, false, JsonHelper.BOOLEAN);
	}

	/**
	 * Блокирует возможность изменять значение текущего свойства в указанном экземпляре типа
	 * 
	 * @param instance
	 *            - экземпляр типа
	 */
	public final void lockValue(TypeInstance<?> instance) {
		setOption(instance, OPT_LOCKED_VALUE_TOKEN, true, false, JsonHelper.BOOLEAN);
	}

	/**
	 * Разблокирует возможность изменять значение текущего свойства в указанном экземпляре типа
	 * 
	 * @param instance
	 *            - экземпляр типа
	 */
	public final void unlockValue(TypeInstance<?> instance) {
		setOption(instance, OPT_LOCKED_VALUE_TOKEN, false, false, JsonHelper.BOOLEAN);
	}

	/**
	 * Возвращает конвертер, используемый для сериализации и десериализации сущностей, входящих в состав значений
	 * свойства. Актуален для справочных и ссылочных типов свойст.
	 * 
	 * @return конвертер для сериализации/десериализации значения свойства
	 */
	protected EntityConverter getEntityConverter() {
		if (entityConverter == null) {
			entityConverter = new EntityConverter();
		}
		return entityConverter;
	}

	/**
	 * Извлекает значение текущего свойства из документа jsonb, определенного на уровне конкретного экземпляра
	 * спецификации, являющегося контекстом для текущего свойства.
	 * 
	 * @param context
	 *            - экземпляр спецификации, для которого нужно определить значение
	 * @param propertiesRoot
	 *            - корневой узел документа jsonb
	 * @param qualifiedName
	 *            - квалифицированное имя свойства
	 * 
	 * @return Значение свойства или null, если свойство не определено или его значение не задано в текущем контексте
	 */
	protected abstract V extractValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName);

	/**
	 * Извлекает строковое представление значения текущего свойства из документа jsonb, определенного на уровне
	 * конкретного экземпляра спецификации, являющегося контекстом для текущего свойства. Для некоторых типов свойств
	 * его корректное строковое представление может отличаться от простого toString(), поэтому способ получения этого
	 * представления делегирован потомкам.
	 * 
	 * @param context
	 *            - экземпляр спецификации, для которого нужно определить значение
	 * @param propertiesRoot
	 *            - корневой узел документа jsonb
	 * @param qualifiedName
	 *            - квалифицированное имя свойства
	 * 
	 * @return Строковое представление значения свойства или null, если свойство не определено или его значение не
	 *         задано в текущем контексте
	 */
	protected abstract String extractValueAsString(TypeInstance<?> context, ObjectNode propertiesRoot,
			String qualifiedName);

	/**
	 * Устанавливает пустое значение для текущего свойства. Метод должен корректно установить значение в документ jsonb
	 * 
	 * @param context
	 *            - экземпляр спецификации, для которого нужно определить значение
	 * @param propertiesRoot
	 *            - корневой узел документа jsonb
	 * @param qualifiedName
	 *            - квалифицированное имя свойства
	 */
	protected void putNullValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		propertiesRoot.putNull(qualifiedName);
	}

	/**
	 * Устанавливает указанное значение для текущего свойства. Метод должен корректно установить значение в документ
	 * jsonb. Значение должно быть корректно с точки зрения определенных для текущего свойства правил валидации.
	 * 
	 * @param context
	 *            - экземпляр спецификации, для которого нужно определить значение
	 * @param propertiesRoot
	 *            - корневой узел документа jsonb
	 * @param qualifiedName
	 *            - квалифицированное имя свойства
	 * @param value
	 *            - новое значение свойства. Всегда не null
	 */
	protected abstract void putValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName, V value);

	/**
	 * Копирует значение из одного экземпляра в другой. Небольшой шоткат для случаев использования wildcard в свойстве
	 * 
	 * @param fromInstance
	 *            - откуда нужно скопировать значение свойства
	 * @param toInstance
	 *            - куда нужно скопировать значение свойства
	 */
	protected void copyValue(TypeInstance<?> fromInstance, TypeInstance<?> toInstance) {
		V value = getValue(fromInstance);
		setValuePrivileged(toInstance, value);
	}

	/**
	 * Возвращает значение текущего свойства для указанного экземпляра спецификации
	 * 
	 * @param instance
	 *            - экземпляр спецификации
	 *
	 * @return значение свойства или null во всех остальных случаях
	 */
	public final V getValue(TypeInstance<?> instance) {
		if (!instance.getType().hasProperty(this)) {
			return null;
		}

		ObjectNode propertiesRoot = getPropertiesRoot(instance);

		V value = null;
		if (propertiesRoot != null) {
			value = extractValue(instance, propertiesRoot, getQualifiedName());
		}

		return value;
	}

	/**
	 * Устанавливает новое значение текущего свойства для указанного экземпляра типа. Перед установкой нового значения
	 * свойства выполняет проверку, можно ли это свойство менять в текущем экземпляре. Если менять свойство в текущем
	 * экземпляре запрещено, то будет брошено соответствующее исключение
	 * 
	 * @param instance
	 *            - экземпляр типа
	 * @param value
	 *            - новое значение свойства. Может быть null, если необходимо сбросить значение свойства.
	 */
	public final void setValue(TypeInstance<?> instance, V value) {
		if (instance.getType().hasProperty(this) && isValueChanges(instance, value)) {
			if (isValueLocked(instance)) {
				TypeMessagesBundle message = LocaleUtils.getMessages(TypeMessagesBundle.class);
				throw message.unableToChangeValueInLockedPropertyException(getName());
			}
			setValuePrivileged(instance, value);
		}
	}

	/**
	 * Устанавливает значение свойства в привилегированном режиме. При этом не проверяется заблокированность свойства
	 * 
	 * @param instance
	 *            - экземпляр типа
	 * @param value
	 *            - новое значение свойства. Может быть null, если необходимо сбросить значение свойства.
	 */
	protected final void setValuePrivileged(TypeInstance<?> instance, V value) {
		checkState(instance.getType().hasProperty(this));
		ObjectNode propertiesRoot = checkNotNull(getPropertiesRoot(instance));
		if (value == null) {
			putNullValue(instance, propertiesRoot, getQualifiedName());
		} else {
			putValue(instance, propertiesRoot, getQualifiedName(), value);
		}
	}

	/**
	 * Возвращает строковое представление текущего свойства для указанного экземпляра спецификации
	 * 
	 * @param instance
	 * 
	 * @return строковое представление или null во всех остальных случаях
	 */
	public final String getAsString(TypeInstance<?> instance) {
		if (!instance.getType().hasProperty(this)) {
			return null;
		}

		ObjectNode propertiesRoot = getPropertiesRoot(instance);

		String value = null;
		if (propertiesRoot != null) {
			value = extractValueAsString(instance, propertiesRoot, getQualifiedName());
		}

		return value != null ? value : null;
	}

	/**
	 * Устанавливает значения по-умолчанию текущего свойства для указанного экземпляра спецификации. Установка значения
	 * невозможна, если текущее свойство деактивировано, отмечено как статическое или поддерживает уникальность и
	 * является уникальным. Если значение по умолчанию не определено для текущего свойства, то в экземпляр спецификации
	 * для текущего свойства будет установлено значение null (для того, чтобы в документе всегда была установлена
	 * валидная структура свойств, определенная на уровне спецификации).
	 * 
	 * @param instance
	 *            - экземпляр спецификации, для которого устанавливается значение по-умолчанию
	 */
	public final void initDefaults(TypeInstance<?> instance) {
		boolean hasProperty = instance.getType().hasProperty(this);
		boolean isActive = getStatus() == MetadataUnitStatus.ACTIVE;
		boolean isPropertyUnique = SUPPORT_UNIQUE_PROPERTY_CLASSES.contains(getClass()) && isUnique();

		if (hasProperty && !isStatical() && isActive && !isPropertyUnique) {
			setValuePrivileged(instance, getDefaultValue());
		}
	}

	/**
	 * Выполняет валидирование указанного значения. Используется для определения потенциальной возможности присвоения
	 * текущему свойству указанного значения. Используется при установке значения свойства в контекст экземпляра
	 * спецификации или значения свойства по-умолчанию
	 * 
	 * @param value
	 *            - проверяемое значение
	 * 
	 * @return результат проверки указанного значения
	 */
	public ValidationResult<TypeProperty<V>> validateValue(V value) {
		return ValidationResult.success();
	}

	@Override
	protected Long checkId(Long id) {
		return checkNotNull(id);
	}

	/**
	 * Утилитный метод, проверяющий, есть ли группа у текущей характеристики
	 */
	public boolean hasGroup() {
		return getGroup() != null;
	}

	@Override
	public Integer initialOrdinalNumber() {
		return INITIAL_ORDINAL_NUMBER;
	}

	@Override
	public List<TypeProperty<?>> group() {
		return getGroup() != null ? getGroup().getProperties()
				: getHolder().getProperties().stream().filter(property -> !property.hasGroup())
						.collect(Collectors.toList());
	}

	/**
	 * Использует метод валидации для проверки указанного значения. Если значение не проходит валидацию, то будет
	 * брошено бизнес-исключение. Должно вызываться потомками каждый раз, когда устанавливается новое значение свойства
	 * или значение по-умолчанию.
	 * 
	 * @param value
	 *            - проверяемое значение
	 * @return
	 */
	protected V checkValue(V value) {
		if (value == null) {
			return null;
		}

		ValidationResult<TypeProperty<V>> result = validateValue(value);
		if (!result.isSuccess(true)) {
			throw new BusinessException(result.explain());
		}
		return value;
	}

	// *****************************************************************************************************************

	/**
	 * Извлекает значение опции с указанным именем из экземпляра типа. Если опция текущего свойства не задана на уровне
	 * экземпляра, то будет возвращено значение по умолчанию. Для извлечения свойства используется вспомогательный
	 * объект accessor
	 * 
	 * @param instance
	 *            - экземпляр типа, для которого необходимо получить значение опции
	 * @param optionName
	 *            - наименование опции
	 * @param optionDefault
	 *            - значение опции по умолчанию
	 * @param accessor
	 *            - утилитарный класс, инкапсулирующий низкоуровневую работу с Json
	 * 
	 * @return значение логической опции
	 */
	protected <O> O getOption(TypeInstance<?> instance, String optionName, O optionDefault, JsonAccessor<O> accessor) {
		if (instance.getType().hasProperty(this)) {
			ObjectNode propertyOptions = getPropertyOptions(instance, false);
			if (propertyOptions != null) {
				return accessor.get(propertyOptions, optionName, optionDefault);
			}
		}
		return optionDefault;
	}

	/**
	 * Устанавливает новое значение опции c указанным имененем для указанного экземпляра типа. Если значение опции
	 * отсутствовало или никогда не отличалось от значения опции по умолчанию и при этом снова пытаются установить
	 * значение по умолчанию, то {@link #getPropertyOptions(TypeInstance, boolean) ничего не будет сделано}, т.е. JPA
	 * контейнер не решит, что объект instance изменился и его необходимо обновить в БД.
	 * 
	 * @param instance
	 *            - экземпляр типа, для которого необходимо установить значение опции
	 * @param optionName
	 *            - наименование опции
	 * @param optionValue
	 *            - новое значение опции
	 * @param optionDefault
	 *            - значение опции по умолчанию
	 * @param accessor
	 *            - утилитарный класс, инкапсулирующий низкоуровневую работу с Json
	 */
	protected <O> void setOption(TypeInstance<?> instance, String optionName, O optionValue, O optionDefault,
			JsonAccessor<O> accessor) {

		if (instance.getType().hasProperty(this)) {
			// форсировать установку свойства имеет смысл только тогда, когда значение свойства отличается от значения
			// по умолчанию (см. коммент getPropertyOptions)
			boolean forced = !Objects.equals(optionDefault, optionValue);
			ObjectNode propertyOptions = getPropertyOptions(instance, forced);

			if (forced) {
				// если форсировали создание контейнера опций, то необходимо убедиться, что он есть
				checkState(propertyOptions != null);
				accessor.set(propertyOptions, optionName, optionValue);
			} else {
				// в нефорсированном режиме устанавливать свойство нужно только в том случае, если раньше его уже
				// устанавливали в значение, отличное от значения по умолчанию (см. коммент getPropertyOptions)
				if (propertyOptions != null && propertyOptions.has(optionName)) {
					accessor.set(propertyOptions, optionName, optionValue);
				}
			}
		}
	}

	// *****************************************************************************************************************

	/**
	 * Определяет корневой JsonObject, в котором хранятся свойства. Может вернуть null, который будет адекватно
	 * восприниматься только в том случае, если мы что-то пытаемся прочитать. Если же при попытке записи нового значения
	 * свойства этим методом будет возвращен null, то будет сгенерировано соответствующее исключение
	 * 
	 * @param instance
	 *            - экземпляр типа, из которого необходимо получить корневой JsonObject значений свойств
	 * 
	 * @return корневой JsonObject или null, если на уровне экземпляра нет такого объекта
	 */
	private ObjectNode getPropertiesRoot(TypeInstance<?> instance) {
		if (instance == null) {
			return null;
		}

		JsonNode propsNode = instance.getProperties();
		return propsNode instanceof ObjectNode ? (ObjectNode) propsNode : null;
	}

	/**
	 * Определяет JsonObject, в котором хранятся дополнительные опции текущего свойства. Метод поддерживает возможность
	 * создания JsonObject для доп.свойств при его отсутствии, однако этим режимом нужно пользоваться осторожно. Если мы
	 * хотим только прочитать какое-то свойство, то необходимо понимать, что вызов с включенным режимом создания
	 * приведет к изменению персистентного атрибута {@link TypeInstance#getProperties()}, что в свою очередь
	 * спровоцирует выполнение update в БД. Если это будет происходить при readonly транзакции, то все, очевидно,
	 * развалится. Таким образом можно выделить следующие рекомендации при работе с дополнительными опциями свойст:
	 * <ul>
	 * <li>Если необходимо только прочитать значение опции, то createIfNotFound = false. При отстутствии JsonObject для
	 * опций необходимо возвращать значение по умолчанию.
	 * <li>Если необходимо установить значение опции, отличное от значения по умолчанию, то createIfNotFound = true. Так
	 * гарантированно появится правильное значение.
	 * <li>Если необходимо сбросить значение в значение по умолчанию, то createIfNotFound = false. Если JsonObject не
	 * существует, то вследствие пункта 1 свойство уже находится в значении по умолчанию. В ином случае, JsonObject уже
	 * был создан ранее и для него можно будет явно сохранить значение по умолчанию. Такой подход позволит
	 * минимизировать количество запросов в БД при редактировании опций свойств.
	 * </ul>
	 * 
	 * @param instance
	 *            - экземпляр типа, из которого необходимо получить контейнер опций
	 * 
	 * @param createIfNotFound
	 *            - создавать контейнер опций для свойства в указанном экземпляре в случае, если этого контейнера раньше
	 *            не было создано
	 * 
	 * @return контейнер опций текущего свойства в экземпляре типа
	 */
	private ObjectNode getPropertyOptions(TypeInstance<?> instance, boolean createIfNotFound) {
		ObjectNode propertiesRoot = getPropertiesRoot(instance);

		ObjectNode optionsRoot = getChildNode(ObjectNode.class, propertiesRoot, OPTIONS_TOKEN);
		if (optionsRoot == null && createIfNotFound) {
			checkState(propertiesRoot != null);
			optionsRoot = propertiesRoot.putObject(OPTIONS_TOKEN);
		}

		ObjectNode propertyOptions = getChildNode(ObjectNode.class, optionsRoot, getQualifiedName());
		if (propertyOptions == null && createIfNotFound) {
			checkState(optionsRoot != null);
			propertyOptions = optionsRoot.putObject(getQualifiedName());
		}

		return propertyOptions;
	}

	private <N extends JsonNode> N getChildNode(Class<N> childNodeClass, ObjectNode parentNode, String childName) {
		if (parentNode == null || parentNode.isNull()) {
			return null;
		}

		JsonNode childNode = parentNode.get(childName);
		if (childNode == null || childNode.isNull()) {
			return null;
		}

		checkState(childNodeClass.isInstance(childNode));
		return childNodeClass.cast(childNode);
	}
}
