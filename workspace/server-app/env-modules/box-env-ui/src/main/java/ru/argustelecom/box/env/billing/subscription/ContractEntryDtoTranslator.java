package ru.argustelecom.box.env.billing.subscription;

import javax.inject.Inject;

import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.pricing.ProductOfferingDtoTranslator;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@DtoTranslator
public class ContractEntryDtoTranslator implements DefaultDtoTranslator<ContractEntryDto, ContractEntry> {

	@Inject
	private ProductOfferingDtoTranslator productOfferingDtoTr;

	@Override
	public ContractEntryDto translate(ContractEntry entry) {
		entry = EntityManagerUtils.initializeAndUnproxy(entry);
		ProductOffering productOffering = entry instanceof ProductOfferingContractEntry
				? ((ProductOfferingContractEntry) entry).getProductOffering() : null;
		return new ContractEntryDto(entry.getId(), productOfferingDtoTr.translate(productOffering));
	}

}