package ru.argustelecom.box.env.commodity;

import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class CommodityTypeTreeNodeDtoTranslator {

	public CommodityTypeTreeNodeDto translate(CommodityType type) {
		CommodityTypeTreeNodeDto parent = type.getGroup() != null ? translate(type.getGroup()) : null;
		//@formatter:off
		return CommodityTypeTreeNodeDto.builder()
					.id(type.getId())
					.name(type.getObjectName())
					.type(CommodityTypeRef.determineType(type))
					.parent(parent)
				.build();
		//@formatter:on
	}

	public CommodityTypeTreeNodeDto translate(CommodityTypeGroup group) {
		CommodityTypeTreeNodeDto parent = group.getParent() != null ? translate(group.getParent()) : null;
		//@formatter:off
		return CommodityTypeTreeNodeDto.builder()
				.id(group.getId())
				.name(group.getObjectName())
				.type(CommodityTypeRef.determineType(group))
				.parent(parent)
				.build();
		//@formatter:on
	}

}