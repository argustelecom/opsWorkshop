package ru.argustelecom.box.nri.logicalresources.phone.model;

import org.apache.commons.lang3.StringUtils;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.logicalresources.phone.model.nls.PhoneNumberEntityListenerMessagesBundle;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import static ru.argustelecom.system.inf.utils.CheckUtils.checkArgument;

/**
 * Слушатель телефонного номера
 * Created by s.kolyada on 16.01.2018.
 */
public class PhoneNumberEntityListener {

	/**
	 * При создании/обновлении сущности производить пересчёт параметров
	 */
	@PrePersist
	@PreUpdate
	public void recalculate(PhoneNumber phoneNumber) {
		checkArgument(phoneNumber != null, LocaleUtils.getMessages(PhoneNumberEntityListenerMessagesBundle.class).phoneNumberIsEmpty());

		// пересчитываем цифровое представление номера
		String name = phoneNumber.getName();
		checkArgument(StringUtils.isNotBlank(name), LocaleUtils.getMessages(PhoneNumberEntityListenerMessagesBundle.class).phoneNumberNameIsEmpty());

		String digitalName = name.replaceAll("\\D","");
		checkArgument(StringUtils.isNotBlank(digitalName),LocaleUtils.getMessages(PhoneNumberEntityListenerMessagesBundle.class).phoneNumberWithoutNumbers());

		phoneNumber.setDigits(digitalName);
	}
}
