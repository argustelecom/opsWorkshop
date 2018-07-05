package ru.argustelecom.box.env.customer;

import java.util.Date;

import javax.inject.Inject;

import ru.argustelecom.box.env.person.PersonDataAppService;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class IndividualDataAppService extends CustomerDataAppService {

	private static final long serialVersionUID = -8112019703690115531L;

	@Inject
	private PersonDataAppService pdAddService;

	public void renamePerson(Long personId, String prefix, String firstName, String secondName, String lastName,
			String suffix) {
		// TODO [Permission] Проверить права на редактирование карточки физ. клиента
		pdAddService.renamePerson(personId, prefix, firstName, secondName, lastName, suffix);
	}

	public void editPersonData(Long personId, String note) {
		// TODO [Permission] Проверить права на редактирование карточки физ. клиента
		pdAddService.editPersonData(personId, note);
	}

}