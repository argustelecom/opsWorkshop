package ru.argustelecom.box.env.party.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.api.data.ReportData;

@Getter
@Setter
public class PartyRdo extends ReportData {

	private Map<String, String> properties;

	public PartyRdo(Long id, Map<String, String> properties) {
		super(id);
		this.properties = properties;
	}

}