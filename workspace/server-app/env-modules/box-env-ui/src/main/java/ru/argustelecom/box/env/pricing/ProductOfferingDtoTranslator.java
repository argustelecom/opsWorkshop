package ru.argustelecom.box.env.pricing;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.pricing.model.MeasuredProductOffering;
import ru.argustelecom.box.env.pricing.model.PeriodProductOffering;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ProductOfferingDtoTranslator extends AbstractProductOfferingDtoTranslator<ProductOfferingDto>
		implements DefaultDtoTranslator<ProductOfferingDto, ProductOffering> {

	@Override
	public ProductOfferingDto translate(ProductOffering productOffering) {
		//@formatter:off
		ProductOfferingDto productOfferingDto =
				ProductOfferingDto.builder()
					.id(productOffering.getId())
					.productName(productOffering.getProductType().getObjectName())
					.pricelistName(productOffering.getPricelist().getObjectName())
					.provisionTermsName(productOffering.getProvisionTerms().getObjectName())
					.provisionTermsDescription(productOffering.getProvisionTerms().getDescription())
					.price(productOffering.getPrice())
					.priceWithoutTax(productOffering.getPriceWithoutTax())
					.currency(productOffering.getCurrency().getSymbol())
				.build();
		//@formatter:on
		fillVolume(productOffering, productOfferingDto);
		return productOfferingDto;
	}

	@Override
	void fillPeriodVolume(PeriodProductOffering productOffering, ProductOfferingDto productOfferingDto) {
		productOfferingDto.setAmount((long) productOffering.getPeriod().getAmount());
		productOfferingDto.setMeasureName(productOffering.getPeriod().getUnit().toString());
		productOfferingDto.setPrivilegeType(productOffering.getPrivilegeType());
		productOfferingDto.setPrivilegeDuration(productOffering.getPrivilegeDuration());
	}

	@Override
	void fillMeasureVolume(MeasuredProductOffering productOffering, ProductOfferingDto productOfferingDto) {
		productOfferingDto.setAmount(productOffering.getMeasuredValue().getStoredValue());
		productOfferingDto.setMeasureName(productOffering.getMeasuredValue().getMeasureUnit().getName());
	}

}