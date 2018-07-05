package ru.argustelecom.box.env.customer;

import javax.inject.Inject;

import ru.argustelecom.box.env.contact.ContactDtoTranslator;
import ru.argustelecom.box.env.contact.EmailContactDto;
import ru.argustelecom.box.env.party.model.Company;
import ru.argustelecom.box.env.party.model.role.Organization;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class OrganizationDataDtoTranslator {

	@Inject
	private ContactDtoTranslator contactDtoTr;

	public OrganizationDataDto translate(Organization organization) {
		Company company = (Company) organization.getParty();
		EmailContactDto mainEmail = organization.getMainEmail() != null
				? (EmailContactDto) contactDtoTr.translate(organization.getMainEmail()) : null;

		//@formatter:off
		return OrganizationDataDto.builder()

			// Собственные данные физ. клиента (Organization)
			.customerId(organization.getId())
			.typeName(organization.getTypeInstance().getType().getObjectName())
			.vip(organization.isVip())
			.mainEmail(mainEmail)

			// Данные персоны (Company)
			.companyId(company.getId())
			.legalName(company.getLegalName())
			.brandName(company.getBrandName())

		.build();
		//@formatter:on
	}

}