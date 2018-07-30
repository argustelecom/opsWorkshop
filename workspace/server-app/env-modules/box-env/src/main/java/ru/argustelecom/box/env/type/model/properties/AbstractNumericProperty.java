package ru.argustelecom.box.env.type.model.properties;

import static com.google.common.base.Preconditions.checkState;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.env.type.nls.TypeMessagesBundle;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@Entity
@Access(AccessType.FIELD)
public abstract class AbstractNumericProperty<V extends Number> extends TypeProperty<V> {

	private static final long serialVersionUID = 6291385373787233439L;

	@Transient
	private V defaultValue;

	@Getter
	@Setter
	@Transient
	private V minValue;

	@Getter
	@Setter
	@Transient
	private V maxValue;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected AbstractNumericProperty() {
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
	protected AbstractNumericProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	@Override
	public V getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(V defaultValue) {
		if (defaultValue != null) {
			this.defaultValue = checkValue(defaultValue);
		}
	}

	@Override
	public String getDefaultValueAsString() {
		return defaultValue != null ? formatWithPrecision(defaultValue) : null;
	}

	public String formatWithPrecision(V value) {
		return value.toString();
	}

	@Override
	public ValidationResult<TypeProperty<V>> validateValue(V value) {
		ValidationResult<TypeProperty<V>> result = ValidationResult.success();
		if (value != null && !validateRange(value)) {
			result.error(this, explainRange());
		}
		return result;
	}

	protected abstract boolean validateRange(V value);

	private String explainRange() {
		TypeMessagesBundle messages = LocaleUtils.getMessages(TypeMessagesBundle.class);
		if (minValue != null && maxValue != null) {
			return messages.numericValueMinMaxRangeIssue(formatWithPrecision(minValue), formatWithPrecision(maxValue));
		} else if (minValue != null) {
			return messages.numericValueMinRangeIssue(formatWithPrecision(minValue));
		} else {
			checkState(maxValue != null);
			return messages.numericValueMaxRangeIssue(formatWithPrecision(maxValue));
		}
	}
}
