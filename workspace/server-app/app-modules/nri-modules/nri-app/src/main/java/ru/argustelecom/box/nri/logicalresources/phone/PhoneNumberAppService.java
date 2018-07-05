package ru.argustelecom.box.nri.logicalresources.phone;

import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.lifecycle.api.history.LifecycleHistoryService;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.booking.PhoneNumberBookingRequirementDto;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberLifecycle;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationInstance;
import ru.argustelecom.box.nri.logicalresources.phone.nls.PhoneNumberAppServiceMessagesBundle;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.utils.CheckUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.getFirst;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Сервис доступа к телефонным номерам
 *
 * @author d.khekk
 * @since 31.10.2017
 */
@ApplicationService
public class PhoneNumberAppService {

	/**
	 * Репозиторий доступа к хранилищу телефонных номеров
	 */
	@Inject
	private PhoneNumberRepository repository;

	/**
	 * Сервис генерации айдишников
	 */
	@Inject
	private IdSequenceService idSequenceService;

	/**
	 * Транслятор сущностей в ДТО
	 */
	@Inject
	private PhoneNumberDtoTranslator translator;

	/**
	 * Репозиторий доступа к хранилищу пулов телефонных номеров
	 */
	@Inject
	private PhoneNumberPoolRepository poolRepository;

	/**
	 * Сервис доступа к истории ЖЦ объектов
	 */
	@Inject
	private LifecycleHistoryService historyService;

	/**
	 * Сервис смены статуса ЖЦ
	 */
	@Inject
	private LifecycleRoutingService routingService;

	/**
	 * сервис типов
	 */
	@Inject
	private TypeFactory factory;

	/**
	 * Найти все телефонные номера
	 *
	 * @return список телефонных номеров
	 */
	public List<PhoneNumberDto> findAll() {
		return repository.findAll().stream()
				.map(translator::translate)
				.collect(toList());
	}

	/**
	 * Найти телефон по его ID
	 *
	 * @param id ID телефонного номера
	 * @return телефонный номер
	 */
	public PhoneNumberDto findPhoneNumberById(@Nonnull Long id) {
		return translator.translate(repository.findOne(id));
	}

	/**
	 * Найти телефонные номера по пулу, в котором они находится
	 *
	 * @param poolDto ДТО пула телефонных номеров
	 * @return список телефонных номеров
	 */
	public List<PhoneNumberDto> findPhoneNumbers(@Nonnull PhoneNumberPoolDto poolDto) {
		return repository.findByPool(poolRepository.findOne(poolDto.getId()))
				.stream()
				.map(translator::translate)
				.collect(toList());
	}

	/**
	 * Найти телефонные номера от .. до ..
	 * @param from от номера
	 * @param to до номера
	 * @return телефонные номера
	 */
	public List<PhoneNumberDto> findPhoneNumbers(String from, String to) {
		return repository.findPhoneNumbers(from, to).stream().map(translator::translate).collect(toList());
	}

	/**
	 * Создать телефонный номер
	 *
	 * @param dto  дто
	 * @param spec спецификация
	 * @return дто нового тн
	 */
	public PhoneNumberDto createPhoneNumber(@Nonnull PhoneNumberDto dto, @Nonnull PhoneNumberSpecification spec) {
		Preconditions.checkArgument(dto != null);
		Preconditions.checkArgument(dto.getPool() != null);
		PhoneNumberPool pool = poolRepository.findOne(dto.getPool().getId());

		if (pool != null) {
			PhoneNumber pn = new PhoneNumber(idSequenceService.nextValue(PhoneNumberPool.class));
			pn.setSpecInstance(factory.createInstance(spec,PhoneNumberSpecificationInstance.class, MetadataUnit.generateId()));
			pn.getSpecInstance().setPhoneNumber(pn);
			pn.setName(dto.getName());
			pool.getPhoneNumbers().add(pn);
			pn.setPool(pool);
			poolRepository.save(pool);
			return translator.translate(pn);
		}
		return null;
	}

	/**
	 * Удалить/переместить в архив телефонный номер
	 *
	 * @param phoneNumberDto ДТО номера для удаления
	 */
	public void remove(PhoneNumberDto phoneNumberDto) throws BusinessExceptionWithoutRollback {
		remove(phoneNumberDto.getId());
	}

	/**
	 * Удалить/переместить в архив телефонный номер
	 * @param id идентификационный номер
	 */
	public void remove(Long id) throws BusinessExceptionWithoutRollback {
		PhoneNumberAppServiceMessagesBundle messages = LocaleUtils.getMessages(PhoneNumberAppServiceMessagesBundle.class);
		checkArgument(id != null, messages.idIsNull());
		PhoneNumber phoneNumber = repository.findOneWithRefresh(id);
		checkArgument(phoneNumber != null, messages.numberWithId()
				+ id + messages.isMissed());
		checkArgument(!phoneNumber.inState(PhoneNumberState.OCCUPIED),
				messages.occupy());
		checkArgument(phoneNumber.getBookingOrder() == null,
				messages.booked());
		if (historyService.getHistory(phoneNumber).isEmpty()) {
			repository.remove(phoneNumber);
		} else {
			moveToArchive(phoneNumber);
		}
	}

