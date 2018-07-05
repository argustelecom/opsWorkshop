package ru.argustelecom.box.env.billing.mediation;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang.StringUtils.stripToEmpty;
import static ru.argustelecom.box.env.billing.mediation.AggregateCallsByUsageInvoiceQr.AGGREGATE_CALLS_BY_USAGE_INVOICE_QR_MAPPER;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;

import ru.argustelecom.box.env.billing.account.PersonalAccountRepository;
import ru.argustelecom.box.env.billing.account.model.Reserve;
import ru.argustelecom.box.env.billing.invoice.UsageInvoiceSettingsRepository;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceEntryData;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceSettings;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

/**
 * Сервис для связывания фактов использования телефонии с инвойсами.
 */
@DomainService
public class AssociateUsageInvoicesWithCallsService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private UsageInvoiceSettingsRepository usageInvoiceSettingsRp;

	@Inject
	private PersonalAccountRepository personalAccountRp;

	private static final String ASSOCIATE_USAGE_INVOICES_WITH_CALLS_FUNCTION = "AssociateUsageInvoicesWithCallsService.associate";

	/**
	 * Осуществляет связывание фактов использования с инвойсами.
	 *
	 * @param invoices
	 *            список инвойсов, для которых необходимо выполнить связывание.
	 * @param job
	 *            джоб в рамках которого выполняется работа. Указывается для перетарификации.
	 * @return возвращает факты использования в аргегированном по инвойсам виде.
	 */
	//@formatter:off
	@NamedNativeQuery(name = ASSOCIATE_USAGE_INVOICES_WITH_CALLS_FUNCTION,
			resultSetMapping = AGGREGATE_CALLS_BY_USAGE_INVOICE_QR_MAPPER,
			query = "SELECT * FROM system.associate_invoices_with_calls(:invoicesIds, :rechargeJobId)")
	//@formatter:on
	public List<AggregateCallsByUsageInvoiceQr> associate(List<UsageInvoice> invoices, ChargeJob job) {

		String ids = concatArrayOfIds(invoices);

		TypedQuery<AggregateCallsByUsageInvoiceQr> query = em
				.createNamedQuery(ASSOCIATE_USAGE_INVOICES_WITH_CALLS_FUNCTION, AggregateCallsByUsageInvoiceQr.class);

		query.setParameter("invoicesIds", ids);
		query.setParameter("rechargeJobId", ofNullable(job).map(ChargeJob::getMediationId).orElse(StringUtils.EMPTY));

		return query.getResultList();
	}

	/**
	 * Создаёт {@linkplain ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceEntryData позиции инвойса}. По
	 * фактам использования.
	 *
	 * @param invoice
	 *            инвойс, в котором нужно учесть факты использования.
	 * @param calls
	 *            вызовы, по которым нужно создать позиции в инвойсе.
	 */
	public void addEntriesTo(UsageInvoice invoice, List<AggregateCallsByUsageInvoiceQr> calls) {
		//@formatter:off
		calls.forEach(newInvoiceEntry ->
				invoice.addEntry(
						UsageInvoiceEntryData
								.builder()
								.resourceNumber(newInvoiceEntry.getResourceNumber())
								.telephonyZoneId(newInvoiceEntry.getTelephonyZoneId())
								.tariffId(newInvoiceEntry.getTariffId())
								.amount(newInvoiceEntry.getAmount())
								.build()
				));
		//@formatter:on

		boolean invoiceNotClosed = !InvoiceState.CLOSED.equals(invoice.getState());

		if (invoiceNotClosed) {
			UsageInvoiceSettings settings = usageInvoiceSettingsRp.find();
			if (settings.isReserveFunds()) {
				Reserve reserve = invoice.getReserve();
				if (reserve != null) {
					personalAccountRp.removeReserve(reserve);
				}
				reserve = personalAccountRp.createReserve(invoice.getPersonalAccount(), invoice.getTotalPrice());
				invoice.attachReserve(reserve, settings);
			}
		}
	}

	private static final String UNTIE_INVOICES_FROM_CALLS = "AssociateUsageInvoicesWithCallsService.untieInvoicesFromCalls";

	/**
	 * Удаляет все связки переданных инвойсов с фактами использования.
	 *
	 * @param invoices
	 *            инвойсы, которые нужно отвязать от фактов использования.
	 */
	@NamedNativeQuery(name = UNTIE_INVOICES_FROM_CALLS, query = "DELETE FROM system.usage_invoice_rated_outgoing_call WHERE usage_invoice_id = any (CAST(:invoice_ids AS BIGINT []))")
	public void untieInvoicesFromCalls(List<UsageInvoice> invoices) {
		String ids = concatArrayOfIds(invoices);
		em.createNamedQuery(UNTIE_INVOICES_FROM_CALLS).setParameter("invoice_ids", ids).executeUpdate();
	}

	private static final String TIE_NEW_INVOICE_WITH_CALLS = "AssociateUsageInvoicesWithCallsService.tieNewInvoiceWithCalls";

	/**
	 * Привязываеь все факты использования с переданным инвойсом.
	 *
	 * @param invoice
	 *            новый инвойс, к которому нужно привязать факты использования.
	 * @param calls
	 *            факты использования
	 */
	//@formatter:off
	@NamedNativeQuery(name = TIE_NEW_INVOICE_WITH_CALLS,
			query = "UPDATE system.usage_invoice_rated_outgoing_call\n" +
					"SET usage_invoice_id = :new_invoice_id\n" +
					"WHERE call_id in (SELECT iroc.call_id\n" +
					"                  FROM\n" +
					"                    system.rated_outgoing_calls roc\n" +
					"                    INNER JOIN system.usage_invoice_rated_outgoing_call iroc ON roc.call_id = iroc.call_id\n" +
					"                  WHERE iroc.usage_invoice_id = ANY (CAST(:old_invoice_ids as BIGINT [])))")
	//@formatter:on
	public void tieNewInvoiceWithCalls(UsageInvoice invoice, List<AggregateCallsByUsageInvoiceQr> calls) {
		String ids = calls.stream()
				.filter(i -> i.getInvoiceId() != null)
				.map(s -> s.getInvoiceId().toString())
				.collect(joining(","));

		ids = "{" + stripToEmpty(ids) + "}";
		em.createNamedQuery(TIE_NEW_INVOICE_WITH_CALLS).setParameter("new_invoice_id", invoice.getId())
				.setParameter("old_invoice_ids", ids).executeUpdate();
	}

	private static final String MARK_CALLS_LIKE_SUITABLE = "AssociateUsageInvoicesWithCallsService.markCallsLikeSuitable";

	/**
	 * Удаляет для факты использования, относящиеся к джобу из таблицы отсева.
	 * 
	 * @param job
	 *            джобу, факты использования которого надо удалить из отсева.
	 */
	//@formatter:off
	@NamedNativeQuery(name = MARK_CALLS_LIKE_SUITABLE,
			query = "DELETE FROM system.unsuitable_rated_outgoing_call\n" +
					"WHERE call_id IN (SELECT call_id\n" +
					"                  FROM system.rated_outgoing_calls\n" +
					"                  WHERE charge_job_id = :mediation_id)")
	//@formatter:on
	public void markCallsLikeSuitable(ChargeJob job) {
		em.createNamedQuery(MARK_CALLS_LIKE_SUITABLE).setParameter("mediation_id", job.getMediationId())
				.executeUpdate();
	}

	private static final String COUNT_CALLS_BY_JOB = "AssociateUsageInvoicesWithCallsService.countCallsByJob";

	/**
	 * Возвращает кол-во фактов использования, относящихся к конкретному заданию.
	 *
	 * @param job
	 *            задания, для которого нужно посчитать факты использования.
	 */
	@NamedNativeQuery(name = COUNT_CALLS_BY_JOB, query = "SELECT count(*) FROM system.rated_outgoing_calls WHERE charge_job_id = :mediation_id")
	public long countCallsByJob(ChargeJob job) {
		return ((BigInteger) em.createNamedQuery(COUNT_CALLS_BY_JOB).setParameter("mediation_id", job.getMediationId())
				.getSingleResult()).longValue();
	}

	private static final String COUNT_UNSUITABLE_CALLS_BY_JOB = "AssociateUsageInvoicesWithCallsService.countUnsuitableCallsByJob";

	/**
	 * Возвращает кол-во фактов использования попавших в остве, относящихся к конкретному заданию.
	 *
	 * @param job
	 *            задания, для которого нужно посчитать факты использования.
	 */
	@NamedNativeQuery(name = COUNT_UNSUITABLE_CALLS_BY_JOB, query = "SELECT count(*) FROM system.unsuitable_rated_outgoing_call WHERE job_id = :mediation_id")
	public long countUnsuitableCallsByJob(ChargeJob job) {
		return ((BigInteger) em.createNamedQuery(COUNT_UNSUITABLE_CALLS_BY_JOB)
				.setParameter("mediation_id", job.getMediationId()).getSingleResult()).longValue();
	}

	private String concatArrayOfIds(List<UsageInvoice> invoices) {
		//@formatter:off
		String ids = invoices.stream()
				.filter(i -> i.getId() != null)
				.map(s -> s.getId().toString())
				.collect(joining(","));

		ids = "{" + stripToEmpty(ids) + "}";
		//@formatter:on
		return ids;
	}

	private static final long serialVersionUID = -4680373328028253987L;

}