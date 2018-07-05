package ru.argustelecom.box.env.customer;

import javax.inject.Inject;

import ru.argustelecom.box.env.company.CompanyDataAppService;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class OrganizationDataAppService extends CustomerDataAppService {

	private static final long serialVersionUID = -6246068230317317341L;

	@Inject
	private CompanyDataAppService cdAppService;

	public void renameCompany(Long companyId, String legalName, String brandName) {
		// TODO [Permission] Проверить права на редактирование карточки юр. клиента
		cdAppService.renameCompany(companyId, legalName, brandName);
	}

}