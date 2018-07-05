package ru.argustelecom.box.env.contract;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Optional.ofNullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.contract.lifecycle.model.SubscriptionCreationPresets;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.pricing.model.PeriodProductOffering;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.privilege.model.PrivilegeType;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named("contractLifecycleExtenderFm")
public class ContractLifecycleExtenderFrameModel extends LifecyclePhaseListener<ContractState, AbstractContract<?>>
		implements Serializable {

	private static final long serialVersionUID = -8178405645547236091L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private SubscriptionRepository subscriptionRepository;

	@Getter
	private AbstractContract<?> contract;

	@Getter
	private List<EntryCorrectionDto> entryCorrectionList = new ArrayList<>();

	private boolean activationRoute;

	@Override
	public void beforeInitialization(AbstractContract<?> businessObject) {
		super.beforeInitialization(businessObject);
		this.contract = businessObject;
	}

	@Override
	public void afterRouteDefinition(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx) {
		super.afterRouteDefinition(ctx);
		activationRoute = ctx.getEndpoint().getDestination() == ContractState.INFORCE;
		if (activationRoute) {
			initEntryCorrectionList(ctx);
		}
	}

	@Override
	public void beforeRouteValidation(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx) {
		super.beforeRouteValidation(ctx);
		for (val correctionDto : entryCorrectionList) {
			val presets = new SubscriptionCreationPresets(correctionDto.getAccount(), correctionDto.isUsePrivilege(),
					correctionDto.getPrivilegeType(), correctionDto.getPrivilegeDuration());
			ctx.putData(correctionDto.getEntry(), presets);
		}
	}

	@Override
	public void afterFinalization(AbstractContract<?> businessObject, ContractState oldState) {
		super.afterFinalization(businessObject, oldState);
		this.contract = null;
		this.activationRoute = false;
		this.entryCorrectionList.clear();
	}

	public boolean isRenderContent() {
		return activationRoute && !entryCorrectionList.isEmpty();
	}

	public List<PersonalAccount> getAccounts() {
		if (contract != null && contract.getCustomer() != null) {
			Customer customer = contract.getCustomer();
			em.refresh(customer);
			return customer.getPersonalAccounts();
		}
		return Collections.emptyList();
	}

	private PersonalAccount getDefAcct() {
		List<PersonalAccount> accounts = getAccounts();
		return !accounts.isEmpty() ? accounts.iterator().next() : null;
	}

	private void initEntryCorrectionList(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx) {
		checkState(contract != null);
		entryCorrectionList.clear();
		for (val entry : contract.getProductOfferingEntries()) {
			Subscription subscription = subscriptionRepository.findSubscription(entry);
			boolean mustChooseAcctForSubs = subscription == null && entry.getProductOffering().isRecurrentProduct();
			boolean hasPrivilege = hasPrivilege(entry);
			if (mustChooseAcctForSubs) {
				PersonalAccount account = ofNullable(ctx.getData(entry, PersonalAccount.class)).orElse(getDefAcct());
				entryCorrectionList.add(new EntryCorrectionDto(entry, account, false, hasPrivilege));
			} else {
				if (hasPrivilege) {
					entryCorrectionList.add(new EntryCorrectionDto(entry, entry.getPersonalAccount(), true, true));
				}
			}
		}
	}

	private boolean hasPrivilege(ProductOfferingContractEntry entry) {
		ProductOffering productOffering = EntityManagerUtils.initializeAndUnproxy(entry.getProductOffering());
		return productOffering instanceof PeriodProductOffering
				&& ((PeriodProductOffering) productOffering).getPrivilegeType() != null;
	}

	/**
	 * Dto для редактирования позиции договора/доп. соглашения при его активации. Содержит основную информацию по
	 * позиции, а так же позволяет:
	 * <ul>
	 * <li>выбрать лицевой счёт, на котором будет создана подписка</li>
	 * <li>отменить создание привилегии для продуктового предложения</li>
	 * </ul>
	 */
	@Getter
	@Setter
	@EqualsAndHashCode(of = "entry")
	public class EntryCorrectionDto implements Serializable {
		private static final long serialVersionUID = 215780168596629069L;

		/**
		 * Позиция договора/доп. соглашения.
		 */
		private ContractEntry entry;

		/**
		 * Названия прайс листа, в который входит продуктовое предложение, на основании которого создана позиция.
		 */
		private String pricelistName;

		/**
		 * Стоимость продуктового предложения.
		 */
		private Money price;

		/**
		 * Название условия предоставления для продуктового предложения.
		 */
		private String provisionTermsName;

		/**
		 * Описание условия предоставления для продуктового предложения.
		 */
		private String provisionTermsDescription;

		/**
		 * Выбран ли для данной позиции ЛС, возможно подписка по данной позиции была создана раньше чем произведина
		 * активация договора.
		 */
		private boolean hasAccount;

		/**
		 * Лицевой счёт, на котором надо создать подписку, по данной позиции.
		 */
		private PersonalAccount account;

		/**
		 * Тип привилегии.
		 */
		private PrivilegeType privilegeType;

		/**
		 * Длительность привилегии.
		 */
		private PeriodDuration privilegeDuration;

		/**
		 * Есть ли для продуктового предложения возможность создать привилегию.
		 */
		private boolean hasPrivilege;

		/**
		 * Необходимо ли создавать привилегию для подписки.
		 */
		private boolean usePrivilege;

		EntryCorrectionDto(ProductOfferingContractEntry entry, PersonalAccount account, boolean hasAccount,
				boolean hasPrivilege) {
			this.entry = entry;
			this.hasAccount = hasAccount;
			this.account = account;

			ProductOffering productOffering = EntityManagerUtils.initializeAndUnproxy(entry.getProductOffering());

			this.pricelistName = productOffering.getPricelist().getObjectName();
			this.price = productOffering.getPrice();
			this.provisionTermsName = productOffering.getProvisionTerms().getObjectName();
			this.provisionTermsDescription = productOffering.getProvisionTerms().getDescription();

			if (hasPrivilege) {
				setPrivilege((PeriodProductOffering) productOffering);
			}
		}

		public void setPrivilege(PeriodProductOffering productOffering) {
			hasPrivilege = true;
			usePrivilege = true;
			privilegeType = productOffering.getPrivilegeType();
			privilegeDuration = productOffering.getPrivilegeDuration();
		}

	}

}