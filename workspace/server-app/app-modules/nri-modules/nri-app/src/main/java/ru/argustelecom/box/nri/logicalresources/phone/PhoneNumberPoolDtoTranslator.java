package ru.argustelecom.box.nri.logicalresources.phone;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Транслятор пула телефонных номеров
 * Created by s.kolyada on 31.10.2017.
 */
@DtoTranslator
public class PhoneNumberPoolDtoTranslator implements DefaultDtoTranslator<PhoneNumberPoolDto, PhoneNumberPool> {

	/**
	 * Транслятор сущности телефонного номера
	 */
	@Inject
	private PhoneNumberDtoTranslator phoneNumberDtoTranslator;

	/**
	 * Транслировать пул в ДТО
	 *
	 * @param businessObject сущность пула
	 * @return ДТО пула
	 */
	@Override
	public PhoneNumberPoolDto translate(PhoneNumberPool businessObject) {
		if (businessObject == null) {
			return null;
		}
		return PhoneNumberPoolDto.builder()
				.id(businessObject.getId())
				.name(businessObject.getName())
				.phoneNumbers(translatePhoneNumbers(businessObject.getPhoneNumbers()))
				.comment(businessObject.getComment())
				.build();
	}

	/**
	 * Ленивый транслятор
	 * @param pool пул
	 * @return ленивый дто
	 */
	public PhoneNumberPoolDto translateLazy(PhoneNumberPool pool) {
		if (pool == null) {
			return null;
		}
		return PhoneNumberPoolDto.builder()
				.id(pool.getId())
				.name(pool.getName())
				.comment(pool.getComment())
				.build();
	}

	/**
	 * Транслировать список телефонных номеров, принадлежащих пулу
	 *
	 * @param phoneNumbers список сущностей телефонных номеров
	 * @return список ДТО телефонных номеров
	 */
	private List<PhoneNumberDto> translatePhoneNumbers(List<PhoneNumber> phoneNumbers) {
		return phoneNumbers.stream().map(phoneNumberDtoTranslator::translate).collect(toList());
	}
}
