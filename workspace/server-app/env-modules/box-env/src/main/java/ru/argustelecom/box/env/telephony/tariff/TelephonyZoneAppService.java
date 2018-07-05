package ru.argustelecom.box.env.telephony.tariff;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class TelephonyZoneAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TelephonyZoneRepository telephonyZoneRp;

	public TelephonyZone create(String name, String description) {
		return telephonyZoneRp.create(name, description);
	}

	public TelephonyZone edit(Long telephonyZoneId, String name, String description) {
		checkRequiredArgument(telephonyZoneId, "TelephonyZoneId");
		checkRequiredArgument(name, "Name");

		TelephonyZone telephonyZone = em.find(TelephonyZone.class, telephonyZoneId);
		telephonyZone.setName(name);
		telephonyZone.setDescription(description);
		return telephonyZone;
	}

	public void remove(Long telephonyZoneId) {
		checkRequiredArgument(telephonyZoneId, "TelephonyZoneId");

		telephonyZoneRp.remove(em.find(TelephonyZone.class, telephonyZoneId));
	}

	/**
	 * Возвращает список всех {@linkplain TelephonyZone зон телефонной нумерации}.
	 */
	public List<TelephonyZone> findAll() {
		return telephonyZoneRp.findAll();
	}

	private static final long serialVersionUID = -2869762760461198194L;

}