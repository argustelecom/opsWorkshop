package ru.argustelecom.box.env.contract.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.ProductOffering;

/**
 * DTO для диалога создания позиции договора или доп. соглашения.
 */
@Getter
@Setter
@NoArgsConstructor
public class ContractEntryCreationDto {

	@Setter
	@Getter
	private BusinessObjectDto<AbstractPricelist> pricelist;
	private BusinessObjectDto<ProductOffering> productOffering;
	private BusinessObjectDto<Building> building;
	private BusinessObjectDto<LocationType> lodgingType;
	private String lodgingNumber;

}