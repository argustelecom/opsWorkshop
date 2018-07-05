package ru.argustelecom.box.env.billing.subscription.testdata;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import com.google.common.base.Preconditions;

import lombok.val;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.account.testdata.PersonalAccountTestDataUtils;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.provision.testdata.ProvisionTermsTestDataUtils;
import ru.argustelecom.box.env.billing.reason.testdata.UserReasonTypeTestDataUtils;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.lifecycle.SubscriptionRoutingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.transaction.TransactionRepository;
import ru.argustelecom.box.env.commodity.model.CommoditySpec;
import ru.argustelecom.box.env.commodity.testdata.CommoditySpecTestDataUtils;
import ru.argustelecom.box.env.contract.ContractEntryRepository;
import ru.argustelecom.box.env.contract.ContractRepository;
import ru.argustelecom.box.env.contract.model.*;
import ru.argustelecom.box.env.contract.testdata.ContractTypeTestDataUtils;
import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.testdata.CustomerSegmentTestDataUtils;
import ru.argustelecom.box.env.party.testdata.CustomerTypeTestDataUtils;
import ru.argustelecom.box.env.party.testdata.PartyTestDataUtils;
import ru.argustelecom.box.env.pricing.model.CommonPricelist;
import ru.argustelecom.box.env.pricing.model.PeriodProductOffering;
import ru.argustelecom.box.env.pricing.model.PricelistState;
import ru.argustelecom.box.env.pricing.testdata.PricelistTestDataUtils;
import ru.argustelecom.box.env.pricing.testdata.ProductOfferingTestDataUtils;
import ru.argustelecom.box.env.product.model.ProductType;
import ru.argustelecom.box.env.product.testdata.ProductTypeTestDataUtils;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.chrono.DateUtils;

/**
 * @author kostd
 *
 */
public class SubscriptionTestDataUtils implements Serializable {

	private static final long serialVersionUID = -5377396580811132266L;

	@Inject
	private SubscriptionRepository subscriptionRp;

	@Inject
	private ContractRepository contractRp;

	@Inject
	private ContractEntryRepository contractEntryRp;
	
	@Inject
	private TransactionRepository transactionRp;

	@Inject
	private ProductTypeTestDataUtils productTypeTestDataUtils;

	@Inject
	private CustomerTypeTestDataUtils customerTypeTestDataUtils;

	@Inject
	private ContractTypeTestDataUtils contractTypeTestDataUtils;

	@Inject
	private PartyTestDataUtils partyTestDataUtils;

	@Inject
	private PersonalAccountTestDataUtils personalAccountTestDataUtils;

	@Inject
	private CommoditySpecTestDataUtils commoditySpecTestDataUtils;

	@Inject
	private ProvisionTermsTestDataUtils provisionTermsTestDataUtils;

	@Inject
	private CustomerSegmentTestDataUtils customerSegmentTestDataUtils;

	@Inject
	private PricelistTestDataUtils pricelistTestDataUtils;

	@Inject
	private ProductOfferingTestDataUtils productOfferingTestDataUtils;

	@Inject
	private UserReasonTypeTestDataUtils userReasonTypeTestDataUtils;

	@Inject
	private SubscriptionRoutingService subscriptionRoutingService;

	@PersistenceContext
	private EntityManager em;

	private static final Logger log = Logger.getLogger(SubscriptionTestDataUtils.class);

	private static final String TOKEN = "-23868";

	private static final String TEST_USER_REASON_TYPE_NAME = "Тип зачисления " + TOKEN;

