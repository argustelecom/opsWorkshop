package ru.argustelecom.box.env.numerationpattern;

import ru.argustelecom.box.env.numerationpattern.nls.NumerationMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;


@FacesValidator("ru.argustelecom.box.env.numerationpattern.CapacityValidator")
public class CapacityValidator implements Validator {
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		NumerationMessagesBundle numerationMessages = LocaleUtils.getMessages(NumerationMessagesBundle.class);

		String capacityValue
				= (String) ((UIInput) context.getViewRoot().findComponent("sequence_edit_form-capacity")).getSubmittedValue();
		if (!"".equals(capacityValue) && (String.valueOf(value)).length() > Integer.valueOf(capacityValue)) {
			FacesMessage message = new FacesMessage(numerationMessages.valueIsTooLarge());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
	}
}
