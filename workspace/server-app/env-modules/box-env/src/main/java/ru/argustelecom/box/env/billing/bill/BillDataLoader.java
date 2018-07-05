package ru.argustelecom.box.env.billing.bill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import static java.util.Optional.ofNullable;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.bill.model.BillGroup;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.queue.BillCreationContext;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Сервис для загрузки всех необходимых данных, для генерации счёта, по {@linkplain BillCreationContext контексту}.
 */
@DomainService
public class BillDataLoader implements Serializable {

	@PersistenceContext
	private EntityManager em;

	/**
	 * Инициализирует все необходимые данные для формирования счёта.
	 */
	public BillData load(Long id, String number, BillGroup billGroup, Long billTypeId, Long templateId,
			PaymentCondition paymentCondition, Date creationDate, Date billDate, BillPeriod period) {
		List<Subscription> subscriptions = !billGroup.getSubscriptionIds().isEmpty()
				? initSubscriptions(billGroup.getSubscriptionIds())
				: new ArrayList<>();
		Customer customer = initPartyRole(billGroup.getCustomerId());
		PartyRole provider = initPartyRole(billGroup.getProviderId());
		PartyRole broker = billGroup.getBrokerId() != null ? initPartyRole(billGroup.getBrokerId()): null;
		ReportModelTemplate template = templateId != null ? em.find(ReportModelTemplate.class, templateId) : null;

		//@formatter:off
		BillData billData = BillData.builder()
								.id(id)
								.number(number)
								.customer(customer)
								.provider(provider)
								.broker((Owner) ofNullable(broker).map(EntityManagerUtils::initializeAndUnproxy).orElse(null))
								.groupingMethod(billGroup.getType())
								.paymentCondition(paymentCondition)
								.billDate(billDate)
								.period(period)
								.billType(initBillType(billTypeId))
								.template(template)
								.creationDate(creationDate)
								.subscriptions(subscriptions)
								.shortTermInvoiceIds(billGroup.getShortTermInvoiceIds())
								.usageInvoiceIds(billGroup.getUsageInvoiceIds())
							.build();
		//@formatter:on
		initGroup(billData, billGroup.getId());

		return billData;
	}

	private static final String INIT_BILL_TYPE = "BillDataLoader.initBillType";

	@NamedQuery(name = INIT_BILL_TYPE, query = "from BillType bs left join fetch bs.analytics join fetch bs.propertyHolder where bs.id = :id")
	private BillType initBillType(Long id) {
		return em.createNamedQuery(INIT_BILL_TYPE, BillType.class).setParameter("id", id).getResultList().stream()
				.findFirst().orElse(null);
	}

	private static final String INIT_SUBSCRIPTIONS = "BillDataLoader.initSubscriptions";

	@NamedQuery(name = INIT_SUBSCRIPTIONS, query = "from Subscription s join fetch s.costCause join fetch s.subjectCause where s.id in (:ids)")
	private List<Subscription> initSubscriptions(List<Long> ids) {
		return em.createNamedQuery(INIT_SUBSCRIPTIONS, Subscription.class).setParameter("ids", ids).getResultList();
	}

	private static final String INIT_PARTY_ROLE = "BillDataLoader.initPartyRole";

	//@formatter:off
	@SuppressWarnings("unchecked")
	@NamedQuery(name = INIT_PARTY_ROLE, query = "from PartyRole pr" +
											  " join fetch pr.party p" +
											  " join fetch p.typeInstance psi" +
											  " join fetch psi.type ps" +
											  " join fetch ps.propertyHolder" +
											  " where pr.id = :id")
	//@formatter:on
	private <T extends PartyRole> T initPartyRole(Long id) {
		return (T) em.createNamedQuery(INIT_PARTY_ROLE, PartyRole.class).setParameter("id", id).getResultList().stream()
				.findFirst().orElse(null);
	}

	private static final String INIT_CUSTOMER = "BillDataLoader.initCustomer";

	//@formatter:off
	@SuppressWarnings("unchecked")
	@NamedQuery(name = INIT_CUSTOMER, query = "from Customer c" +
											  " join fetch c.typeInstance csi" +
											  " join fetch csi.type cs" +
											  " join fetch cs.propertyHolder" +
											  " join fetch c.party p" +
											  " join fetch p.typeInstance psi" +
											  " join fetch psi.type ps" +
											  " join fetch ps.propertyHolder" +
											  " where c.id = :id")
	//@formatter:on
	private Customer initCustomer(Long id) {
		return em.createNamedQuery(INIT_CUSTOMER, Customer.class).setParameter("id", id).getResultList().stream()
				.findFirst().orElse(null);
	}

	private static final String INIT_CONTRACT = "BillDataLoader.initContract";

	//@formatter:off
	@NamedQuery(name = INIT_CONTRACT, query = "from Contract c" +
			                                  " join fetch c.type cs" +
											  " join fetch cs.propertyHolder" +
											  " join fetch c.attachmentContext" +
											  " join fetch c.commentContext" +
											  " where c.id = :id")
	//@formatter:on
	private Contract initContract(Long id) {
		return em.createNamedQuery(INIT_CONTRACT, Contract.class).setParameter("id", id).getResultList().stream()
				.findFirst().orElse(null);
	}

	private void initGroup(BillData billData, Long groupId) {
		switch (billData.getGroupingMethod()) {
		case CONTRACT:
			billData.setContract(initContract(groupId));
			break;
		case PERSONAL_ACCOUNT:
			billData.setPersonalAccount(em.find(PersonalAccount.class, groupId));
			break;
		default:
			throw new SystemException(String.format("Unsupported grouping method: '%s'", billData.getGroupingMethod()));
		}
	}

	private static final long serialVersionUID = -4272625070135587283L;

}