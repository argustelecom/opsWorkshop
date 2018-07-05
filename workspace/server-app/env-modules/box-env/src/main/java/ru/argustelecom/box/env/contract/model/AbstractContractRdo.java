package ru.argustelecom.box.env.contract.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.AddressRdo;
import ru.argustelecom.box.env.party.model.role.CustomerRdo;
import ru.argustelecom.box.env.report.api.data.ReportData;

@Getter
@Setter
public class AbstractContractRdo extends ReportData {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone = "Europe/Moscow")
	private Date validFrom;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone = "Europe/Moscow")
	private Date validTo;

	private String documentNumber;
	private CustomerRdo customer;
	private AddressRdo address;
	private Map<String, String> properties;

	public AbstractContractRdo(Long id, Date validFrom, Date validTo, String documentNumber, CustomerRdo customer,
			AddressRdo address, Map<String, String> properties) {
		super(id);
		this.validFrom = translate(validFrom);
		this.validTo = translate(validTo);
		this.documentNumber = documentNumber;
		this.customer = customer;
		this.address = address;
		this.properties = properties;
	}

}