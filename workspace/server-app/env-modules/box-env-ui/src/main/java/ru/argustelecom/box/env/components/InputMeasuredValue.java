package ru.argustelecom.box.env.components;

import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;

import ru.argustelecom.box.env.measure.model.BaseMeasureUnit;
import ru.argustelecom.box.env.measure.model.Measurable;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.measure.model.MeasuredValue;

@FacesComponent("inputMeasuredValue")
public class InputMeasuredValue extends AbstractInputMeasuredValue {

	@Override
	protected void initMeasurableValue(Measurable value, BaseMeasureUnit baseMeasure, MeasureUnit defaultMeasure) {
		Double rawValue = null;

		checkState(value == null || value instanceof MeasuredValue);
		if (value != null) {
			rawValue = ((MeasuredValue) value).getValue();
		}

		super.initMeasurableValue(value, baseMeasure, defaultMeasure);

		// Здесь умышленно не вызывается сеттер, так как это может привесит к передергиванию компонента при
		// инициализации
		if (rawValueChanged(rawValue)) {
			setPrivateState(PropertyKeys.rawValue, rawValue);
		}

		Boolean requiredForChild = (Boolean) getAttributes().get(PropertyKeys.requiredForChild.toString());

		if (requiredForChildChanged(requiredForChild)) {
			setPrivateState(PropertyKeys.requiredForChild, requiredForChild);
		}
	}

	@Override
	protected void updateValue() {
		Double rawValue = getRawValue();
		MeasureUnit rawMeasure = getRawMeasure();

		// Если не указано хотя бы одно из значений составной единицы измерения, то она неконсистентна и мы не можем
		// создавать value
		if (rawValue == null || rawMeasure == null) {
			this.setValue(null);
			return;
		}

		MeasuredValue value = this.getValue();

		// Если value задан, то нужно его корректно поменять
		if (value != null) {
			// Если поменялась единица измерения, то необходимо пересоздать весь value, т.к. единица измерения не может
			// быть изменена
			if (!Objects.equals(value.getMeasureUnit(), rawMeasure)) {
				// Для создания будем использовать конструктор, позволяющий не пересчитывать значение
				value = new MeasuredValue(value.getValue(), rawMeasure);
				this.setValue(value);
			}
			// Если поменялось значение измерямой величины, то нужно просто обновить его, оно пересчитается само
			if (!Objects.equals(value.getValue(), rawValue)) {
				value.setValue(rawValue);
			}
		} else {
			// Если значения не было, то нужно его создать. На этом этапе у нас должно быть как rawValue, так и
			// rawMeasure, иначе мы бы вышли из этого метода на самом первом условии
			value = new MeasuredValue(rawValue, rawMeasure);
			this.setValue(value);
		}
	}

	@Override
	public Object getSubmittedValue() {
		if (getRawValue() == null || getRawMeasure() == null) {
			return "-";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(getRawValue());
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
		checkState(valueParts.length == 2);

		double rawValue = Double.parseDouble(valueParts[0]);
		MeasureUnit rawMeasure = getEntityConverter().convertToObject(MeasureUnit.class, valueParts[1]);
		return new MeasuredValue(rawValue, rawMeasure);
	}

	// ****************************************************************************************************************

	@Override
	public MeasuredValue getValue() {
		return (MeasuredValue) super.getValue();
	}

	public boolean rawValueChanged(Double rawValue) {
		return !Objects.equals(rawValue, getRawValue());
	}

	public Double getRawValue() {
		return getPrivateState(PropertyKeys.rawValue, Double.class);
	}

	public void setRawValue(Double rawValue) {
		setPrivateState(PropertyKeys.rawValue, rawValue);
		updateValue();
	}

	public Boolean getRequiredForChild() {
		return getPrivateState(PropertyKeys.requiredForChild, Boolean.class);
	}

	private boolean requiredForChildChanged(Boolean requiredForChild) {
		return !Objects.equals(requiredForChild, getRequiredForChild());
	}

	enum PropertyKeys {
		rawValue, requiredForChild
	}
}
