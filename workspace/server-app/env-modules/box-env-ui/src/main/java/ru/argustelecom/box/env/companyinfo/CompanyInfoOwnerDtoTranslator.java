package ru.argustelecom.box.env.companyinfo;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class CompanyInfoOwnerDtoTranslator implements DefaultDtoTranslator<CompanyInfoOwnerDto, Owner> {

	@Override
	public CompanyInfoOwnerDto translate(Owner owner) {
		//@formatter:off
		return CompanyInfoOwnerDto.builder()
					.id(owner.getId())
					.name(owner.getObjectName())
					.partyTypeName(owner.getParty().getTypeInstance() != null ? owner.getParty().getTypeInstance().getType().getObjectName() : null)
					.principal(owner.isPrincipal())
					.qrCodePattern(owner.getQrCodePattern())
					.taxRate(owner.getTaxRate())
				.build();
		//@formatter:on
	}

}