package ru.argustelecom.box.env.pricing;

import static java.util.Optional.ofNullable;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.provision.ProvisionTermsDtoTranslator;
import ru.argustelecom.box.env.measure.MeasureUnitDtoTranslator;
import ru.argustelecom.box.env.pricing.model.MeasuredProductOffering;
import ru.argustelecom.box.env.pricing.model.PeriodProductOffering;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.product.ProductDtoTranslator;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ProductOfferingEditDtoTranslator extends AbstractProductOfferingDtoTranslator<ProductOfferingEditDto> {

	@Inject
	private ProductDtoTranslator productDtoTr;

	@Inject
	private ProvisionTermsDtoTranslator provisionTermsDtoTr;

	@Inject
	private MeasureUnitDtoTranslator measureUnitDtoTr;

	public ProductOfferingEditDto translate(ProductOffering productOffering) {
		//@formatter:off
		ProductOfferingEditDto productOfferingEditDto =
				ProductOfferingEditDto.builder()
					.id(productOffering.getId())
					.product(productDtoTr.translate(productOffering.getProductType()))
					.provisionTerms(provisionTermsDtoTr.translate(productOffering.getProvisionTerms()))
					.price(productOffering.getPrice())
				.build();
		//@formatter:on
		fillVolume(productOffering, productOfferingEditDto);
		return productOfferingEditDto;
	}

	@Override
	void fillPeriodVolume(PeriodProductOffering productOffering, ProductOfferingEditDto productOfferingEditDto) {
		productOfferingEditDto.setAmount((long) productOffering.getPeriod().getAmount());
		productOfferingEditDto.setPeriodUnit(productOffering.getPeriod().getUnit());
		productOfferingEditDto.setPrivilegeType(productOffering.getPrivilegeType());
		productOfferingEditDto.setPrivilegeAmount(
				ofNullable(productOffering.getPrivilegeDuration()).map(PeriodDuration::getAmount).orElse(null));
		productOfferingEditDto.setPrivilegeUnit(
				ofNullable(productOffering.getPrivilegeDuration()).map(PeriodDuration::getUnit).orElse(null));
	}

	@Override
	void fillMeasureVolume(MeasuredProductOffering productOffering, ProductOfferingEditDto productOfferingEditDto) {
		productOfferingEditDto.setAmount(productOffering.getMeasuredValue().getStoredValue());
		productOfferingEditDto
				.setMeasureUnit(measureUnitDtoTr.translate(productOffering.getMeasuredValue().getMeasureUnit()));
	}

}