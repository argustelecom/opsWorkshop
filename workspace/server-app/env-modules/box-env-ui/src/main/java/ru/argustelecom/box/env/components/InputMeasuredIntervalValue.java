package ru.argustelecom.box.env.components;

import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;

import ru.argustelecom.box.env.measure.model.BaseMeasureUnit;
import ru.argustelecom.box.env.measure.model.Measurable;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.measure.model.MeasuredIntervalValue;

@FacesComponent("inputMeasuredIntervalValue")
public class InputMeasuredIntervalValue extends AbstractInputMeasuredValue {

	@Override
	protected void initMeasurableValue(Measurable value, BaseMeasureUnit baseMeasure, MeasureUnit defaultMeasure) {
		Double rawStartValue = null;
		Double rawEndValue = null;

		checkState(value == null || value instanceof MeasuredIntervalValue);
		if (value != null) {
			rawStartValue = ((MeasuredIntervalValue) value).getStartValue();
			rawEndValue = ((MeasuredIntervalValue) value).getEndValue();
		}

		super.initMeasurableValue(value, baseMeasure, defaultMeasure);

		// Здесь умышленно не вызывается сеттер, так как это может привесит к передергиванию компонента при
		// инициализации
		if (rawStartValueChanged(rawStartValue)) {
			setPrivateState(PropertyKeys.rawStartValue, rawStartValue);
		}
		if (rawEndValueChanged(rawEndValue)) {
			setPrivateState(PropertyKeys.rawEndValue, rawEndValue);
		}
	}

	@Override
	protected void updateValue() {
		Double rawStartValue = getRawStartValue();
		Double rawEndValue = getRawEndValue();
		MeasureUnit rawMeasure = getRawMeasure();

		// Если не указано хотя бы одно из значений составной единицы измерения, то она неконсистентна и мы не можем
		// создавать value
		if (rawStartValue == null || rawEndValue == null || rawMeasure == null) {
			this.setValue(null);
			return;
		}

		MeasuredIntervalValue value = this.getValue();

		// Если value задан, то нужно его корректно поменять
		if (value != null) {
			// Если поменялась единица измерения, то необходимо пересоздать весь value, т.к. единица измерения не может
			// быть изменена
			if (!Objects.equals(value.getMeasureUnit(), rawMeasure)) {
				// Для создания будем использовать конструктор, позволяющий не пересчитывать значение
				value = new MeasuredIntervalValue(value.getStartValue(), value.getEndValue(), rawMeasure);
				this.setValue(value);
			}
			// Если поменялось значение измерямой величины, то нужно просто обновить его, оно пересчитается само
			if (!Objects.equals(value.getStartValue(), rawStartValue)) {
				value.setStartValue(rawStartValue);
			}
			if (!Objects.equals(value.getEndValue(), rawEndValue)) {
				value.setEndValue(rawEndValue);
			}
		} else {
			// Если значения не было, то нужно его создать. На этом этапе у нас должно быть как rawValue, так и
			// rawMeasure, иначе мы бы вышли из этого метода на самом первом условии
			value = new MeasuredIntervalValue(rawStartValue, rawEndValue, rawMeasure);
			this.setValue(value);
		}
	}

	@Override
	public Object getSubmittedValue() {
		if (getRawStartValue() == null || getRawEndValue() == null || getRawMeasure() == null) {
			return "-";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(getRawStartValue());
		builder.append(':');
		builder.append(getRawEndValue());
		builder.append(':');
		builder.append(getEntityConverter().convertToString(getRawMeasure()));
		return builder.toString();
	}

	@Override
	protected Object getConvertedValue(FacesContext context, Object submittedValue) {
		if ("-".equals(submittedValue)) {
			return null;
		}

		String[] valueParts = ((String) submittedValue).split(":");
		checkState(valueParts.length == 3);

		double rawStartValue = Double.parseDouble(valueParts[0]);
		double rawEndValue = Double.parseDouble(valueParts[1]);
		MeasureUnit rawMeasure = getEntityConverter().convertToObject(MeasureUnit.class, valueParts[2]);
		return new MeasuredIntervalValue(rawStartValue, rawEndValue, rawMeasure);
	}

	// ****************************************************************************************************************

	@Override
	public MeasuredIntervalValue getValue() {
		return (MeasuredIntervalValue) super.getValue();
	}

	public boolean rawStartValueChanged(Double rawStartValue) {
		return !Objects.equals(rawStartValue, getRawStartValue());
	}

	public Double getRawStartValue() {
		return getPrivateState(PropertyKeys.rawStartValue, Double.class);
	}

	public void setRawStartValue(Double rawStartValue) {
		setPrivateState(PropertyKeys.rawStartValue, rawStartValue);
		updateValue();
	}

	public boolean rawEndValueChanged(Double rawEndValue) {
		return !Objects.equals(rawEndValue, getRawEndValue());
	}

	public Double getRawEndValue() {
		return getPrivateState(PropertyKeys.rawEndValue, Double.class);
	}

	public void setRawEndValue(Double rawEndValue) {
		setPrivateState(PropertyKeys.rawEndValue, rawEndValue);
		updateValue();
	}

	static enum PropertyKeys {
		rawStartValue, rawEndValue
	}
}
