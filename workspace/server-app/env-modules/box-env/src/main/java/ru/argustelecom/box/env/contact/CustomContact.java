package ru.argustelecom.box.env.contact;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Контакт специального вида.
 */
@Entity
@Access(AccessType.FIELD)
public class CustomContact extends Contact<String> {

	protected CustomContact() {
	}

	protected CustomContact(Long id) {
		super(id);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "contact_data")
	public String getValue() {
		return super.getValue();
	}

	private static final long serialVersionUID = -1677916299785046714L;
}