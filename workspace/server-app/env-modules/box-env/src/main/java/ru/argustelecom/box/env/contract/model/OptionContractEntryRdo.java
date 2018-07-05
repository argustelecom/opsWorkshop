package ru.argustelecom.box.env.contract.model;

import lombok.Builder;
import ru.argustelecom.box.env.address.model.AddressRdo;
import ru.argustelecom.box.env.report.api.data.ReportDataList;

public class OptionContractEntryRdo extends ContractEntryRdo {

	@Builder
	public OptionContractEntryRdo(Long id, String name, AddressRdo address, ReportDataList<AddressRdo> addresses) {
		super(id, name, address, addresses);
	}

}
