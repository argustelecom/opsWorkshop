package ru.argustelecom.box.env.address.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.api.data.ReportData;

@Getter
@Setter
public class AddressRdo extends ReportData {

	private String regionName;
	private String regionTreeName;
	private String streetName;
	private String buildingName;
	private String buildingNumber;
	private String buildingCorpus;
	private String buildingWing;
	private String lodgingName;
	private String fullName;

	@Builder
	public AddressRdo(Long id, String regionName, String regionTreeName, String streetName, String buildingName,
			String buildingNumber, String buildingCorpus, String buildingWing, String lodgingName, String fullName) {
		super(id);
		this.regionName = regionName;
		this.regionTreeName = regionTreeName;
		this.streetName = streetName;
		this.buildingName = buildingName;
		this.buildingNumber = buildingNumber;
		this.buildingCorpus = buildingCorpus;
		this.buildingWing = buildingWing;
		this.lodgingName = lodgingName;
		this.fullName = fullName;
	}

}