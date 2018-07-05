package ru.argustelecom.box.env.billing.bill;

import static com.google.common.collect.Lists.newArrayList;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.findList;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.model.AbstractBillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.billing.bill.model.SummaryBillAnalyticType;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class BillTypeAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private BillTypeRepository billTypeRp;

	@Inject
	private BillAnalyticTypeRepository billAnalyticTypeRp;

	public BillType create(String name, Long customerTypeId, BillPeriodType periodType, PeriodUnit periodUnit,
			GroupingMethod groupingMethod, PaymentCondition paymentCondition, Long summaryBillAnalyticTypeId,
			String description, List<Long> providerIds) {
		CustomerType customerType = customerTypeId != null ? em.find(CustomerType.class, customerTypeId) : null;
		SummaryBillAnalyticType summaryToPay = em.find(SummaryBillAnalyticType.class, summaryBillAnalyticTypeId);
		List<PartyRole> providers = findList(em, PartyRole.class, providerIds);
		return billTypeRp.create(name, customerType, periodType, periodUnit, groupingMethod, paymentCondition,
				summaryToPay, description, providers);
	}

	public void save(Long billTypeId, String name, String description) {
		BillType billType = em.find(BillType.class, billTypeId);
		billTypeRp.change(billType, name, description);
	}

	public void save(Long billTypeId, List<Long> ids) {
		BillType billType = em.find(BillType.class, billTypeId);
		List<AbstractBillAnalyticType> analytics = ids.isEmpty() ? newArrayList()
				: billAnalyticTypeRp.findAnalyticTypesByIds(ids);
		billType.replaceAnalyticsWith(analytics);
	}

	public List<BillType> findAll() {
		return billTypeRp.findAll();
	}

	private static final long serialVersionUID = -783759269402404274L;
}