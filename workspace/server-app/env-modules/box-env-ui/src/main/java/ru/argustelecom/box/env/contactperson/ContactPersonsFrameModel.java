package ru.argustelecom.box.env.contactperson;

import java.io.Serializable;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.contractperson.ContactPersonDataAppService;
import ru.argustelecom.box.env.party.model.Company;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "contactPersonsFM")
@PresentationModel
public class ContactPersonsFrameModel implements Serializable {

	private static final long serialVersionUID = -2107025603575164299L;

	@Inject
	private ContactPersonDataAppService cpdAppService;

	@Inject
	private ContactPersonsDtoTranslator cpDtoTranslator;

	private Company company;

	private ContactPersonsDto cpDto;

	public void preRender(Company company) {
		if (!Objects.equals(this.company, company)) {
			this.company = company;
			refresh();
		}
	}

	public Callback<ContactPersonDataDto> getCallback() {
		return instance -> {
			ContactPersonDataDto old = cpDto.getValues().stream().filter(cp -> cp.equals(instance)).findAny()
					.orElse(null);
			if (old != null)
				cpDto.getValues().remove(old);

			cpDto.getValues().add(instance);
		};
	}

	public void remove(ContactPersonDataDto contactPersonDataDto) {
		cpdAppService.removeContactPerson(contactPersonDataDto.getContactPersonId());
		cpDto.getValues().remove(contactPersonDataDto);
	}

	private void refresh() {
		cpDto = cpDtoTranslator.translate(company.getContactPersons());
	}

	public Long getCompanyId() {
		return company.getId();
	}

	public ContactPersonsDto getCpDto() {
		return cpDto;
	}

}