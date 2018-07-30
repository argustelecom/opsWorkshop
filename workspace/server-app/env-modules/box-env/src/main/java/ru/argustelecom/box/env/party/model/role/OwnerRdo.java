package ru.argustelecom.box.env.party.model.role;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.party.model.CompanyRdo;
import ru.argustelecom.box.env.party.model.PartyRoleRdo;

@Getter
@Setter
public class OwnerRdo extends PartyRoleRdo {

	private Map<String, String> properties;

	public OwnerRdo(Long id, CompanyRdo company, Map<String, String> properties) {
		super(id, null, company);
		this.properties = properties;
	}

}