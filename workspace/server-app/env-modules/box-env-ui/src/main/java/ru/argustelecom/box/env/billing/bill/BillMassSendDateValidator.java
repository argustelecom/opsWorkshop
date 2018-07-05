package ru.argustelecom.box.env.billing.bill;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import ru.argustelecom.box.env.billing.bill.nls.BillMessagesBundle;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@FacesValidator(value = "billMassSendDateValidator")
public class BillMassSendDateValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value != null && ((Date) value).compareTo(new Date()) < 0) {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			BillMessagesBundle billMessages = LocaleUtils.getMessages(BillMessagesBundle.class);
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, overallMessages.error(),
					billMessages.dayCannotBeBeforeToday()));
		}
	}
}
