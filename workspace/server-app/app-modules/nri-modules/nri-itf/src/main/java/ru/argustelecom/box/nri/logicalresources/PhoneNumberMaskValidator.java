package ru.argustelecom.box.nri.logicalresources;

import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.logicalresources.nls.PhoneNumberMaskValidatorMessagesBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Валидатор маски телефонного номера
 */
@FacesValidator("ru.argustelecom.box.nri.logicalresources.PhoneNumberMaskValidator")
public class PhoneNumberMaskValidator  implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) {
		if (value == null)
			return;

		String mask = (String)value;
		//Маска должна содержать символы BOX-1832 +, любые цифры, (, ), дефис, пробел
		Matcher m = Pattern.compile("[^\\d\\*\\-\\+\\(\\)\\*\\-\\+\\(\\) ]").matcher(mask);
		if(m.find()){
			PhoneNumberMaskValidatorMessagesBundle messages = LocaleUtils.getMessages(PhoneNumberMaskValidatorMessagesBundle.class);
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.maskError(),
					messages.maskNeedsToContain()));
		}

		String objMask = ((String) value).trim();
		String string = objMask.replaceAll("[^\\d.]", "");

		if (string.length() > 15)
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					LocaleUtils.getMessages(PhoneNumberMaskValidatorMessagesBundle.class).phoneNumberLenghtNeedToBe(), ""));
	}
}
