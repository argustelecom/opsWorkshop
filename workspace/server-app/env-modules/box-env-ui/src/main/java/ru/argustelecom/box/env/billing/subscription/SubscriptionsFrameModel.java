package ru.argustelecom.box.env.billing.subscription;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.collect.Lists;

import lombok.Getter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.lifecycle.SubscriptionFullLifecycle;
import ru.argustelecom.box.env.billing.subscription.lifecycle.SubscriptionShortLifecycle;
import ru.argustelecom.box.env.lifecycle.LifecycleFrameModel.RouteActionModel;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@Named(value = "subscriptionsFm")
@PresentationModel
public class SubscriptionsFrameModel implements Serializable {

	private static final long serialVersionUID = 421113583268066077L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private SubscriptionAppService subscriptionAs;

	@Inject
	private SubscriptionDtoTranslator subscriptionDtoTr;

	private EntityConverter entityConverter;

	@Getter
	private PersonalAccount personalAccount;

	@Getter
	private boolean showTerminated;

	private List<SubscriptionDto> allSubscriptions;
	private List<SubscriptionDto> activeSubscriptions;

	private RouteActionModel actionModel;
	private Boolean canCreateSubscription;

	public void preRender(PersonalAccount personalAccount) {
		this.personalAccount = personalAccount;
		initActiveSubscriptions();
		if (entityConverter == null)
			entityConverter = new EntityConverter();
	}

	public List<SubscriptionDto> getSubscriptions() {
		return showTerminated ? allSubscriptions : activeSubscriptions;
	}

	public RouteActionModel getActionModel() {
		// @formatter:off
		if (actionModel == null) {
			actionModel = new RouteActionModel();

			actionModel.put("fa fa-check-circle-o", "fa fa-check-circle-o",
				SubscriptionFullLifecycle.Routes.ACTIVATE,
				SubscriptionFullLifecycle.Routes.ACTIVATE_AFTER_DEBT_SUSPENSION,
				SubscriptionShortLifecycle.Routes.ACTIVATE
			);

			actionModel.put("icon-money_off", "icon-money_off",
				SubscriptionFullLifecycle.Routes.SUSPEND_FOR_DEBT,
				SubscriptionFullLifecycle.Routes.COMPLETE_SUSPENSION_FOR_DEBT
			);

			actionModel.put("fa fa-pause-circle-o", "fa fa-pause-circle-o",
				SubscriptionFullLifecycle.Routes.SUSPEND_ON_DEMAND,
				SubscriptionFullLifecycle.Routes.COMPLETE_SUSPENSION_ON_DEMAND
			);

			actionModel.put("fa fa-times-circle-o", "fa fa-times-circle-o",
				SubscriptionFullLifecycle.Routes.CLOSE_BEFORE_ACTIVATION,
				SubscriptionFullLifecycle.Routes.CLOSE_FROM_ACTIVE,
				SubscriptionFullLifecycle.Routes.CLOSE_FROM_SUSPENSION,
				SubscriptionFullLifecycle.Routes.COMPLETE_CLOSURE,
				SubscriptionShortLifecycle.Routes.CLOSE_BEFORE_ACTIVATION,
				SubscriptionShortLifecycle.Routes.CLOSE,
				SubscriptionShortLifecycle.Routes.COMPLETE_CLOSURE
				);
		}
		// @formatter:on
		return actionModel;
	}

	public Boolean canCreateSubscription() {
		if (canCreateSubscription == null)
			canCreateSubscription = subscriptionAs
					.haveContractWithEntriesWithoutSubs(personalAccount.getCustomer().getId());
		return canCreateSubscription;
	}

	public Callback<SubscriptionDto> getCreatedCallback() {
		return (subscription -> {
			addSubscription(allSubscriptions, subscription);
			addSubscription(activeSubscriptions, subscription);
		});
	}

	public void remove(SubscriptionDto subscription) {
		removeSubscription(allSubscriptions, subscription);
		removeSubscription(activeSubscriptions, subscription);
		subscriptionAs.removeSubscription(subscription.getId());
	}

	public void setShowTerminated(boolean showTerminated) {
		if (showTerminated)
			initAllSubscriptions();
		this.showTerminated = showTerminated;
	}

	private void initAllSubscriptions() {
		if (allSubscriptions == null)
			allSubscriptions = subscriptionAs.findAllSubscriptions(personalAccount.getId()).stream()
					.map(subs -> subscriptionDtoTr.translate(subs)).collect(Collectors.toList());
	}

	private void initActiveSubscriptions() {
		if (activeSubscriptions == null)
			activeSubscriptions = subscriptionAs.findAllActiveSubscriptions(personalAccount.getId()).stream()
					.map(subs -> subscriptionDtoTr.translate(subs)).collect(Collectors.toList());
	}

	private void addSubscription(List<SubscriptionDto> subscriptions, SubscriptionDto newSubscription) {
		if (subscriptions == null)
			subscriptions = Lists.newArrayList(newSubscription);
		else
			subscriptions.add(newSubscription);
	}

	private void removeSubscription(List<SubscriptionDto> subscriptions, SubscriptionDto subscription) {
		if (subscriptions != null)
			subscriptions.remove(subscription);
	}

}