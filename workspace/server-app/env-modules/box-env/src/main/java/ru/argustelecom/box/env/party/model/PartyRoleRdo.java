package ru.argustelecom.box.env.party.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.api.data.ReportData;

@Getter
@Setter
public class PartyRoleRdo extends ReportData {

	private PersonRdo person;
	private CompanyRdo company;

	public PartyRoleRdo(Long id, PersonRdo person, CompanyRdo company) {
		super(id);
		this.person = person;
		this.company = company;
	}

}
