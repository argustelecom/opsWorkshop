package ru.argustelecom.box.env.contact;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;

import ru.argustelecom.box.env.stl.SkypeLogin;

@Entity
@Access(AccessType.FIELD)
public class SkypeContact extends Contact<SkypeLogin> {

	protected SkypeContact() {
	}

	protected SkypeContact(Long id) {
		super(id);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@AttributeOverride(name = "skypeLogin", column = @Column(name = "contact_data", nullable = false))
	public SkypeLogin getValue() {
		return super.getValue();
	}

	private static final long serialVersionUID = -4146483517331135289L;
}
