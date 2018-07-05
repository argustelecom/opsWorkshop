package ru.argustelecom.box.env.billing.invoice;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.billing.invoice.model.JobDataType.REGULAR;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob.ChargeJobQuery;
import ru.argustelecom.box.env.billing.invoice.model.FilterAggData;
import ru.argustelecom.box.env.billing.invoice.model.JobDataType;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.lifecycle.impl.LifecycleHistoryRepository;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

@Repository
public class ChargeJobRepository implements Serializable {

	private static final long serialVersionUID = 4950733570197494067L;

	private static final String MEDIATION_SEQUENCE = "system.gen_charge_job_mediation_id";

	private static final String FIND_MOST_EARLY_FOR_SYNC = "ChargeJobRepository.findMostEarlyForSync";

	private static final String PREF_OF_REGULAR = "C";
	private static final String PREF_OF_RECHARGE = "RC";

	private static final String GET_NEXT_VALUE = "select nextval(:sequence_name)";
	private static final String GET_CURRENT_VALUE = "select currval(:sequence_name)";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private LifecycleHistoryRepository lifecycleHistoryRp;

	public ChargeJob create(JobDataType dataType, FilterAggData filter) {
		return create(generateMediationId(dataType), dataType, filter);
	}

	public ChargeJob create(String mediationId, JobDataType dataType, FilterAggData filter) {
		checkRequiredArgument(mediationId, "mediationId");
		checkNotNull(dataType);
		checkNotNull(filter);

		Date now = new Date();

		ChargeJob instance = new ChargeJob(idSequence.nextValue(ChargeJob.class), mediationId, dataType, now);

		instance.setFilter(filter);

		lifecycleHistoryRp.createRoutingHistory(instance, instance.getState(), now);

		em.persist(instance);

		return instance;
	}

	public ChargeJob find(String mediationId) {
		checkRequiredArgument(mediationId, "mediationId");

		ChargeJobQuery query = new ChargeJobQuery();
		query.and(query.mediationId().equal(mediationId));

		return query.getSingleResult(em, false);
	}

	// @formatter:off
	@NamedNativeQuery(name = FIND_MOST_EARLY_FOR_SYNC,
			query = "SELECT id\n" +
					"FROM system.charge_job\n" +
					"WHERE state = ANY (ARRAY ['SYNCHRONIZATION', 'PERFORMED_BILLING'])\n" +
					"ORDER BY creation_date\n" +
					"LIMIT 1")
	// @formatter:on
	public ChargeJob findMostEarlyForSynchronization() {
		try {
			BigInteger id = (BigInteger) em.createNamedQuery(FIND_MOST_EARLY_FOR_SYNC).getSingleResult();
			return em.find(ChargeJob.class, id.longValue());
		} catch (NoResultException nre) {
			// nothing to do
		}
		return null;
	}

	private String generateMediationId(JobDataType dataType) {
		checkNotNull(dataType);

		Long mediationLongId = Long.valueOf(em.createNativeQuery(GET_NEXT_VALUE)
				.setParameter("sequence_name", MEDIATION_SEQUENCE).getSingleResult().toString());
		return createMediationId(mediationLongId, dataType);
	}

	private String createMediationId(Long id, JobDataType dataType) {
		checkNotNull(id);
		checkNotNull(dataType);

		return (dataType.equals(REGULAR) ? PREF_OF_REGULAR : PREF_OF_RECHARGE) + id;
	}
}
