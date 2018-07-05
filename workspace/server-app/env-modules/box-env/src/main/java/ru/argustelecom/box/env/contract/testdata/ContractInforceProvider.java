package ru.argustelecom.box.env.contract.testdata;

import static com.google.common.base.Preconditions.checkState;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.testdata.LocationTestDataUtils;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.subscription.testdata.SubscriptionTestDataUtils;
import ru.argustelecom.box.env.contract.ContractRepository;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;
import ru.argustelecom.system.inf.validation.ValidationIssue;
import ru.argustelecom.system.inf.validation.ValidationResult;

/**
 * Провадер создает для теста ContractCardIT#shouldCreateContractExtension:
 * <li>Адрес (Location)
 * <li>Тестовую подписку (Subscription) из неё получает:
 * <ol>
 * <li>Клиента (Customer)
 * <li>Договор (Contract)
 * <li>и другние необходимые компоненты (см. SubscriptionTestDataUtils#createTestSubscription)
 * </ol>
 * <li>Спецификацию доп.соглашения (ContractExtensionSpec)
 * <li><b>Полученный договор переводит в состояние "Действует"<b>
 * 
 * @author v.semchenko
 *
 */
public class ContractInforceProvider implements TestDataProvider {

	public static final String CONTRACT = "contract.inforce.provider.contract";
	public static final String EXTENSION_TYPE_NAME = "contract.inforce.provider.extension.spec";

	@Inject
	private ContractRepository contractRp;

	@Inject
	private LifecycleRoutingService routingSrv;

	@Inject
	private LocationTestDataUtils locationTestDataUtils;

	@Inject
	private SubscriptionTestDataUtils subscriptionTestDataUtils;

	@Inject
	private ContractTypeTestDataUtils contractTypeTestDataUtils;

	public void provide(TestRunContext testRunContext) {
		checkState(testRunContext != null);

		Location location = locationTestDataUtils.findOrCreateTestLocation();
		checkState(location != null);
		// почти всё необходиомое создается при создании подписки
		Subscription subscription = subscriptionTestDataUtils.createTestSubscription(
				location, SubscriptionState.FORMALIZATION, SubscriptionLifecycleQualifier.FULL, false, new Money("10000"),
				new Money("100"), new Date(), null
		);
		checkState(subscription != null);
		// необходимо получить экземпляр контракта чтобы перевсти его в состояние "Действует"
		PersonalAccount personalAccount = subscription.getPersonalAccount();
		checkState(personalAccount != null);

		Customer customer = personalAccount.getCustomer();
		checkState(customer != null);

		List<Contract> contracts = contractRp.findContracts(customer);
		checkState(!contracts.isEmpty());

		Contract contract = contracts.get(0);
		checkState(contract != null);

		boolean result = activate(contract);
		checkState(result);

		CustomerType customerType = customer.getTypeInstance().getType();
		checkState(customerType != null);

		ContractExtensionType contractExtensionType = contractTypeTestDataUtils.findOrCreateDemoContractExtensionType(customerType);

		testRunContext.setBusinessPropertyWithMarshalling(CONTRACT, contract);
		testRunContext.setBusinessPropertyWithMarshalling(EXTENSION_TYPE_NAME, contractExtensionType.getName());
	}

	/**
	 * Переводи контракт из состояния ContractState.REGISTRATION в ContractState.INFORCE
	 * 
	 * @param contract
	 * @return
	 */
	private boolean activate(Contract contract) {
		if (contract.inState(ContractState.REGISTRATION)) {
			routingSrv.performRouting(contract, ContractState.INFORCE, false, WARN_SUPPRESSOR);
			return true;
		}
		return false;
	}

	// копипаст из SubscriptionRoutingService. Необходим в performRouting для перевода Contract из стадии
	// ContractState.REGISTRATION в ContractState.INFORCE
	private static final ContractWarningSuppressor WARN_SUPPRESSOR = new ContractWarningSuppressor();

	private static class ContractWarningSuppressor extends LifecyclePhaseListener<ContractState, Contract> {
		@Override
		public void beforeRouteExecution(ExecutionCtx<ContractState, ? extends Contract> ctx,
				ValidationResult<Object> result) {

			if (result.hasWarnings() && !result.hasErrors()) {
				ctx.suppressWarnings();
				for (ValidationIssue<Object> warning : result.getWarnings()) {
					log.infov("Warning of subject {0} was suppressed: {1}", warning.getSource(), warning.getMessage());
				}
			}
		}

		private static final Logger log = Logger.getLogger(ContractWarningSuppressor.class);
	}

}
