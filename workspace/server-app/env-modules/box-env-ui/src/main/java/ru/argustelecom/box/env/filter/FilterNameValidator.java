package ru.argustelecom.box.env.filter;

import java.io.Serializable;

import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.filter.nls.FilterMessagesBundle;
import ru.argustelecom.box.env.login.LoginService;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@Named
@ConversationScoped
public class FilterNameValidator implements Validator, Serializable {

	@Inject
	private ListFilterPresetFrameModel presetFrameModel;

	@Inject
	private ListFilterPresetAppService listFilterPresetAppService;

	@Inject
	private LoginService loginService;

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String str = (String) value;
		OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
		FilterMessagesBundle filterMessages = LocaleUtils.getMessages(FilterMessagesBundle.class);

		// Убрал проверку на длину строки на уровень ListFilterPresetCreationDialog p:inputText maxLength=64, чтобы
		// сохранить однообразность сообщений, т.к. в Messages_ru_RU / Messages_EN уже прописано аналогичное сообщение
		if (str.trim().length() == 0) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, overallMessages.error(),
					filterMessages.presetNameIsBlank()));
		}
		if (listFilterPresetAppService.findByName(str, loginService.getCurrentEmployee().getId(),
				presetFrameModel.getPage()) != null) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, overallMessages.error(),
					filterMessages.presetAlreadyExists()));
		}
	}

	private static final long serialVersionUID = -8909946868425774654L;
}
