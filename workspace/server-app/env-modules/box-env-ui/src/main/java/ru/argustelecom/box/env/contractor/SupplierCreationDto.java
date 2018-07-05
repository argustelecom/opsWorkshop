package ru.argustelecom.box.env.contractor;

import lombok.Getter;
import lombok.Setter;

import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.party.model.PartyType;

@Getter
@Setter
public class SupplierCreationDto {
	private String legalName;
	private String brandName;
	private BusinessObjectDto<PartyType> type;
}
