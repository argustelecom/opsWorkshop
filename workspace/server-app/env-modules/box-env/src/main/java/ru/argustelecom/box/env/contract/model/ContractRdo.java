package ru.argustelecom.box.env.contract.model;

import java.util.Date;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.AddressRdo;
import ru.argustelecom.box.env.party.model.PartyRoleRdo;
import ru.argustelecom.box.env.party.model.role.CustomerRdo;

@Getter
@Setter
public class ContractRdo extends AbstractContractRdo {

	private PartyRoleRdo provider;
	private PartyRoleRdo broker;

	@Builder
	public ContractRdo(Long id, Date validFrom, Date validTo, String documentNumber, CustomerRdo customer,
			AddressRdo address, Map<String, String> properties, PartyRoleRdo provider, PartyRoleRdo broker) {
		super(id, validFrom, validTo, documentNumber, customer, address, properties);
		this.provider = provider;
		this.broker = broker;
	}

}