package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import lombok.Setter;
import org.jboss.logging.Logger;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.box.nri.logicalresources.nls.LogicalResourcesMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolRepository;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationRepository;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState.AVAILABLE;
import static ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState.LOCKED;
import static ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState.OCCUPIED;

/**
 * Контроллер страницы поиска телефонных номеров
 *
 * @author d.khekk
 * @since 23.11.2017
 */
@Named(value = "phoneNumbersVM")
@PresentationModel
public class PhoneNumbersViewModel extends ViewModel {

	private static final Logger log = Logger.getLogger(PhoneNumbersViewModel.class);

	/**
	 * Выбранные номера
	 */
	@Getter
	@Setter
	private List<PhoneNumberDtoTmp> selectedPhones;

	/**
	 * Ленивый список номеров
	 */
	@Inject
	@Getter
	private PhoneNumberList lazyNumbers;

	/**
	 * Репозиторий пулов номеров
	 */
	@Inject
	private PhoneNumberPoolRepository poolRepository;

	/**
	 * Репозиторий спецификаций номеров
	 */
	@Inject
	private PhoneNumberSpecificationRepository specRepository;

	/**
	 * Сервис доступа к телефонам
	 */
	@Inject
	private PhoneNumberAppService phoneService;

	/**
	 * Список доступных пулов номеров
	 */
	@Getter
	private List<PhoneNumberPool> pools = new ArrayList<>();

	/**
	 * Список доступных спецификаций номеров
	 */
	@Getter
	private List<PhoneNumberSpecification> specifications = new ArrayList<>();

	/**
	 * Обновляет список номеров после перемещения номеров в другой пул
	 */
	@Getter
	private Callback<Object> reloadData = ignored -> {
		lazyNumbers.reloadData();
		selectedPhones.clear();
	};

	@Override
	@PostConstruct
	protected void postConstruct() {
		pools = poolRepository.findAll();
		specifications = specRepository.getAllSpecs();
		unitOfWork.makePermaLong();
	}

	/**
	 * Удалить выбранные номера
	 */
	public void remove() {
		if (!isEmpty(selectedPhones)) {
			List<String> notRemovedNumbers = new ArrayList<>();
			for (PhoneNumberDtoTmp selectedPhone : selectedPhones) {
				try {
					phoneService.remove(selectedPhone.getId());
				} catch (BusinessExceptionWithoutRollback e) {
					log.error("Ошибка при удалении номера", e);
					notRemovedNumbers.add(selectedPhone.getName());
				}
			}
			if (!isEmpty(notRemovedNumbers))
				Notification.error(LocaleUtils.getMessages(LogicalResourcesMessagesBundle.class).warning(), phoneService.getDescription(notRemovedNumbers));
			else
				selectedPhones.clear();
			lazyNumbers.reloadData();
		}
	}

	/**
	 * Получить все возможные статусы
	 *
	 * @return список возможных статусов
	 */
	public List<PhoneNumberState> getStates() {
		return asList(AVAILABLE, OCCUPIED, LOCKED);
	}

	/**
	 * Очистить список выбранных номеров
	 */
	public void clearSelected() {
		selectedPhones.clear();
	}
}
