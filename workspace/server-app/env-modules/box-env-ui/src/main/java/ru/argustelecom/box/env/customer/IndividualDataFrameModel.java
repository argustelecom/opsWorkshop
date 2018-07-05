package ru.argustelecom.box.env.customer;

import static ru.argustelecom.box.env.contact.ContactCategory.EMAIL;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.contact.ContactDtoTranslator;
import ru.argustelecom.box.env.contact.EmailContactDto;
import ru.argustelecom.box.env.party.model.role.Individual;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "individualDataFM")
@PresentationModel
public class IndividualDataFrameModel implements Serializable {

	private static final long serialVersionUID = -8427823593002898299L;

	@Inject
	private IndividualDataAppService idAppService;

	@Inject
	private IndividualDataDtoTranslator idDtoTranslator;

	@Inject
	private ContactDtoTranslator contactDtoTr;

	private IndividualDataDto idDto;
	private List<EmailContactDto> emails;

	public void preRender(Individual individual) {
		if (idDto == null) {
			idDto = idDtoTranslator.translate(individual);
			initEmails(individual);
		}
	}

	public void handleSave() {
		//@formatter:off
		idAppService.renamePerson(
			idDto.getPersonData().getPersonId(),
			idDto.getPersonData().getPrefix(),
			idDto.getPersonData().getFirstName(),
			idDto.getPersonData().getSecondName(),
			idDto.getPersonData().getLastName(),
			idDto.getPersonData().getSuffix()
		);

		idAppService.editPersonData(
			idDto.getPersonData().getPersonId(),
			idDto.getPersonData().getNote()
		);
		//@formatter:on

		Long mainEmailId = idDto.getMainEmail() != null ? idDto.getMainEmail().getId() : null;
		idAppService.changeMainEmail(idDto.getCustomerId(), mainEmailId);
	}

	public List<EmailContactDto> getEmails() {
		return emails;
	}

	private void initEmails(Individual individual) {
		emails = individual.getParty().getContactInfo().getContacts().stream()
				.filter(contact -> contact.getType().getCategory().equals(EMAIL))
				.map(contact -> (EmailContactDto) contactDtoTr.translate(contact)).collect(Collectors.toList());
	}

	public IndividualDataDto getIdDto() {
		return idDto;
	}

}