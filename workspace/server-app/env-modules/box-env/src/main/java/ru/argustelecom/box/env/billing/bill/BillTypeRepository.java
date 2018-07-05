package ru.argustelecom.box.env.billing.bill;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Arrays.asList;
import static ru.argustelecom.box.env.billing.bill.model.BillPeriodType.CALENDARIAN;
import static ru.argustelecom.box.env.billing.bill.model.BillPeriodType.CUSTOM;
import static ru.argustelecom.box.env.billing.bill.model.BillType.VALID_PROVIDER_CLASSES;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.BillType.BillTypeQuery;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.billing.bill.model.SummaryBillAnalyticType;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.service.Repository;

/**
 * Репозиторий для работы со спецификациями счетов.
 */
@Repository
public class BillTypeRepository implements Serializable {

	private static final long serialVersionUID = -1520370917358373804L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TypeFactory typeFactory;

	/**
	 * Создание спецификации счёта.
	 */
	public BillType create(String name, CustomerType customerType, BillPeriodType periodType, PeriodUnit periodUnit,
			GroupingMethod groupingMethod, PaymentCondition paymentCondition, SummaryBillAnalyticType summaryToPay,
			String description, List<PartyRole> providers) {

		checkState((CUSTOM.equals(periodType) && periodUnit == null)
				|| (CALENDARIAN.equals(periodType) && asList(periodType.getUnits()).contains(periodUnit)));
		checkNotNull(groupingMethod);
		checkNotNull(paymentCondition);
		checkNotNull(summaryToPay);
		checkState(!checkNotNull(providers).isEmpty() && providers.stream()
				.noneMatch(role -> role == null || !VALID_PROVIDER_CLASSES.contains(role.getClass())));

		BillType billType = typeFactory.createType(BillType.class);
		billType.setName(name);
		billType.setCustomerType(customerType);
		billType.setPeriodType(periodType);
		billType.setPeriodUnit(periodUnit);
		billType.setGroupingMethod(groupingMethod);
		billType.setPaymentCondition(paymentCondition);
		billType.setSummaryToPay(summaryToPay);
		billType.setDescription(description);
		billType.setProviders(providers);

		em.persist(billType);

		return billType;
	}

	/**
	 * Возвращает список всех спецификаций счетов.
	 */
	public List<BillType> findAll() {
		return new BillTypeQuery<>(BillType.class).getResultList(em);
	}

	/**
	 * Ищет спецификации счетов подходящие для определённой спецификации клиента.
	 * 
	 * @param customerType
	 *            спецификация клиента.
	 */
	public List<BillType> findBy(CustomerType customerType) {
		BillTypeQuery<BillType> query = new BillTypeQuery<>(BillType.class);
		query.and(query.customerType().equal(customerType));
		return query.createTypedQuery(em).getResultList();
	}

	public void change(BillType billType, String name, String description) {
		billType.setName(name);
		billType.setDescription(description);
	}

}