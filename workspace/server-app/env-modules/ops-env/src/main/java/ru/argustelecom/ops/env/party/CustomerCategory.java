package ru.argustelecom.ops.env.party;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.ops.env.party.nls.CustomerMessagesBundle;
import ru.argustelecom.ops.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CustomerCategory {

	//@formatter:off
	PERSON	("person", "Individual.class", "fa fa-user"),
	COMPANY ("company", "Organization.class", "fa fa-users");
	//@formatter:on

	private String keyword;

	private String clazz;

	private String icon;

	public String getName() {
		CustomerMessagesBundle messages = LocaleUtils.getMessages(CustomerMessagesBundle.class);

		switch (this) {
			case PERSON:
				return messages.categoryPerson();
			case COMPANY:
				return messages.categoryCompany();
			default:
				throw new SystemException("Unsupported CustomerCategory");
		}
	}
}