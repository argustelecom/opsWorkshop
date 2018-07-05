package ru.argustelecom.box.env.billing.invoice;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class PersonalAccountDtoTranslator implements DefaultDtoTranslator<PersonalAccountDto, PersonalAccount> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public PersonalAccountDto translate(PersonalAccount personalAccount) {
		return new PersonalAccountDto(personalAccount.getId(),
				businessObjectDtoTr.translate(personalAccount.getCustomer()), personalAccount.getThreshold());
	}
}