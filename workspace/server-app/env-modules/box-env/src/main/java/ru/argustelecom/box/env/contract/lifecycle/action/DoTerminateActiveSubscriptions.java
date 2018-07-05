package ru.argustelecom.box.env.contract.lifecycle.action;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.lifecycle.SubscriptionRoutingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

@LifecycleBean
public class DoTerminateActiveSubscriptions implements LifecycleCdiAction<ContractState, AbstractContract<?>> {

	@Inject
	private SubscriptionRoutingService routings;

	@Inject
	private SubscriptionRepository subscriptionRepository;

	@Override
	public void execute(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx) {
		AbstractContract<?> contract = ctx.getBusinessObject();
		for (ContractEntry entry : contract.getEntries()) {
			if (entry instanceof ProductOfferingContractEntry) {
				Subscription subs = subscriptionRepository.findSubscription((ProductOfferingContractEntry) entry);
				if (subs != null) {
					routings.close(subs);
				}
			}
		}
	}
}
