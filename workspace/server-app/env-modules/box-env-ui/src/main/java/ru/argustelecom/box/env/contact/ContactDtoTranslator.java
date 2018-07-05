package ru.argustelecom.box.env.contact;

import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.exception.SystemException;

@DtoTranslator
public class ContactDtoTranslator {

	public ContactDto translate(Contact<?> contact) {
		if (contact instanceof PhoneContact)
			return translateToPhone(contact);
		else if (contact instanceof EmailContact)
			return translateToEmail(contact);
		else if (contact instanceof SkypeContact)
			return translateToSkype(contact);
		else if (contact instanceof CustomContact)
			return translateToCustom(contact);
		else
			throw new SystemException("Unsupported contact type");
	}

	private CustomContactDto translateToCustom(Contact<?> contact) {
		CustomContact customContact = (CustomContact) contact;
		//@formatter:off
		return CustomContactDto.builder()
				.id(customContact.getId())
				.name(customContact.getObjectName())
				.value(customContact.getValue())
				.type(customContact.getType())
				.category(customContact.getType().getCategory())
				.comment(customContact.getComment())
			.build();
		//@formatter:on
	}

	private SkypeContactDto translateToSkype(Contact<?> contact) {
		SkypeContact skypeContact = (SkypeContact) contact;
		//@formatter:off
		return SkypeContactDto.builder()
				.id(skypeContact.getId())
				.name(skypeContact.getObjectName())
				.value(skypeContact.getValue().value())
				.type(skypeContact.getType())
				.category(skypeContact.getType().getCategory())
				.comment(skypeContact.getComment())
			.build();
		//@formatter:on
	}

	private EmailContactDto translateToEmail(Contact<?> contact) {
		EmailContact emailContact = (EmailContact) contact;
		//@formatter:off
		return EmailContactDto.builder()
				.id(emailContact.getId())
				.name(emailContact.getObjectName())
				.value(emailContact.getValue().value())
				.type(emailContact.getType())
				.category(emailContact.getType().getCategory())
				.comment(emailContact.getComment())
			.build();
		//@formatter:on
	}

	private PhoneContactDto translateToPhone(Contact<?> contact) {
		PhoneContact phoneContact = (PhoneContact) contact;
		//@formatter:off
		return PhoneContactDto.builder()
				.id(phoneContact.getId())
				.name(phoneContact.getObjectName())
				.value(phoneContact.getValue().value())
				.type(phoneContact.getType())
				.category(phoneContact.getType().getCategory())
				.comment(phoneContact.getComment())
			.build();
		//@formatter:on
	}

}