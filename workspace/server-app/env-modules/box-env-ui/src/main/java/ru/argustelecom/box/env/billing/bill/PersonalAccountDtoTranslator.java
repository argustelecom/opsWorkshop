package ru.argustelecom.box.env.billing.bill;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class PersonalAccountDtoTranslator implements DefaultDtoTranslator<PersonalAccountDto, PersonalAccount> {
	@Override
	public PersonalAccountDto translate(PersonalAccount personalAccount) {
		return new PersonalAccountDto(personalAccount.getId(), personalAccount.getNumber());
	}
}
