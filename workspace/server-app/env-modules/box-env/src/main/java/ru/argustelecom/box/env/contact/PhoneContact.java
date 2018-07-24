package ru.argustelecom.box.env.contact;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;

import ru.argustelecom.box.env.stl.PhoneNumber;

/**
 * Контак являющийся телефонным номером.
 */
@Entity
@Access(AccessType.FIELD)
public class PhoneContact extends Contact<PhoneNumber> {

	protected PhoneContact() {
	}

	protected PhoneContact(Long id) {
		super(id);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@AttributeOverride(name = "phoneNumber", column = @Column(name = "contact_data", nullable = false))
	public PhoneNumber getValue() {
		return super.getValue();
	}

	private static final long serialVersionUID = 642089086462910971L;

}