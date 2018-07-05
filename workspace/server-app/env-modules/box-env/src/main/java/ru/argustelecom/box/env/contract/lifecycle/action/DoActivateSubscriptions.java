package ru.argustelecom.box.env.contract.lifecycle.action;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.stl.period.PeriodUtils.createPeriod;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.maxDate;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.Date;

import javax.inject.Inject;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.lifecycle.SubscriptionRoutingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.contract.lifecycle.model.SubscriptionCreationPresets;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.privilege.PrivilegeRepository;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;

@LifecycleBean
public class DoActivateSubscriptions implements LifecycleCdiAction<ContractState, AbstractContract<?>> {

	@Inject
	private PrivilegeRepository privilegeRp;

	@Inject
	private SubscriptionRepository subscriptionRp;

	@Inject
	private SubscriptionRoutingService routingSvc;

	@Override
	public void execute(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx) {
		AbstractContract<?> contract = ctx.getBusinessObject();
		Date activationDate = ctx.getExecutionDate();

		for (ProductOfferingContractEntry entry : contract.getProductOfferingEntries()) {
			SubscriptionCreationPresets presets = ctx.getData(entry, SubscriptionCreationPresets.class);
			Subscription subscription = findOrCreateSubscription(entry, presets);

			ensureSubscriptionDate(subscription, contract, activationDate);
			tryCreatePrivilege(subscription, presets);
			activate(subscription);
		}
	}

	/**
	 * Находит подписку по отношению с ContractEntry, если не находит, то пытается создать по параметрам, определенным
	 * пользователем в UI
	 */
	private Subscription findOrCreateSubscription(ProductOfferingContractEntry entry,
			SubscriptionCreationPresets presets) {
		Subscription subscription = subscriptionRp.findSubscription(entry);
		if (subscription == null) {
			PersonalAccount account = presets.getPersonalAccount();
			checkState(account != null);
			subscription = subscriptionRp.createSubscriptionByContract(account, entry);
		}
		return subscription;
	}

	/**
	 * Гарантирует установку для подписки актуальной даты начала действия
	 * <p>
	 * 
	 * <strong>Дано:</strong>
	 * <ul>
	 * <li><i>Дата подписки SD</i> - tсли SD на этом этапе указана, то используется она (т.к. подписка создана
	 * пользователем явно). Если SD не указана, то требуется автоматическое заполнение
	 * <li><i>Дата договора CD</i> - обязательный параметр, должен быть указан к этому моменту
	 * <li><i>Дата активации AD</i> - обязательный параметр, должен быть определен к этому моменту
	 * </ul>
	 * 
	 * <strong>Правило автоматического определения актуальной даты:</strong>
	 * <ul>
	 * <li>Если AD < CD, то SD = CD, подписка должна перейти в ожидание активации
	 * <li>Если AD = CD, то SD = CD, подписка переходит в активное состояние, если это возможно
	 * <li>Если AD > CD, то SD = AD, подписка переходит в активное состояние, если это возможно
	 * </ul>
	 * 
	 * <strong>Общее итоговое правило:</strong> SD должна быть не меньше чем AD
	 */
	private void ensureSubscriptionDate(Subscription subscription, AbstractContract<?> contract, Date activationDate) {
		if (subscription.getValidFrom() == null) {
			checkState(contract.getValidFrom() != null);
			subscription.setValidFrom(maxDate(activationDate, contract.getValidFrom()));
		}
	}

	/**
	 * Если необходимо, создает для подписки привилегию, указанную в продуктовом предложении. Это единственное место,
	 * где для подписки СОЗДАЕТСЯ (не расширяется) привилегия с использованием длительности и точки начала отсчета.
	 * <p>
	 * Чтобы не было чего-то похожего на BOX-2479, мы должны гарантировать, что привилегия начнется в точке начала
	 * тарификации, а не в точке начала действия подписки, т.к. в этом случае ассоциация периода и инвойсов будет
	 * работать корректно только для произвольных и отладочных периодов, но не для календарных периодов.
	 */
	private void tryCreatePrivilege(Subscription subscription, SubscriptionCreationPresets presets) {
		if (presets != null && presets.isUsePrivilege()) {
			PeriodType periodType = subscription.getProvisionTerms().getPeriodType();
			PeriodDuration duration = presets.getPrivilegeDuration();
			LocalDateTime poi = toLocalDateTime(subscription.getValidFrom());

			Range<LocalDateTime> baseUnit = periodType.calculateBaseUnitBoundaries(poi, poi);
			Range<LocalDateTime> boundaries = createPeriod(baseUnit.lowerEndpoint(), duration);
			checkState(boundaries != null);

			Date privilegeStart = fromLocalDateTime(boundaries.lowerEndpoint());
			Date privilegeEnd = fromLocalDateTime(boundaries.upperEndpoint());

			privilegeRp.createPrivilege(privilegeStart, privilegeEnd, subscription, presets.getPrivilegeType());
		}
	}

	/**
	 * Выполняет активацию подписки, если это возможно
	 */
	private void activate(Subscription subscription) {
		routingSvc.activate(subscription);
	}
}