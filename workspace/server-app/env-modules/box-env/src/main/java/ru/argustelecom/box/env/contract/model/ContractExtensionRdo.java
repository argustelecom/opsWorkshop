package ru.argustelecom.box.env.contract.model;

import java.util.Date;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.AddressRdo;
import ru.argustelecom.box.env.party.model.role.CustomerRdo;

@Getter
@Setter
public class ContractExtensionRdo extends AbstractContractRdo {

	private ContractRdo contract;

	@Builder
	public ContractExtensionRdo(Long id, Date validFrom, Date validTo, String documentNumber, CustomerRdo customer,
			AddressRdo address, Map<String, String> properties, ContractRdo contract) {
		super(id, validFrom, validTo, documentNumber, customer, address, properties);
		this.contract = contract;
	}

}