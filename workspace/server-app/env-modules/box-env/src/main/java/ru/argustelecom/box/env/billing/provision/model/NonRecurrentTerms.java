package ru.argustelecom.box.env.billing.provision.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
public class NonRecurrentTerms extends AbstractProvisionTerms {

	private static final long serialVersionUID = -1134520005908781599L;

	protected NonRecurrentTerms() {
	}

	public NonRecurrentTerms(Long id) {
		super(id);
	}

	@Override
	public final boolean isRecurrent() {
		return false;
	}

}
