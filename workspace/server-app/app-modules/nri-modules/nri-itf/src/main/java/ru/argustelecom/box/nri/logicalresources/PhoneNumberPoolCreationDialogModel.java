package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.box.nri.logicalresources.nls.PhoneNumberPoolFrameModelMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Контроллер диалога создания нового пула
 *
 * @author d.khekk
 * @since 31.10.2017
 */
@Named("phoneNumberPoolCreationDM")
@PresentationModel
public class PhoneNumberPoolCreationDialogModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Новый пул телефонных номеров
	 */
	@Getter
	private PhoneNumberPoolDto newPool;

	/**
	 * Коллбек для передачи на страницу созданного пула телефонных номеров
	 */
	@Getter
	@Setter
	private Callback<PhoneNumberPoolDto> onCreateButtonPressed;

	/**
	 * Сервис доступа к пулам телефонных номеров
	 */
	@Inject
	private PhoneNumberPoolAppService poolService;

	/**
	 * Действия после открытия диалога создания
	 */
	public void onCreationDialogOpen() {
		newPool = PhoneNumberPoolDto.builder().build();
	}

	/**
	 * Создать новый пул
	 */
	public void create() {
		onCreateButtonPressed.execute(newPool);
	}

	/**
	 * Валидация имени
	 *
	 * @param facesContext facesContext
	 * @param component    UIComponent
	 * @param objectName   Имя для валидации
	 */
	@SuppressWarnings("unused")
	public void nameValidator(FacesContext facesContext, UIComponent component, Object objectName) {
		if (objectName == null)
			return;
		String objName = ((String) objectName).trim();
		PhoneNumberPoolFrameModelMessagesBundle messages = LocaleUtils.getMessages(PhoneNumberPoolFrameModelMessagesBundle.class);
		for (PhoneNumberPoolDto poolDto : poolService.findAllLazy()) {
			if (objName.equalsIgnoreCase(poolDto.getName()))
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.nameDoesNotUnique(), ""));
		}

		if (isBlank(objName)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.nameCanNotBeEmpty(), ""));
		}
	}
}
