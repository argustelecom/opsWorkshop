package ru.argustelecom.ops.inf.pref;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.ops.inf.service.Repository;
import ru.argustelecom.system.inf.configuration.dbprefs.PrefTable;
import ru.argustelecom.system.inf.dataaccess.cache.CacheManagementService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

@Repository
public class PrefTableRepository implements Serializable {

	private static final String SENDER_NAME_KEY = "sender_name";
	private static final String CREATE_OR_SET_SENDER_NAME = "PrefTableRepository.createOrSetSenderName";
	private static final String GET_DB_VERSION = "PrefTableRepository.getDbVersion";
	private static final String DB_VERSION = "db_version";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CacheManagementService cacheManagementService;

	//@formatter:off
	@NamedNativeQuery(name = CREATE_OR_SET_SENDER_NAME,
			query = "INSERT INTO system.pref_table (pref_name, pref_value)\n" +
			"VALUES (:prefName, :prefValue)\n" +
			"ON CONFLICT (pref_name)\n" +
			"  DO UPDATE\n" +
			"    SET pref_value = EXCLUDED.pref_value")
	//@formatter:on
	public int createOrSetSenderName(String senderName) {
		int count = em.createNamedQuery(CREATE_OR_SET_SENDER_NAME).setParameter("prefName", SENDER_NAME_KEY)
				.setParameter("prefValue", senderName).executeUpdate();
		cacheManagementService.cleanAllCaches();
		return count;
	}

	public String getSenderName() {
		PrefTable prefTable = em.find(PrefTable.class, SENDER_NAME_KEY);
		return prefTable != null ? prefTable.getPrefValue() : null;
	}

	@SuppressWarnings("unchecked")
	@NamedNativeQuery(name = GET_DB_VERSION, query = "select pref_value from system.pref_table where pref_name = :prop")
	public String getDbVersion() {
		return (String) em.createNamedQuery(GET_DB_VERSION).setParameter("prop", DB_VERSION).getResultList().stream()
				.findFirst().orElse(null);
	}

	private static final long serialVersionUID = -7841129967343229381L;

}