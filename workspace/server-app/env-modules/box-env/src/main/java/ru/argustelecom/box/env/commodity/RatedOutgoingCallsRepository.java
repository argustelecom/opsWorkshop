package ru.argustelecom.box.env.commodity;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.findList;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.SqlResultSetMapping;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

@Repository
public class RatedOutgoingCallsRepository implements Serializable {

	private static final long serialVersionUID = 1024701469319864518L;

	private static final String FIND_CROSSING_SERVICES = "RatedOutgoingCallsRepository.findCrossingServices";
	private static final String FIND_CROSSING_SERVICES_BY_TARIFF = "RatedOutgoingCallsRepository.findCrossingServicesByTariff";
	private static final String FIND_CROSSING_SERVICES_WITH_TARIFF = "RatedOutgoingCallsRepository.findCrossingServicesWithTariff";
	private static final String SYNC = "RatedOutgoingCallsRepository.sync";
	private static final String RESTORE_CONTEXT = "RatedOutgoingCallsRepository.restoreServiceContext";

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	// @formatter:off
	@NamedNativeQuery(name = FIND_CROSSING_SERVICES,
			query = "SELECT DISTINCT roc.service_id\n" +
					"FROM rated_outgoing_calls roc\n" +
					"WHERE roc.call_date >= :date_from AND roc.call_date <= :date_to AND roc.service_id IN :service_ids"
	)
	// @formatter:on
	public List<Service> findCrossingServices(List<Long> serviceIds, Date dateFrom, Date dateTo) {
		checkRequiredArgument(serviceIds, "serviceIds");
		checkNotNull(dateFrom);
		checkNotNull(dateTo);

		List<BigInteger> ids = em.createNamedQuery(FIND_CROSSING_SERVICES).setParameter("date_from", dateFrom)
				.setParameter("date_to", dateTo).setParameter("service_ids", serviceIds).getResultList();
		return findList(em, Service.class, ids.stream().map(BigInteger::longValue).collect(toList()));
	}

	@SuppressWarnings("unchecked")
	// @formatter:off
	@NamedNativeQuery(name = FIND_CROSSING_SERVICES_BY_TARIFF,
			query = "SELECT DISTINCT roc.service_id\n" +
					"FROM rated_outgoing_calls roc\n" +
					"WHERE roc.call_date >= :date_from AND roc.call_date <= :date_to AND roc.tariff_id = :tariff_id"
	)
	// @formatter:on
	public List<Service> findCrossingServicesByTariff(Long tariffId, Date dateFrom, Date dateTo) {
		checkRequiredArgument(tariffId, "serviceIds");
		checkNotNull(dateFrom);
		checkNotNull(dateTo);

		List<BigInteger> ids = em.createNamedQuery(FIND_CROSSING_SERVICES_BY_TARIFF).setParameter("date_from", dateFrom)
				.setParameter("date_to", dateTo).setParameter("tariff_id", tariffId).getResultList();
		return findList(em, Service.class, ids.stream().map(BigInteger::longValue).collect(toList()));
	}

	@SuppressWarnings("unchecked")
	@NamedNativeQuery(name = FIND_CROSSING_SERVICES_WITH_TARIFF, query = "SELECT DISTINCT roc.service_id\n"
			+ "FROM rated_outgoing_calls roc\n"
			+ "WHERE roc.call_date >= :date_from AND roc.call_date <= :date_to AND roc.service_id IN :service_ids AND roc.tariff_id = :tariff_id")
	public List<Service> findCrossingServicesWithTariff(Long tariffId, Date dateFrom, Date dateTo,
			List<Long> serviceIds) {
		checkRequiredArgument(tariffId, "tariffId");
		checkRequiredArgument(serviceIds, "serviceIds");
		checkNotNull(dateFrom);
		checkNotNull(dateTo);

		List<BigInteger> ids = em.createNamedQuery(FIND_CROSSING_SERVICES_WITH_TARIFF)
				.setParameter("date_from", dateFrom).setParameter("service_ids", serviceIds)
				.setParameter("date_to", dateTo).setParameter("tariff_id", tariffId).getResultList();
		return findList(em, Service.class, ids.stream().map(BigInteger::longValue).collect(toList()));
	}

	// @formatter:off
	@NamedNativeQuery(name = SYNC, query = "SELECT system.synchronize(:charge_job_id)")
	// @formatter:on
	public int sync(String chargeJobId) {
		checkRequiredArgument(chargeJobId, "chargeJobId");
		Query query = em.createNamedQuery(SYNC);
		query.setParameter("charge_job_id", chargeJobId);

		Object result = query.getSingleResult();
		return Integer.parseInt(result.toString());
	}

	// @formatter:off
	@NamedNativeQuery(name = RESTORE_CONTEXT,
			resultSetMapping = RatedOutgoingCallsRechargeContext.RATED_OUTGOING_CALLS_DATA_MAPPER,
			query = "SELECT service_id, min(call_date) as min_call_date, max(call_date) as max_call_date\n" +
					"FROM system.rated_outgoing_calls\n" +
					"WHERE charge_job_id = :charge_job_id\n" +
					"GROUP BY service_id")
	// @formatter:on
	public List<RatedOutgoingCallsRechargeContext> restoreServiceContext(String chargeJobId) {
		checkRequiredArgument(chargeJobId, "chargeJobId");

		return em.createNamedQuery(RESTORE_CONTEXT, RatedOutgoingCallsRechargeContext.class)
				.setParameter("charge_job_id", chargeJobId).getResultList();
	}

}
