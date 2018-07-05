package ru.argustelecom.box.env.party.model.role;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.party.model.CompanyRdo;
import ru.argustelecom.box.env.party.model.PartyRoleRdo;
import ru.argustelecom.box.env.party.model.PersonRdo;

@Getter
@Setter
public class CustomerRdo extends PartyRoleRdo {

	private boolean vip;
	private Map<String, String> properties;

	@Builder
	public CustomerRdo(Long id, PersonRdo person, CompanyRdo company, boolean vip, Map<String, String> properties) {
		super(id, person, company);
		this.vip = vip;
		this.properties = properties;
	}

}