package ru.argustelecom.box.env.party.model;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRdo extends PartyRdo {

	private String legalName;
	private String brandName;

	@Builder
	public CompanyRdo(Long id, Map<String, String> properties, String legalName, String brandName) {
		super(id, properties);
		this.legalName = legalName;
		this.brandName = brandName;
	}

}