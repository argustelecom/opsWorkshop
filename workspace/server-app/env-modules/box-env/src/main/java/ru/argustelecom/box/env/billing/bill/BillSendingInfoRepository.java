package ru.argustelecom.box.env.billing.bill;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.findList;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillSendingInfo;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@Repository
public class BillSendingInfoRepository implements Serializable {

	private static final String FIND_UNSENT_BILLS_WITHOUT_TEMPLATE = "BillSendingInfoRepository.findUnsentBillsWIthoutTemplate";

	@PersistenceContext
	private EntityManager em;

	/**
	 * Создаён информацию об отправке счёта, если данная информация уже существует, то она будет перезаписана.
	 */
	public BillSendingInfo save(Bill bill, Date sendingDate, String email) {
		checkArgument(bill != null);
		checkArgument(sendingDate != null);
		checkArgument(email != null);

		BillSendingInfo sendingInfo = em.find(BillSendingInfo.class, bill.getId());

		if (sendingInfo != null) {
			sendingInfo.setSendingDate(sendingDate);
			sendingInfo.setEmail(email);
		} else {
			sendingInfo = new BillSendingInfo(bill, sendingDate, email);
			em.persist(sendingInfo);
		}

		return sendingInfo;
	}

	public BillSendingInfo findSendingInfo(Bill bill) {
		return em.find(BillSendingInfo.class, bill.getId());
	}

	private static final String FIND_ALL_NOT_SENT_BILL_IDS = "BillSendingInfoRepository.findAllNotSentBillIds";

	//@formatter:off
	@SuppressWarnings("unchecked")
	@NamedNativeQuery(name = FIND_ALL_NOT_SENT_BILL_IDS,
			query = "SELECT b.id FROM system.bill b WHERE id NOT IN (SELECT bsi.id FROM system.bill_sending_info bsi)")
	//@formatter:on
	public List<Long> findAllNotSentBillIds() {
		List<BigInteger> resultList = em.createNamedQuery(FIND_ALL_NOT_SENT_BILL_IDS).getResultList();
		return resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
	}

	private static final String FIND_UNSENT_BILLS_WHERE_CUSTOMER_DOES_NOT_HAVE_MAIN_EMAIL = "BillSendingInfoRepository.findUnsentBillsWhereCustomerDoesNotHaveMainEmail";

	/**
	 * Возвращает список ещё не отправленных счетов, но у которых для клиента не указан
	 * {@linkplain ru.argustelecom.box.env.party.model.role.Customer#mainEmail основной адрес}.
	 */
	//@formatter:off
	@SuppressWarnings("unchecked")
	@NamedNativeQuery(name = FIND_UNSENT_BILLS_WHERE_CUSTOMER_DOES_NOT_HAVE_MAIN_EMAIL,
			query = "SELECT b.id\n" +
					"FROM\n" +
					"  system.bill b,\n" +
					"  system.customer c\n" +
					"WHERE\n" +
					"  b.customer_id = c.id\n" +
					"  AND c.main_email_id IS NULL\n" +
					"  AND NOT exists(SELECT *\n" +
					"                 FROM system.bill_sending_info bsi\n" +
					"                 WHERE bsi.id = b.id)")
	//@formatter:on
	public List<Bill> findUnsentBillsWhereCustomerDoesNotHaveMainEmail() {
		List<BigInteger> ids = em.createNamedQuery(FIND_UNSENT_BILLS_WHERE_CUSTOMER_DOES_NOT_HAVE_MAIN_EMAIL)
				.getResultList();
		return findList(em, Bill.class, ids.stream().map(BigInteger::longValue).collect(Collectors.toList()));
	}

	//@formatter:off
	@NamedQuery(name = FIND_UNSENT_BILLS_WITHOUT_TEMPLATE,
			query = "FROM Bill b " +
					"WHERE b.template is null AND " +
					"NOT exists (SELECT bsi.id " +
					"                 FROM BillSendingInfo bsi " +
					"                 WHERE bsi.id = b.id)")
	//@formatter:on
	public List<Bill> findUnsentBillsWithoutTemplate() {
		return em.createNamedQuery(FIND_UNSENT_BILLS_WITHOUT_TEMPLATE, Bill.class).getResultList();
	}

	private static final long serialVersionUID = -6333202210860824441L;
}
