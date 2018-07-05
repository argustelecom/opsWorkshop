package ru.argustelecom.box.env.commodity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.dto.BusinessObjectDto;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = { "id", "type" })
public class CommodityTypeAttrDto {

	private Long id;

	@Setter
	private String name;

	@Setter
	private String keyword;

	private CommodityTypeRef type;

	@Setter
	private BusinessObjectDto<CommodityTypeGroup> parent;

	@Setter
	private String description;

	public boolean isGroup() {
		return type.equals(CommodityTypeRef.GROUP);
	}

}