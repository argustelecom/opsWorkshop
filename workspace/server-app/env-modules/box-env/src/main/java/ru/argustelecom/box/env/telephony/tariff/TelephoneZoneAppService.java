package ru.argustelecom.box.env.telephony.tariff;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class TelephoneZoneAppService implements Serializable {

	private static final long serialVersionUID = -4341749201717932052L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TelephonyZoneRepository telephoneZoneRp;


	public List<TelephonyZone> findAll() {
		return telephoneZoneRp.findAll();
	}
}
