package ru.argustelecom.box.env.contract.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.model.role.Supplier;

@Getter
@AllArgsConstructor
public enum ContractRole {
	OWNER(Owner.class), CUSTOMER(Customer.class), SUPPLIER(Supplier.class);

	private Class<? extends PartyRole> partyRoleClass;

}
