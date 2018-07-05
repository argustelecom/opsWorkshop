package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import ru.argustelecom.box.inf.nls.LocaleUtils;
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
 * Контроллер фрейма просмотра информации о пуле
 *
 * @author d.khekk
 * @since 31.10.2017
 */
@Named(value = "phoneNumberPoolFM")
@PresentationModel
public class PhoneNumberPoolFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Выбранный пул
	 */
	@Getter
	private PhoneNumberPoolDto pool;

	/**
	 * Сервис доступа к пулам телефонных номеров
	 */
	@Inject
	private PhoneNumberPoolAppService poolService;

	/**
	 * Действия перед отрисовкой фрейма
	 *
	 * @param pool        пул для отрисовки фрейма
	 */
	public void preRender(PhoneNumberPoolDto pool) {
		this.pool = pool;
	}

	/**
	 * Сохранить пул
	 */
	public void savePool() {
		poolService.save(pool);
	}

	/**
	 * Валидация имени пула перед изменениме
	 *
	 * @param facesContext ignored
	 * @param component    ignored
	 * @param objectName   новое имя
	 */
	@SuppressWarnings("unused")
	public void poolNameValidator(FacesContext facesContext, UIComponent component, Object objectName) {
		if (objectName == null)
			return;
		String objName = ((String) objectName).trim();
		PhoneNumberPoolFrameModelMessagesBundle messages = LocaleUtils.getMessages(PhoneNumberPoolFrameModelMessagesBundle.class);

		for (PhoneNumberPoolDto poolDto : poolService.findAllLazy()) {
			if (objName.equalsIgnoreCase(poolDto.getName()) && !pool.getId().equals(poolDto.getId()))
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.nameDoesNotUnique() , ""));
		}

		if (isBlank(objName)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.nameCanNotBeEmpty(), ""));
		}
	}
}
