package ru.argustelecom.box.env.type.model.properties;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import ru.argustelecom.box.env.measure.model.Measurable;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.env.type.nls.TypeMessagesBundle;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@Entity
@Access(AccessType.FIELD)
public abstract class AbstractMeasuredProperty<T extends Measurable> extends TypeProperty<T> {

	private static final long serialVersionUID = -7104446835664881447L;

	protected static final String MEASURE_UNIT_TOKEN = "measureUnit";

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "measure_unit_id")
	private MeasureUnit measureUnit;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected AbstractMeasuredProperty() {
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
	protected AbstractMeasuredProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	public MeasureUnit getMeasureUnit() {
		return EntityManagerUtils.initializeAndUnproxy(measureUnit);
	}

	public void setMeasureUnit(MeasureUnit measureUnit) {
		if (!Objects.equals(this.measureUnit, measureUnit)) {
			directsetMeasureUnit(measureUnit);
			if (!isSameMeasureGroup(this.measureUnit, getDefaultValue())) {
				escapeDefaultValue();
			}
		}
	}

	protected void directsetMeasureUnit(MeasureUnit measureUnit) {
		this.measureUnit = measureUnit;
	}

	protected abstract void escapeDefaultValue();

	@Override
	public ValidationResult<TypeProperty<T>> validateValue(T value) {
		ValidationResult<TypeProperty<T>> result = ValidationResult.success();

		if (!isSameMeasureGroup(measureUnit, value)) {
			TypeMessagesBundle messages = LocaleUtils.getMessages(TypeMessagesBundle.class);

			String passed = value.getMeasureUnit().getObjectName();
			String expected = measureUnit.getObjectName();
			result.error(this, messages.measuredValueCategoryMismatch(passed, expected));
		}
		return result;
	}

	private boolean isSameMeasureGroup(MeasureUnit measureUnit, Measurable value) {
		return measureUnit == null || value == null || measureUnit.isConvertibleFrom(value.getMeasureUnit());
	}
}
