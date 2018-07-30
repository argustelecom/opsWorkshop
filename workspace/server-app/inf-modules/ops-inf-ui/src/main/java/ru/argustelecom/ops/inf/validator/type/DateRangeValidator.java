package ru.argustelecom.ops.inf.validator.type;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("ru.argustelecom.ops.inf.validator.type.DateRangeValidator")
public class DateRangeValidator implements Validator, HasAttribute {

	private final static String MIN_ATTR = "minimum";
	private final static String MAX_ATTR = "maximum";

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
		if (!(value instanceof Date)) {
			return;
		}

		getAttribute(MIN_ATTR, uiComponent).ifPresent(minDate -> {
			if (((Date) value).before((Date) minDate)) {
				throwValidationException();
			}
		});

		getAttribute(MAX_ATTR, uiComponent).ifPresent(maxDate -> {
			if (((Date) value).after((Date) maxDate)) {
				throwValidationException();
			}
		});
	}

	private void throwValidationException() {
		FacesMessage msg = new FacesMessage("", "Дата начала не может быть больше даты окончания");
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(msg);
	}
}