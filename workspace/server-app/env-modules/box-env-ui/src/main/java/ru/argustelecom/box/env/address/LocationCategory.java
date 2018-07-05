package ru.argustelecom.box.env.address;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.address.nls.LocationMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum LocationCategory {

	//@formatter:off
	COUNTRY  ("country",  "fa fa-globe"),
	REGION   ("region",   "icon-location_city"),
	DISTRICT ("district", "fa fa-globe"),
	STREET   ("street",   "fa fa-road"),
	LODGING  ("lodging",  "icon-airline_seat_flat");
	//@formatter:on

	@Getter
	private String keyword;
	@Getter
	private String icon;

	public static LocationCategory[] forCreationType() {
		return new LocationCategory[] { REGION, DISTRICT, STREET, LODGING };
	}

	public String getName() {
		LocationMessagesBundle messages = LocaleUtils.getMessages(LocationMessagesBundle.class);

		switch (this) {
		case COUNTRY:
			return messages.country();
		case REGION:
			return messages.region();
		case DISTRICT:
			return messages.district();
		case STREET:
			return messages.street();
		case LODGING:
			return messages.lodging();
		default:
			throw new SystemException("Unsupported LocationCategory");
		}
	}

}