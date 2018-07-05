package ru.argustelecom.box.env.contractor;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Supplier;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class SupplierDtoTranslator implements DefaultDtoTranslator<SupplierDto, Supplier> {
	@Override
	public SupplierDto translate(Supplier supplier) {
		//@formatter:off
		return SupplierDto.builder()
				.id(supplier.getId())
				.name(supplier.getObjectName())
				.brandName(supplier.getParty().getBrandName())
				.legalName(supplier.getParty().getLegalName())
				.partyTypeName(supplier.getParty().getTypeInstance().getType().getObjectName())
				.build();
		//@formatter:on
	}
}