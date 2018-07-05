package ru.argustelecom.box.env.companyinfo;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.party.model.PartyType;

@Getter
@Setter
public class OwnerCreationDto {

	private BusinessObjectDto<PartyType> partyType;
	private String name;
	private int taxRate;
	private String qrCodePattern;
	private boolean principal;

}