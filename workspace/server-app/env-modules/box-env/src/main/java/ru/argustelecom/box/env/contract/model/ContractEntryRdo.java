package ru.argustelecom.box.env.contract.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.AddressRdo;
import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.env.report.api.data.format.ReportBandDef;

@Getter
@Setter
public class ContractEntryRdo extends ReportData {

	private String name;

	private AddressRdo address;

	@ReportBandDef(name = "Addresses")
	private ReportDataList<AddressRdo> addresses;

	public ContractEntryRdo(Long id, String name, AddressRdo address, ReportDataList<AddressRdo> addresses) {
		super(id);
		this.name = name;
		this.address = address;
		this.addresses = addresses;
	}

}