	/**
	 * Переместить номер в архив
	 *
	 * @param phoneNumber номер для перемещения
	 */
	private void moveToArchive(PhoneNumber phoneNumber) {
		phoneNumber.setPool(null);
		phoneNumber.setResource(null);
		routingService.performRouting(phoneNumber, PhoneNumberLifecycle.Routes.DELETE);
		repository.save(phoneNumber);
	}

	/**
	 * Удалить несколько номеров
	 *
	 * @param phoneNumbers номера для удаления
	 */
	public void removeSeveralNumbers(List<PhoneNumber> phoneNumbers) {
		Map<Boolean, List<PhoneNumber>> sortedNumbers = phoneNumbers.stream().collect(groupingBy(number -> historyService.getHistory(number).isEmpty()));
		if (sortedNumbers.containsKey(true)) {
			repository.removeSeveralNumbers(sortedNumbers.get(true).stream().map(PhoneNumber::getId).collect(toList()));
		}
		if (sortedNumbers.containsKey(false)) {
			sortedNumbers.get(false).forEach(this::moveToArchive);
		}
	}

	/**
	 * Сменить пул
	 *
	 * @param phoneNumberDto дто телефонного номера
	 * @param newPoolDto     дто нового пула
	 */
	public void changePool(PhoneNumberDto phoneNumberDto, PhoneNumberPoolDto newPoolDto) {
		PhoneNumberPool newPool = poolRepository.findOne(newPoolDto.getId());
		changePool(phoneNumberDto, newPool);
	}

	/**
	 * Сменить пул
	 *
	 * @param phoneNumberDto дто телефонного номера
	 * @param newPool        новый пул
	 */
	public void changePool(PhoneNumberDto phoneNumberDto, PhoneNumberPool newPool) {
		PhoneNumber phoneNumber = repository.findOne(phoneNumberDto.getId());
		if (phoneNumber != null && newPool != null) {
			PhoneNumberPool prevPool = phoneNumber.getPool();
			if (prevPool != null) {
				prevPool.getPhoneNumbers().remove(phoneNumber);
			}
			newPool.getPhoneNumbers().add(phoneNumber);
			phoneNumber.setPool(newPool);
			poolRepository.save(newPool);
		}
	}

	/**
	 * Удалить из ресурса номера
	 *
	 * @param phoneNumbers id номеров
	 */
	public void removeFromResource(List<Long> phoneNumbers) {
		repository.removeFromResource(phoneNumbers);
	}

	/**
	 * Найти все "имена" телефонных номеров
	 *
	 * @return список "имен" телефонных номеров
	 */
	public List<String> findAllNotDeletedPhoneDigits() {
		return repository.findAllNotDeletedPhoneDigits();
	}

	/**
	 * Проверить аргумент
	 *
	 * @param expression выражение
	 * @param message    сообщение в случае ошибки
	 * @throws BusinessExceptionWithoutRollback исключение, не вызывающее откат формы
	 */
	private void checkArgument(boolean expression, String message) throws BusinessExceptionWithoutRollback {
		if (!expression) {
			throw new BusinessExceptionWithoutRollback(message);
		}
	}

	/**
	 * Получить описание ошибки при удалении
	 *
	 * @param notRemovedNumbers список номеров, удаление которых невозможно
	 * @return русское описание ошибки при удалении
	 */
	public String getDescription(List<String> notRemovedNumbers) {
		PhoneNumberAppServiceMessagesBundle messages = LocaleUtils.getMessages(PhoneNumberAppServiceMessagesBundle.class);
		if (notRemovedNumbers.size() > 1) {
			String numbers = notRemovedNumbers.stream().limit(5).collect(joining(", "));
			String moreNumbers = notRemovedNumbers.size() <= 5 ? "" : messages.andMore() + (notRemovedNumbers.size() - 5);
			return messages.numbersWasNotDeleted(numbers, moreNumbers);
		} else return messages.phoneNumberWasNotDeleted(getFirst(notRemovedNumbers, ""));
	}

	/**
	 * Найти телефонные номера по требованию
	 * @param currentRequirement требование
	 * @return список подходящих телефонных номеров
	 */
	public List<PhoneNumberDto> findPhoneNumbersLike(PhoneNumberBookingRequirementDto currentRequirement) {
		CheckUtils.checkArgument(currentRequirement != null,
				LocaleUtils.getMessages(PhoneNumberAppServiceMessagesBundle.class).search());

		PhoneNumber.PhoneNumberQuery query = new PhoneNumber.PhoneNumberQuery();

		List<PhoneNumber> phoneNumbers = repository.findByPredicates(currentRequirement.createPredicates(query),
				query, 100);

		if (CollectionUtils.isEmpty(phoneNumbers)) {
			return Collections.emptyList();
		}

		return phoneNumbers.stream()
				.map(translator::translate)
				.collect(toList());
	}
}
