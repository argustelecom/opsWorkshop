package ru.argustelecom.box.env.pricing;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class PricelistAttributesDtoTranslator implements DefaultDtoTranslator<PricelistAttributesDto, AbstractPricelist> {

	@Override
	public PricelistAttributesDto translate(AbstractPricelist pricelist) {
		//@formatter:off
		return PricelistAttributesDto.builder()
				.id(pricelist.getId())
				.name(pricelist.getObjectName())
				.validFrom(pricelist.getValidFrom())
				.validTo(pricelist.getValidTo())
				.state(pricelist.getState())
				.owner(pricelist.getOwner())
				.clazz(pricelist.getClass())
				.build();
		//@formatter:on
	}
}
