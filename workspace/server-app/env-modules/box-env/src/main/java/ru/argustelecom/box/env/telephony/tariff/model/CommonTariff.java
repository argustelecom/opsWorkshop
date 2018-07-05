package ru.argustelecom.box.env.telephony.tariff.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
public class CommonTariff extends AbstractTariff {

	private static final long serialVersionUID = 7122520575044433898L;

	protected CommonTariff() {
	}

	public CommonTariff(Long id) {
		super(id);
	}
}
