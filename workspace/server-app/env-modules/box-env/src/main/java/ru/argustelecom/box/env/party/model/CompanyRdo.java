package ru.argustelecom.box.env.party.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRdo extends PartyRdo {

	private String legalName;
	private String brandName;

	@Builder
	public CompanyRdo(Long id, String legalName, String brandName) {
		super(id);
		this.legalName = legalName;
		this.brandName = brandName;
	}

}