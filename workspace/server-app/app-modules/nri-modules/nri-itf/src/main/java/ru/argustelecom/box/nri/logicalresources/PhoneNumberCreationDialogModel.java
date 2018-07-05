package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import lombok.Setter;
import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.box.nri.logicalresources.nls.PhoneNumberCreationDialogModelMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationRepository;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.function.BiConsumer;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Контроллер диалога создания нового пула
 *
 * @author b.bazarov
 * @since 03.11.2017
 */
@Named("phoneNumberCreationDM")
@PresentationModel
public class PhoneNumberCreationDialogModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(PhoneNumberCreationDialogModel.class);

	/**
	 * Новый телефонный номер
	 */
	@Getter
	@Setter
	private PhoneNumberDto newPhone;

	/**
	 * Пул
	 */
	@Getter
	@Setter
	private PhoneNumberPoolDto pool;

	/**
	 * спецификация
	 */
	@Getter
	@Setter
	private PhoneNumberSpecification newPhoneSpec;

	/**
	 * возможные телефонные спеки
	 */
	@Getter
	@Setter
	private List<PhoneNumberSpecification> possiblePhoneSpecs;

	/**
	 * Действие при нажатии кнопки "Создать" в одиночном номере
	 */
	@Getter
	@Setter
	private BiConsumer<PhoneNumberDto, PhoneNumberSpecification> onCreate;

	/**
	 * Действие с сохраненным пулом после генерации номеров
	 */
	@Getter
	@Setter
	private Callback<PhoneNumberPoolDto> afterGenerate;

	/**
	 * От номера
	 */
	@Getter
	@Setter
	private String fromPhone;

	/**
	 * До номера
	 */
	@Getter
	@Setter
	private String toPhone;

	/**
	 * Репозиторий спецификаций телефонныъ номеров
	 */
	@Inject
	private PhoneNumberSpecificationRepository phoneNumberSpecificationRepository;

	/**
	 * Сервис доступа к телефонным номерам
	 */
	@Inject
	private PhoneNumberAppService phoneService;

	/**
	 * Сервис пулов номеров
	 */
	@Inject
	private PhoneNumberPoolAppService poolService;

	/**
	 * Инициализация
	 *
	 * @param pool пул
	 */
	public void init(PhoneNumberPoolDto pool) {
		if (possiblePhoneSpecs == null)
			possiblePhoneSpecs = phoneNumberSpecificationRepository.getAllSpecs();
		newPhone = PhoneNumberDto.builder().pool(pool).build();
		this.pool = pool;
	}

	/**
	 * Создать номер
	 */
	public void create() {
		onCreate.accept(newPhone, newPhoneSpec);
	}

	/**
	 * Сгенерировать номера
	 */
	public void generateNumbers() {
		try {
			PhoneNumberPoolDto persistedPool = poolService.generatePhoneNumbers(pool.getId(), newPhoneSpec.getId(), fromPhone, toPhone);
			afterGenerate.execute(persistedPool);
			RequestContext.getCurrentInstance().update("phone_number_info_form");
			RequestContext.getCurrentInstance().execute("PF('phoneNumberCreationDlg').hide(); Argus.System.AutoHeight.compute('phone_number_pool_tree_form-phone_number_pool_tree', 35);");
		} catch (BusinessExceptionWithoutRollback e) {
			log.error("Ошибка при генерации номеров", e);
			PhoneNumberCreationDialogModelMessagesBundle messages = LocaleUtils.getMessages(PhoneNumberCreationDialogModelMessagesBundle.class);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.error() + e.getMessage(), ""));
		}
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
		String objName = ((String) objectName).trim().replaceAll("\\D","");

		PhoneNumberCreationDialogModelMessagesBundle messages = LocaleUtils.getMessages(PhoneNumberCreationDialogModelMessagesBundle.class);
		for (String digits : phoneService.findAllNotDeletedPhoneDigits()) {
			if (objName.equalsIgnoreCase(digits))
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.error(),messages.phoneNumberIsAlreadyExist() ));
		}

		if (isBlank(objName)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.error(),messages.nameCanNotBeEmpty() ));
		}
	}
}
