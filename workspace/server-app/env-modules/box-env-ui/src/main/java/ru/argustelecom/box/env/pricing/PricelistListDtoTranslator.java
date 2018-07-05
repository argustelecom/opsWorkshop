package ru.argustelecom.box.env.pricing;

import java.util.stream.Collectors;

import javax.inject.Inject;

import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.CommonPricelist;
import ru.argustelecom.box.env.pricing.model.CustomPricelist;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class PricelistListDtoTranslator implements DefaultDtoTranslator<PricelistListDto, AbstractPricelist> {
	@Inject
	private CustomerPricelistListDtoTranslator customerPricelistListDtoTranslator;

	@Inject
	private BusinessObjectDtoTranslator ownerDtoTr;

	@Override
	public PricelistListDto translate(AbstractPricelist pricelist) {
		return PricelistListDto.builder().id(pricelist.getId()).name(pricelist.getObjectName())
				.state(pricelist.getState().getName()).validFrom(pricelist.getValidFrom())
				.validTo(pricelist.getValidTo())
				.segmentNames(pricelist instanceof CommonPricelist
						? ((CommonPricelist) pricelist).getCustomerSegments().stream()
								.map(CustomerSegment::getObjectName).collect(Collectors.toList())
						: null)
				.customerDto(pricelist instanceof CustomPricelist
						? customerPricelistListDtoTranslator.translate(((CustomPricelist) pricelist).getCustomer())
						: null)
				.owner(ownerDtoTr.translate(pricelist.getOwner()))
				.build();
	}
}