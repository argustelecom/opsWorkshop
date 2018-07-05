package ru.argustelecom.box.nri.logicalresources.phone;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.math.NumberUtils;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationInstance;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationRepository;
import ru.argustelecom.box.nri.logicalresources.phone.nls.PhoneNumberPoolAppServiceMessagesBundle;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.utils.comparators.AlphanumObjectNameComparator;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Collections2.filter;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Сервис для работы с пулами телефонных номеров
 *
 * @author d.khekk
 * @since 31.10.2017
 */
@ApplicationService
public class PhoneNumberPoolAppService {

	/**
	 * Репозиторий доступа к хранилищу пулов телефонных номеров
	 */
	@Inject
	private PhoneNumberPoolRepository repository;

	/**
	 * Сервис доступа к хранилищу телефонных номеров
	 */
	@Inject
	private PhoneNumberAppService phoneNumberService;

	/**
	 * Транслятор сущности в ДТО
	 */
	@Inject
	private PhoneNumberPoolDtoTranslator translator;

	/**
	 * Транслятор номеров
	 */
	@Inject
	private PhoneNumberDtoTranslator phoneTranslator;

	/**
	 * сервис id
	 */
	@Inject
	private IdSequenceService idSequence;

	/**
	 * Репозиторий спецификаций номеров
	 */
	@Inject
	private PhoneNumberSpecificationRepository phoneNumberSpecRepository;

	/**
	 * сервис типов
	 */
	@Inject
	private TypeFactory factory;


	/**
	 * Найти все пулы телефонных номеров
	 *
	 * @return пулы. без телефонов
	 */
	public List<PhoneNumberPoolDto> findAllLazy() {
		List<PhoneNumberPool> pools = repository.findAll();
		if (pools != null) {
			return pools.stream().map(translator::translateLazy).sorted(new AlphanumObjectNameComparator()).collect(toList());
		}
		return new ArrayList<>();
	}

	/**
	 * Найти номера пула
	 *
	 * @param poolId пул
	 * @return номера
	 */
	public List<PhoneNumberDto> getPhoneNumbers(Long poolId) {
		PhoneNumberPool pool = repository.findOne(poolId);
		if (pool != null && !isEmpty(pool.getPhoneNumbers())) {
			return pool.getPhoneNumbers().stream()
					.map(phoneTranslator::translate)
					.sorted(new AlphanumObjectNameComparator())
					.collect(toList());
		}
		return new ArrayList<>();
	}

