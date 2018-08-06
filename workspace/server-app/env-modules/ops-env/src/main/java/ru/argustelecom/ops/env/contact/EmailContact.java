package ru.argustelecom.ops.env.contact;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;

import ru.argustelecom.ops.env.stl.EmailAddress;

/**
 * Контакт являющийся адресом электронной почты.
 */
@Entity
@Access(AccessType.FIELD)
public class EmailContact extends Contact<EmailAddress> {

	protected EmailContact() {
	}

	public EmailContact(Long id) {
		super(id);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@AttributeOverride(name = "emailAddress", column = @Column(name = "contact_data", nullable = false))
	public EmailAddress getValue() {
		return super.getValue();
	}

	private static final long serialVersionUID = -7355983404407636154L;
}