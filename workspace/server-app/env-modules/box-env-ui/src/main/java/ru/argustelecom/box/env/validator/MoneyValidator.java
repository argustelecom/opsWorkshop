package ru.argustelecom.box.env.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.nls.MoneyMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@FacesValidator("moneyValidator")
public class MoneyValidator implements Validator {

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
		if(value instanceof Money) {
			if(((Money) value).isZero()) {
				throwValidationException();
			}
		}
	}

	private void throwValidationException() {
		OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
		MoneyMessagesBundle moneyMessages = LocaleUtils.getMessages(MoneyMessagesBundle.class);

		FacesMessage msg = new FacesMessage(
				overallMessages.error(),
				moneyMessages.amountCannotBeZero()
		);
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(msg);
	}
}