	/**
	 * Создает тестовую подписку в желаемом состоянии
	 * @param location Адрес предоставления
	 * @param desiredState Состояние подписки
	 * {@link SubscriptionState.ACTIVATION_WAITING} дата "в будущем", для {@link SubscriptionState.ACTIVE} дата "в
	 * @param qualifier Тип ЖЦ
	 * @param reserveFunds Резервируем или нет деньги при открытии инвойса
	 * @param balance Баланс ЛС
	 * @param productOfferingPrice Стоимость позиции договора
	 * @param validFrom Начало действия подписки
	 * @param validTo Конец действия подписки
	 * @return
	 */
	public Subscription createTestSubscription(
			Location location,
			SubscriptionState desiredState,
			SubscriptionLifecycleQualifier qualifier,
			boolean reserveFunds,
			Money balance,
			Money productOfferingPrice,
			Date validFrom,
			Date validTo
	) {
		// 2. Создаем(или находим) продукт и добавляем в него номенклатуру. Для продукта также понадобится группа
		// продуктов
		ProductType productType = productTypeTestDataUtils.findOrCreateTestProductType();

		// 3. Создаем (или берем существующее) длительное предложение на продукт. Для него нужны:
		// 3.1. Условия предоставления;
		RecurrentTerms recurrentTerms = provisionTermsTestDataUtils.findOrCreateTestRecurrentTerms(qualifier, reserveFunds);

		// 3.2. Прайс-лист, которому нужен
		// 3.2.1 тип клиента, которому нужна
		// 3.2.1.1. спецификация клиента
		CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType();
		Preconditions.checkState(customerType != null);
		CustomerSegment customerSegment = customerSegmentTestDataUtils.findOrCreateTestCustomerSegment(customerType);

		CommonPricelist commonPricelist = pricelistTestDataUtils.createTestCommonPricelist(
				customerType, customerSegment, PricelistState.INFORCE
		);
		PeriodProductOffering periodProductOffering = productOfferingTestDataUtils
				.createTestPeriodProductOffering(commonPricelist, productType, recurrentTerms, productOfferingPrice);

		log.debug("Создано предложение id = " + periodProductOffering.getId());
		// 4. Создаем (или находим) типовой договор.
		ContractType contractType = contractTypeTestDataUtils.findOrCreateTestContractType(customerType);
		Preconditions.checkState(contractType != null);
		log.debug("Создан типовой договор id = " + contractType.getId());
		// 4.1. Теперь создаем договор и entry в нем. Для этого понадобится лицо и лицевой счет и location
		// Все, что создаем далее -- не справочники, поэтому предварительным поиском уже существующего не обременены
		Customer individual = partyTestDataUtils.createTestIndividualCustomer(customerType);
		PersonalAccount personalAccount = personalAccountTestDataUtils.createTestPersonalAccount(individual);
		// изначально на счету ноль. Закинем бабла
		transactionRp.createUserTransaction(personalAccount, balance,
				userReasonTypeTestDataUtils.findOrCreateTestUserReasonType(TEST_USER_REASON_TYPE_NAME),
				UUID.randomUUID().toString());

		Contract contract = contractRp.createContract(contractType, individual,
				/* contractNumber = */ UUID.randomUUID().toString(), validFrom, DateUtils.add24Hours(new Date(), 7),
				PaymentCondition.POSTPAYMENT, null);
		Preconditions.checkState(contract != null);
		log.debug("Создан экземпляр договора id = " + contract.getId());

		val contractEntry = contractEntryRp.createProductOfferingEntry(contract, periodProductOffering, location,
				personalAccount);

		// 5. теперь наконец можем создавать подписку.
		// Если желаемое состояние "ожидает активации", то дата validFrom должа быть обязательно "в будущем", иначе
		// потом при переводе в состояние "подготовлен к активации" сразу активируется, а нам надо, чтобы застряла в
		// этом состоянии.

		Subscription subscription = subscriptionRp.createSubscriptionByContract(personalAccount, contractEntry,
				validFrom, validTo);
		Preconditions.checkState(subscription != null);

		log.debug("Создана подписка id = " + subscription.getId());
		// 5.1. Если требуется, переводим в ожидаемое состояние

		if (!desiredState.equals(SubscriptionState.FORMALIZATION)) {
			// SubscriptionRoutingService игнорит некоторые изменения в контексте персистенции, получает их прямо из БД.
			// Вынуждены флашить перед обращением к SubscriptionRoutingService
			// #TODO: это известная (по крайней мере, Антону) фича Box
			em.flush();

			boolean isRouted;
			switch (desiredState) {
				case SUSPENDED_FOR_DEBT: {
					subscriptionRoutingService.activate(subscription);
					subscriptionRoutingService.suspendForDebt(subscription);
					isRouted = subscriptionRoutingService.completeSuspensionForDebt(subscription);
					break;
				}
				case SUSPENDED_ON_DEMAND: {
					subscriptionRoutingService.activate(subscription);
					subscriptionRoutingService.suspendOnDemand(subscription);
					isRouted = subscriptionRoutingService.completeSuspensionOnDemand(subscription);
					break;
				}
				case CLOSURE_WAITING: {
					subscriptionRoutingService.activate(subscription);
					isRouted = subscriptionRoutingService.close(subscription);
					break;
				}
				default: {
					isRouted = subscriptionRoutingService.activate(subscription);
				}
			}
			Preconditions.checkState(isRouted, "Subscription was not routed");
		}
		// у жизненного цикла подписки много особенностей. Например, если дата validFrom "в прошлом", сразу активируется
		// (а нам надо "ожидает активации"). На всякий случай проверим, что получили то, что хотели.
		Preconditions.checkState(subscription.getState().equals(desiredState),
				"Actual subscription state is: " + subscription.getState() + ", when expected: " + desiredState);

		return subscription;
	}
}