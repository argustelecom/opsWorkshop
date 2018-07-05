package ru.argustelecom.box.env.contract.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.AddressRdo;
import ru.argustelecom.box.env.pricing.model.ProductOfferingRdo;
import ru.argustelecom.box.env.report.api.data.ReportDataList;

@Getter
@Setter
public class ProductOfferingContractEntryRdo extends ContractEntryRdo {

	private ProductOfferingRdo productOffering;

	@Builder
	public ProductOfferingContractEntryRdo(Long id, String name, ProductOfferingRdo productOffering, AddressRdo address,
			ReportDataList<AddressRdo> addresses) {
		super(id, name, address, addresses);
		this.productOffering = productOffering;
	}

}
