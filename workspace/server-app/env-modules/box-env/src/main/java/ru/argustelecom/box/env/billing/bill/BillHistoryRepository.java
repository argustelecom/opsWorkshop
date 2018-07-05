package ru.argustelecom.box.env.billing.bill;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillHistoryItem;
import ru.argustelecom.box.env.billing.bill.model.BillHistoryItem.BillHistoryQuery;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.service.Repository;

/**
 * Репозиторий для работы с историей {@linkplain BillHistoryItem счёта}.
 */
@Repository
public class BillHistoryRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService iss;

	/**
	 * Создание нового слепка счёта.
	 * 
	 * @param bill
	 *            счёт, который был изменён и для него создайтся слепок.
	 * @param employee
	 *            работник внёсший изменение.
	 */
	public BillHistoryItem create(Bill bill, Employee employee) {
		checkNotNull(bill);
		BillHistoryItem result = new BillHistoryItem(iss.nextValue(BillHistoryItem.class), bill, employee);
		em.persist(result);
		return result;
	}

	/**
	 * Ищет всю историю по счёту.
	 */
	public List<BillHistoryItem> find(Bill bill) {
		BillHistoryQuery query = new BillHistoryQuery();
		query.and(query.bill().equal(bill));
		return query.createTypedQuery(em).getResultList();
	}

	private static final long serialVersionUID = -5504612236859695856L;

}