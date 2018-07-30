package ru.argustelecom.box.env.type.model.properties;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import java.util.Locale;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.env.type.nls.TypeMessagesBundle;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@Entity
@Access(AccessType.FIELD)
public class DoubleProperty extends AbstractNumericProperty<Double> {

	private static final long serialVersionUID = 8960766831115743098L;

	@Getter
	@Setter
	@Column(name = "double_precision", nullable = false)
	private int precision;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected DoubleProperty() {
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
	protected DoubleProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	@Override
	public Class<?> getValueClass() {
		return Double.class;
	}


	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "double_min_value")
	public Double getMinValue() {
		return super.getMinValue();
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "double_max_value")
	public Double getMaxValue() {
		return super.getMaxValue();
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "double_default")
	public Double getDefaultValue() {
		return super.getDefaultValue();
	}

	@Override
	public String formatWithPrecision(Double value) {
		return precision == 0 ? Integer.toString(value.intValue()) : format(Locale.US, "%." + precision + "f", value);
	}

	@Override
	protected boolean validateRange(Double value) {
		boolean greaterThanMinimum = getMinValue() == null || Double.compare(value, getMinValue()) >= 0;
		boolean lessThanMaximum = getMaxValue() == null || Double.compare(value, getMaxValue()) <= 0;

		return greaterThanMinimum && lessThanMaximum;
	}

	@Override
	protected Double extractValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		return JsonHelper.DOUBLE.get(propertiesRoot, qualifiedName);
	}

	@Override
	protected String extractValueAsString(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		Double value = extractValue(context, propertiesRoot, qualifiedName);
		return value != null ? formatWithPrecision(value) : null;
	}

	@Override
	protected void putValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName, Double value) {
		checkState(value != null);
		checkValue(value);
		JsonHelper.DOUBLE.set(propertiesRoot, qualifiedName, value);
	}
}
