package ru.argustelecom.box.env.party;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.party.nls.PartyMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@Getter
@AllArgsConstructor(access = AccessLevel.MODULE)
public enum PartyTypeNodeRef {

	//@formatter:off
	ROOT 		("root", ""),
	PERSON 		("person", "fa fa-user"),
	COMPANY 	("company", "fa fa-group"),
	PARTY_TYPE  ("party_type", "");
	//@formatter:on

	private String keyword;
	private String icon;

	public String getName() {
		switch (this) {
		case ROOT:
			return "root";
		case PERSON:
			return LocaleUtils.getMessages(PartyMessagesBundle.class).categoryPerson();
		case COMPANY:
			return LocaleUtils.getMessages(PartyMessagesBundle.class).categoryCompany();
		case PARTY_TYPE:
			return "";
		default:
			throw new SystemException("Unsupported PartyTypeNodeRef");
		}
	}

}