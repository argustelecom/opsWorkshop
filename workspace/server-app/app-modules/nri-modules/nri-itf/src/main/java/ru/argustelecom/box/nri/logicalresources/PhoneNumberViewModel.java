package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolRepository;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberRepository;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDtoTranslator;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

/**
 * Модель страницы карточки телефонного номера
 * Created by b.bazarov on 22.11.2017.
 */
@Named(value = "phoneNumberVM")
@PresentationModel
public class PhoneNumberViewModel extends ViewModel {

	private static final long serialVersionUID = -5716612579398650122L;

	/**
	 * Состояние вьюхи
	 */
	@Inject
	private PhoneNumberViewState viewState;

	/**
	 * Выбранный номер
	 */
	@Getter
	private PhoneNumberDto phoneNumber;

	/**
	 * Репозиторий доступа к сущностям телефонных номеров
	 */
	@Inject
	private PhoneNumberRepository repository;

	/**
	 * Репозиторий пулов телефонных номеров
	 */
	@Inject
	private PhoneNumberPoolRepository poolRepository;

	/**
	 * Сервис телефонных номеров
	 */
	@Inject
	private PhoneNumberAppService phoneNumberAppService;

	/**
	 * транслятор в ДТО
	 */
	@Inject
	private PhoneNumberDtoTranslator phoneNumberDtoTranslator;

	/**
	 * транслятор для пула
	 */
	@Inject
	private PhoneNumberPoolDtoTranslator phoneNumberPoolDtoTranslator;

	/**
	 * транслятор для ресурса
	 */
	@Inject
	private ResourceInstanceDtoTranslator resourceInstanceDtoTranslator;

	/**
	 * Создать пул
	 */
	@Getter
	private final Callback<PhoneNumberDto> changePool = phone -> {
		phoneNumberAppService.changePool(phone,phone.getPool());
		viewState.getPhoneNumber().setPool(poolRepository.findOne(phone.getPool().getId()));
	};

	/**
	 * Действия после созданя модели
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		init();

		unitOfWork.makePermaLong();
	}

	/**
	 * Инициализация вьюхи
	 */
	public void init() {
		PhoneNumber pn = repository.findOne(viewState.getPhoneNumber().getId());
		pn = initializeAndUnproxy(pn);

		this.phoneNumber = phoneNumberDtoTranslator.translate(pn);
		this.phoneNumber.setPool(phoneNumberPoolDtoTranslator.translateLazy(pn.getPool()));
		this.phoneNumber.setResource(resourceInstanceDtoTranslator.translateLazy(pn.getResource()));
	}

	public PhoneNumber getPhone() {
		return viewState.getPhoneNumber();
	}
}
