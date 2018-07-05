package ru.argustelecom.box.env.billing.bill.queue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static ru.argustelecom.box.env.billing.bill.BillEmailQueryResult.BILL_EMAIL_QUERY_RESULT_MAPPER;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;
import static ru.argustelecom.box.inf.queue.api.QueueProducer.Priority.MEDIUM;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.billing.bill.BillEmailQueryResult;
import ru.argustelecom.box.inf.queue.api.QueueProducer;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

/**
 * Сервис для планирования событий на отправку счётов.
 */
@DomainService
public class BillSendService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private QueueProducer queueProducer;

	/**
	 * Создаёт события на отправку счётов. Если параметр @batchSize не передан, то все события будут зашедулины на
	 * отправку в одно время.
	 * 
	 * @param ids
	 *            список идентификаторов счётов, которые надо отправить.
	 * @param senderName
	 *            наименования отправителя (того кто письма отправляет).
	 * @param batchSize
	 *            размер максимальной пачки, которая может быть отправлена в одну единицу времени.
	 * @param intervalInMinutes
	 *            интервал времени в минутах, через который надо зашедулить события на отправку.
	 * @param sendDate
	 *            дата отправки, если не передана, то будет взята текущая дата.
	 * @param forcedSending
	 *            принудительно отправить счет, даже если ранее он уже был отправлен
	 */
	public void scheduleSendingPlansForBills(List<Long> ids, String senderName, Long batchSize, Long intervalInMinutes,
			Date sendDate, boolean forcedSending) {
		checkArgument(!CollectionUtils.isEmpty(ids));
		checkNotNull(senderName);

		List<BillEmailQueryResult> billEmailPairList = findEmailsForBills(ids);
		LocalDateTime sendDateTime = sendDate != null ? toLocalDateTime(sendDate) : now();

		boolean validBatchSize = batchSize != null && !batchSize.equals(0L);
		boolean validInterval = intervalInMinutes != null && !intervalInMinutes.equals(0L);

		if (validBatchSize && validInterval) {
			List<List<BillEmailQueryResult>> partitions = Lists.partition(billEmailPairList, batchSize.intValue());
			for (List<BillEmailQueryResult> partition : partitions) {
				scheduleSendingPlansForPartition(partition, senderName, fromLocalDateTime(sendDateTime), forcedSending);
				sendDateTime = sendDateTime.plusMinutes(intervalInMinutes);
			}
		} else {
			scheduleSendingPlansForPartition(billEmailPairList, senderName, fromLocalDateTime(sendDateTime),
					forcedSending);
		}
	}

	private static final String FIND_EMAIL_FOR_BILLS = "BillSendService.findEmailsForBills";

	//@formatter:off
	@NamedNativeQuery(name = FIND_EMAIL_FOR_BILLS, resultSetMapping = BILL_EMAIL_QUERY_RESULT_MAPPER,
			query = "SELECT\n" +
					"  b.id as bill_id,\n" +
					"  e.contact_data as email\n" +
					"FROM\n" +
					"  system.bill b,\n" +
					"  system.customer c,\n" +
					"  system.contact e\n" +
					"WHERE\n" +
					"  b.customer_id = c.id\n" +
					"  AND c.main_email_id = e.id\n" +
					"  AND b.id IN (:bill_ids)\n" +
					"  AND e.dtype = 'EmailContact'"
	)
	//@formatter:on
	public List<BillEmailQueryResult> findEmailsForBills(List<Long> billIds) {
		return em.createNamedQuery(FIND_EMAIL_FOR_BILLS, BillEmailQueryResult.class).setParameter("bill_ids", billIds)
				.getResultList();
	}

	private void scheduleSendingPlansForPartition(List<BillEmailQueryResult> billEmailPairList, String senderName,
			Date sendDate, boolean forcedSending) {
		billEmailPairList.forEach(pair -> {
			BillSendContext sendContext = new BillSendContext(pair.getBillId(), senderName, pair.getEmail(),
					forcedSending);
			String queueId = genQueueName(pair.getBillId());
			queueProducer.remove(queueId);
			queueProducer.schedule(queueId, null, MEDIUM, sendDate, BillSendHandler.HANDLER_NAME, sendContext);
		});
	}

	private String genQueueName(Long billId) {
		return format("SEND_BILL_%d", billId);
	}

}