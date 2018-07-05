package ru.argustelecom.box.env.pricing;

import static ru.argustelecom.box.env.billing.provision.ProvisionTermsDto.ProvisionTermsType.RECURRENT;

import java.util.Optional;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.provision.ProvisionTermsDto;
import ru.argustelecom.box.env.measure.MeasureUnitDto;
import ru.argustelecom.box.env.privilege.model.PrivilegeType;
import ru.argustelecom.box.env.product.ProductDto;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodUnit;

@Getter
@Setter
@NoArgsConstructor
public class ProductOfferingEditDto {

	private Long id;
	private ProductDto product;
	private ProvisionTermsDto provisionTerms;
	private Long amount;
	private MeasureUnitDto measureUnit;
	private PeriodUnit periodUnit;
	private Money price;
	private PrivilegeType privilegeType;
	private Integer privilegeAmount;
	private PeriodUnit privilegeUnit;

	@Builder
	public ProductOfferingEditDto(Long id, ProductDto product, ProvisionTermsDto provisionTerms, Long amount,
			MeasureUnitDto measureUnit, PeriodUnit periodUnit, Money price, PrivilegeType privilegeType,
			Integer privilegeAmount, PeriodUnit privilegeUnit) {
		this.id = id;
		this.product = product;
		this.provisionTerms = provisionTerms;
		this.amount = amount;
		this.measureUnit = measureUnit;
		this.periodUnit = periodUnit;
		this.price = price;
		this.privilegeType = privilegeType;
		this.privilegeAmount = privilegeAmount;
		this.privilegeUnit = privilegeUnit;
	}

	public boolean canHavePrivilege() {
		return RECURRENT.equals(Optional.ofNullable(provisionTerms).map(ProvisionTermsDto::getType).orElse(null));
	}

}