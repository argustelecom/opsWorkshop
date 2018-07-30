package ru.argustelecom.ops.env.party;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.ops.env.party.nls.PartyMessagesBundle;
import ru.argustelecom.ops.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@Getter
@AllArgsConstructor(access = AccessLevel.MODULE)
public enum PartyCategory {

	//@formatter:off
	PERSON	("person", "Person.class", "fa fa-user"),
	COMPANY ("company", "Company.class", "fa fa-group");
	//@formatter:on

	private String keyword;

	private String clazz;

	private String icon;

	public String getName() {
		PartyMessagesBundle messages = LocaleUtils.getMessages(PartyMessagesBundle.class);

		switch (this) {
			case PERSON:
				return messages.categoryPerson();
			case COMPANY:
				return messages.categoryCompany();
			default:
				throw new SystemException("Unsupported PartyCategory");
		}
	}
}
