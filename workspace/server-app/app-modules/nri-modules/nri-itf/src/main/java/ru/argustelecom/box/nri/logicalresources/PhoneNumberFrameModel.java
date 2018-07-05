package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import lombok.Setter;
import org.jboss.logging.Logger;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.box.nri.logicalresources.nls.LogicalResourcesMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Контроллер фрейма просмотра информации о телефонном номере
 *
 * @author d.khekk
 * @since 01.11.2017
 */
@Named("phoneNumberFM")
@PresentationModel
public class PhoneNumberFrameModel implements Serializable {

	private static final Logger log = Logger.getLogger(PhoneNumberFrameModel.class);
	private static final long serialVersionUID = 1L;

	/**
	 * Собитие нажатия кнопки удаления
	 */
	@Getter
	@Setter
	private Callback<PhoneNumberDto> onDeleteButtonPressed;

	/**
	 * Сервис доступа к хранилищу телефонных номеров
	 */
	@Inject
	private PhoneNumberAppService phoneService;

	/**
	 * Выбранный пул
	 */
	@Getter
	private List<PhoneNumberDto> phoneNumbers;

	/**
	 * Список выбранных номеров
	 */
	@Getter
	@Setter
	private List<PhoneNumberDto> selectedPhones;

	/**
	 * Действия перед отрисовкой фрейма
	 *
	 * @param phoneNumberPool пул для отрисовки фрейма
	 */
	public void preRender(PhoneNumberPoolDto phoneNumberPool) {
		selectedPhones = new ArrayList<>();
		this.phoneNumbers = phoneNumberPool.getPhoneNumbers();
	}

	/**
	 * Удалить выбранный номера телефонов
	 */
	public void removeSelectedNumbers() {
		if (!isEmpty(selectedPhones)) {
			List<String> notRemovedNumbers = new ArrayList<>();
			for (PhoneNumberDto selectedPhone : selectedPhones) {
				try {
					phoneService.remove(selectedPhone);
					onDeleteButtonPressed.execute(selectedPhone);
				} catch (BusinessExceptionWithoutRollback e) {
					log.error("Ошибка при удалении номера", e);
					notRemovedNumbers.add(selectedPhone.getName());
				}
			}
			if (!isEmpty(notRemovedNumbers))
				Notification.error(LocaleUtils.getMessages(LogicalResourcesMessagesBundle.class).warning(), phoneService.getDescription(notRemovedNumbers));
			else {
				selectedPhones.clear();
			}
		}
	}
}
