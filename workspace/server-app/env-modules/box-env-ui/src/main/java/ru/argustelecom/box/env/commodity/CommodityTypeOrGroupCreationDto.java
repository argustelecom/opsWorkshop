package ru.argustelecom.box.env.commodity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.dto.BusinessObjectDto;

@Getter
@Setter
@NoArgsConstructor
public class CommodityTypeOrGroupCreationDto {

	private String name;
	private String keyword;
	private CommodityTypeRef category;
	private BusinessObjectDto<CommodityTypeGroup> group;
	private String description;

}