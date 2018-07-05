package ru.argustelecom.box.env.type;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.LongRangeValidator;
import javax.faces.validator.ValidatorException;

@FacesValidator("ru.argustelecom.box.env.type.DynamicLongRangeValidator")
public class DynamicLongRangeValidator extends LongRangeValidator {

	private final static String MIN_ATTR = "minimum";
	private final static String MAX_ATTR = "maximum";

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		Object minObj = component.getAttributes().get(MIN_ATTR);
		Object maxObj = component.getAttributes().get(MAX_ATTR);

		if (minObj != null) {
			setMinimum(longValue(minObj));
		}

		if (maxObj != null) {
			setMaximum(longValue(maxObj));
		}

		super.validate(context, component, value);
	}

	private static Long longValue(Object attributeValue) throws NumberFormatException {

		if (attributeValue instanceof Number) {
			return (((Number) attributeValue).longValue());
		} else {
			return Long.parseLong(attributeValue.toString());
		}
	}
}
