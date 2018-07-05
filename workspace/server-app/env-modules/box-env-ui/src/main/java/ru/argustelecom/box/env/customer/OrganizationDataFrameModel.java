package ru.argustelecom.box.env.customer;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.party.model.role.Organization;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "organizationDataFM")
@PresentationModel
public class OrganizationDataFrameModel implements Serializable {

	private static final long serialVersionUID = -861189238626421653L;

	@Inject
	private OrganizationDataAppService odAppService;

	@Inject
	private OrganizationDataDtoTranslator odDtoTranslator;

	private OrganizationDataDto odDto;

	public void preRender(Organization organization) {
		if (odDto == null)
			odDto = odDtoTranslator.translate(organization);
	}

	public void handleSave() {
		//@formatter:off
		odAppService.renameCompany(
			odDto.getCompanyData().getCompanyId(),
			odDto.getCompanyData().getLegalName(),
			odDto.getCompanyData().getBrandName()
		);
		//@formatter:on
	}

	public OrganizationDataDto getOdDto() {
		return odDto;
	}

}