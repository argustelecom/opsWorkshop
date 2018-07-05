package ru.argustelecom.box.env.pricing;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class PricelistDtoTranslator implements DefaultDtoTranslator<PricelistDto, AbstractPricelist> {

	@Override
	public PricelistDto translate(AbstractPricelist pricelist) {
		return new PricelistDto(pricelist.getId(), pricelist.getObjectName());
	}

}