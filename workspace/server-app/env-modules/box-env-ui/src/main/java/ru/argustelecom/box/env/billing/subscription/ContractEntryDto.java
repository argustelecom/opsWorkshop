package ru.argustelecom.box.env.billing.subscription;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.pricing.ProductOfferingDto;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
public class ContractEntryDto extends ConvertibleDto {

	private Long id;
	private ProductOfferingDto productOffering;

	public ContractEntryDto(Long id, ProductOfferingDto productOffering) {
		this.id = id;
		this.productOffering = productOffering;
	}

	@Override
	public Class<ContractEntryDtoTranslator> getTranslatorClass() {
		return ContractEntryDtoTranslator.class;
	}

	@Override
	public Class<ContractEntry> getEntityClass() {
		return ContractEntry.class;
	}

}