package ru.argustelecom.box.env.commodity;

import java.util.Optional;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@DtoTranslator
public class CommodityTypeAttrDtoTranslator {

	@Inject
	private BusinessObjectDtoTranslator boTranslator;

	public CommodityTypeAttrDto translate(Identifiable identifiable) {
		Identifiable unproxyIdentifiable = EntityManagerUtils.initializeAndUnproxy(identifiable);
		if (identifiable instanceof CommodityType) {
			return translate((CommodityType) unproxyIdentifiable);
		} else if (identifiable instanceof CommodityTypeGroup) {
			return translate((CommodityTypeGroup) unproxyIdentifiable);
		} else {
			throw new SystemException(String.format("Unsupported class '%s'", identifiable.getClass().getSimpleName()));
		}
	}

	public CommodityTypeAttrDto translate(CommodityType type) {
		//@formatter:off
		return CommodityTypeAttrDto.builder()
				.id(type.getId())
				.name(type.getObjectName())
				.type(CommodityTypeRef.determineType(type))
				.parent(boTranslator.translate(type.getGroup()))
				.description(type.getDescription())
				.build();
		//@formatter:on
	}

	public CommodityTypeAttrDto translate(CommodityTypeGroup group) {
		BusinessObjectDto<CommodityTypeGroup> parent = Optional.ofNullable(group.getParent())
				.map(boTranslator::translate).orElse(null);
		//@formatter:off
		return CommodityTypeAttrDto.builder()
				.id(group.getId())
				.name(group.getObjectName())
				.type(CommodityTypeRef.determineType(group))
				.keyword(group.getKeyword())
				.parent(parent)
				.build();
		//@formatter:on
	}

}