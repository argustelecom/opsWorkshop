package ru.argustelecom.box.env.numerationpattern;

import ru.argustelecom.box.env.measure.nls.MeasureMessagesBundle;
import ru.argustelecom.box.env.numerationpattern.nls.NumerationMessagesBundle;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ConversationScoped
public class NumerationSequenceNameValidator implements Validator, Serializable {

	private static final Pattern VALID_NAME_SYMBOL_PATTERN = Pattern.compile("^([\\w_0-9]+)",
			Pattern.UNICODE_CHARACTER_CLASS);

	@Inject
	private NumerationSequenceAppService numerationSequenceAppService;

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
		NumerationMessagesBundle numerationMessages = LocaleUtils.getMessages(NumerationMessagesBundle.class);

		if (numerationSequenceAppService.findNumerationSequenceByName((String) value, false) != null) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, overallMessages.error(),
					numerationMessages.sequenceAlreadyExists()));
		}
		if (!VALID_NAME_SYMBOL_PATTERN.matcher((String) value).matches()) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, overallMessages.error(),
					numerationMessages.sequenceShouldNotContainWhitespaces()));
		}
	}

	private static final long serialVersionUID = 4351272700170880767L;
}
