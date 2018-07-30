package ru.argustelecom.box.env.party.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.api.data.ReportData;

@Getter
@Setter
public class PartyRdo extends ReportData {

	public PartyRdo(Long id) {
		super(id);
	}

}