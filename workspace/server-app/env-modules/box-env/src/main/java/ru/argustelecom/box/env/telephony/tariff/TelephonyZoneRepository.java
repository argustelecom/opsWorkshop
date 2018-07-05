package ru.argustelecom.box.env.telephony.tariff;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.cache.DirectoryCacheService;

@Repository
public class TelephonyZoneRepository implements Serializable {

	private static final long serialVersionUID = -3959031917554445007L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private DirectoryCacheService cacheSrv;

	public TelephonyZone create(String name, String description) {
		checkRequiredArgument(name, "Name");

		TelephonyZone telephonyZone = new TelephonyZone(idSequence.nextValue(TelephonyZone.class));
		telephonyZone.setName(name);
		telephonyZone.setDescription(description);
		em.persist(telephonyZone);
		return telephonyZone;
	}

	public void remove(TelephonyZone telephonyZone) {
		checkRequiredArgument(telephonyZone, "TelephonyZone");

		em.remove(telephonyZone);
	}

	public TelephonyZone findBy(String name) {
		return findAll().stream().filter(zone -> zone.getName().equals(name)).findFirst().orElse(null);
	}

	public List<TelephonyZone> findAll() {
		return cacheSrv.getDirectoryObjects(TelephonyZone.class);
	}

}
