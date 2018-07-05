package ru.argustelecom.box.env.pricing;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.privilege.model.PrivilegeType;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodDuration;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class ProductOfferingDto extends ConvertibleDto {

	private Long id;
	private String productName;
	private String pricelistName;
	private String provisionTermsName;
	private String provisionTermsDescription;
	@Setter
	private Long amount;
	@Setter
	private String measureName;
	private Money price;
	private Money priceWithoutTax;
	private String currency;
	@Setter
	private PrivilegeType privilegeType;
	@Setter
	private PeriodDuration privilegeDuration;

	@Builder
	public ProductOfferingDto(Long id, String productName, String pricelistName, String provisionTermsName,
			String provisionTermsDescription, Long amount, String measureName, Money price, Money priceWithoutTax,
			String currency, PrivilegeType privilegeType, PeriodDuration privilegeDuration) {
		this.id = id;
		this.productName = productName;
		this.pricelistName = pricelistName;
		this.provisionTermsName = provisionTermsName;
		this.provisionTermsDescription = provisionTermsDescription;
		this.amount = amount;
		this.measureName = measureName;
		this.price = price;
		this.priceWithoutTax = priceWithoutTax;
		this.currency = currency;
		this.privilegeType = privilegeType;
		this.privilegeDuration = privilegeDuration;
	}

	@Override
	public Class<ProductOffering> getEntityClass() {
		return ProductOffering.class;
	}

	@Override
	public Class<ProductOfferingDtoTranslator> getTranslatorClass() {
		return ProductOfferingDtoTranslator.class;
	}

}