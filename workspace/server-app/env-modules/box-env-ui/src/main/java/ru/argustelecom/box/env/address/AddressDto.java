package ru.argustelecom.box.env.address;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = { "building", "lodgingId" })
public class AddressDto {

	private BuildingDto building;
	private Long lodgingId;
	private LocationTypeDto lodgingType;
	private String lodgingNumber;
	private String fullName;
	private Long coverageId;
	private String coverageStateName;
	private String coverageStateColor;
	private String coverageNote;

	@Builder
	public AddressDto(BuildingDto building, Long lodgingId, LocationTypeDto lodgingType, String lodgingNumber,
			String fullName, Long coverageId, String coverageStateName, String coverageStateColor,
			String coverageNote) {
		this.building = building;
		this.lodgingId = lodgingId;
		this.lodgingType = lodgingType;
		this.lodgingNumber = lodgingNumber;
		this.fullName = fullName;
		this.coverageId = coverageId;
		this.coverageStateName = coverageStateName;
		this.coverageStateColor = coverageStateColor;
		this.coverageNote = coverageNote;
	}

}