	/**
	 * Найти пул по его ID
	 *
	 * @param id ID пула
	 * @return найденный пул
	 */
	public PhoneNumberPoolDto findPoolById(Long id) {
		Preconditions.checkArgument(id != null);
		return translator.translate(repository.findOne(id));
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
	 * Создать новый пул номеров
	 *
	 * @param newPoolDto ДТО нового пула
	 * @return проинициализированный пул
	 */
	public PhoneNumberPoolDto createPool(PhoneNumberPoolDto newPoolDto) {
		return translator.translate(repository.create(newPoolDto.getName(), newPoolDto.getComment()));
	}

	/**
	 * Сохранить пул номеров
	 *
	 * @param poolDto ДТО пула с новым состоянием
	 */
	public void save(PhoneNumberPoolDto poolDto) {
		Preconditions.checkArgument(poolDto.getId() != null);
		PhoneNumberPool pool = repository.findOne(poolDto.getId());
		pool.setName(poolDto.getName());
		pool.setComment(poolDto.getComment());
		repository.save(pool);
	}

	/**
	 * получить номера по id пула
	 *
	 * @param poolId id пула
	 * @param resId  id ресурса, для которого ищем номера
	 * @return номера
	 */
	public List<PhoneNumberDto> getPhoneNumbersByPoolAndRes(Long poolId, Long resId) {
		return repository.findOne(poolId).getPhoneNumbers().stream()
				.filter(number -> number.getResource() == null || number.getResource().getId().equals(resId))
				.map(phoneTranslator::translate)
				.collect(toList());
	}

	/**
	 * Удалить пул
	 *
	 * @param poolDto ДТО удаляемого пула
	 */
	public void remove(@Nonnull PhoneNumberPoolDto poolDto) {
		Preconditions.checkArgument(poolDto.getId() != null);
		PhoneNumberPool pool = repository.findOneWithRefresh(poolDto.getId());
		PhoneNumberPoolAppServiceMessagesBundle messages = LocaleUtils.getMessages(PhoneNumberPoolAppServiceMessagesBundle.class);
		Preconditions.checkArgument(canBeDeleted(pool), messages.poolCanNotBeDeletedBecauseNumberIsUsedInBusinessProcess());
		phoneNumberService.removeSeveralNumbers(pool.getPhoneNumbers());
		repository.remove(pool);
	}

	/**
	 * Можно ли удалить пул
	 *
	 * @param pool пул для удаления
	 * @return true, если телефоны пула не участвуют в бизнес-процессах
	 */
	private boolean canBeDeleted(PhoneNumberPool pool) {
		List<PhoneNumber> phoneNumbers = pool.getPhoneNumbers();
		return phoneNumbers.stream().noneMatch(phone -> phone.getBookingOrder() != null || phone.getState().equals(PhoneNumberState.OCCUPIED));
	}

	/**
	 * сгенерировать номер long -> String по маске
	 *
	 * @param number      long номер
	 * @param numberMask  маска. В маске все символы. в которые нужно подставить цифры, обозначены Х
	 * @param digitLength длина требуемого набора цифр. получаем извне, чтобы не считать для каждого номера
	 * @return номер в виде строки
	 */
	private String generateNumber(long number, String numberMask, int digitLength) {
		String digitFormat = "%0" + digitLength + "d";
		String digits = String.format(digitFormat, number);
		String resultNumber = numberMask;
		for (char c : digits.toCharArray()) {
			resultNumber = resultNumber.replaceFirst("X", String.valueOf(c));
		}
		return resultNumber;
	}

	/**
	 * Сгенерировать номера телефонов
	 *
	 * @param poolId      id пула
	 * @param phoneSpecId id спецификации номеров
	 * @param from        первый номер для генерации
	 * @param to          последний номер для генерации
	 * @return пул
	 */
	public PhoneNumberPoolDto generatePhoneNumbers(@Nonnull Long poolId, @Nonnull Long phoneSpecId,
												   @Nonnull String from, @Nonnull String to) throws BusinessExceptionWithoutRollback {
		PhoneNumberPoolAppServiceMessagesBundle messages = LocaleUtils.getMessages(PhoneNumberPoolAppServiceMessagesBundle.class);
		checkArgument(!from.contains("X"), messages.starterNumberContainsUnacceptableSymbols());
		checkArgument(!to.contains("X"), messages.lastNumberContainsUnacceptableSymbols());
		String stringFrom = from.replaceAll("[^\\d.]", "");
		String stringTo = to.replaceAll("[^\\d.]", "");
		long longFrom = Long.parseLong(stringFrom);
		long longTo = Long.parseLong(stringTo);
		long count = longTo - longFrom + 1;
		checkArgument(count >= 1, messages.startValueIsBiggerThanLastValue());

		int digitLength = stringFrom.length();
		checkArgument(digitLength <= 15, messages.quantityDigitsOfPhoneNumberCanNotBeMoreThan());

		String maskFrom = from.replaceAll("[\\d.]", "X");
		String maskTo = to.replaceAll("[\\d.]", "X");
		checkArgument(maskFrom.equals(maskTo), messages.startValueAndEndValueHaveDifferentFormat());

		// проверим, что количество номеров не выходит за максимально допустимое кол-во
		String maxNumber = System.getProperty("box.nri.logicalresoupce.phone.creation-limit");
		int max = NumberUtils.toInt(maxNumber, 10000);
		checkArgument(count <= max, messages.couldNotCreatePhoneNumbersBecauseAmountOfPhoneNumbersMoreThan()
				+ max + " (" + messages.total() + count + ")");

		Map<String, String> numbers = new HashMap<>();
		for (Long numberLong = Long.parseLong(stringFrom); numberLong <= Long.parseLong(stringTo); numberLong++) {
			numbers.put(numberLong.toString(), generateNumber(numberLong, maskFrom, digitLength));
		}

		PhoneNumberPool pool = repository.findOne(poolId);
		PhoneNumberSpecification spec = phoneNumberSpecRepository.findOne(phoneSpecId);

		List<String> allNotDeletedPhoneDigits = phoneNumberService.findAllNotDeletedPhoneDigits();
		Collection<String> numberDigits = numbers.keySet();
		numberDigits = filter(numberDigits, number -> !allNotDeletedPhoneDigits.contains(number));
		checkArgument(!numberDigits.isEmpty(), messages.numbersIsAlreadyExist());

		for (String numberString : numberDigits) {
			PhoneNumber phoneNumber = new PhoneNumber(idSequence.nextValue(PhoneNumber.class));
			phoneNumber.setName(numbers.get(numberString));
			phoneNumber.setType(LogicalResourceType.PHONE_NUMBER);
			phoneNumber.setState(PhoneNumberState.AVAILABLE);
			phoneNumber.setStateChangeDate(Date.from(Instant.now()));
			phoneNumber.setPool(pool);

			PhoneNumberSpecificationInstance specInst = factory.createInstance(spec, PhoneNumberSpecificationInstance.class,
					MetadataUnit.generateId());

			phoneNumber.setSpecInstance(specInst);
			specInst.setPhoneNumber(phoneNumber);

			pool.getPhoneNumbers().add(phoneNumber);
		}
		repository.save(pool);
		return translator.translate(pool);
	}
}
