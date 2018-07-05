package ru.argustelecom.box.env.contract.lifecycle.validator;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Arrays.asList;

import javax.inject.Inject;

import lombok.val;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustWarnIfHaveSubscriptions implements LifecycleCdiValidator<ContractState, AbstractContract<?>> {

	@Inject
	private SubscriptionRepository subscriptionRepository;

	@Override
	public void validate(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx,
			ValidationResult<Object> result) {

		val abstractContract = ctx.getBusinessObject();
		val builder = new StringBuilder();
		boolean needWarn = false;

		for (ProductOfferingContractEntry entry : abstractContract.getProductOfferingEntries()) {
			Subscription subscription = subscriptionRepository.findSubscription(entry);
			if (subscription != null && !isTerminatedState(subscription)) {
				if (needWarn) {
					builder.append(", ");
				} else {
					needWarn = true;
				}
				builder.append(getProductName(subscription));
			}
		}

		if (needWarn) {
			ContractMessagesBundle messages = LocaleUtils.getMessages(ContractMessagesBundle.class);

			result.warnv(abstractContract, messages.contractClosureWillCauseSubscriptionClosure(builder.toString()));
		}
	}

	private Object getProductName(Subscription subscription) {
		checkState(subscription.getSubject() != null);
		return subscription.getSubject().getObjectName();
	}

	private boolean isTerminatedState(Subscription subscription) {
		return subscription.inState(asList(SubscriptionState.CLOSURE_WAITING, SubscriptionState.CLOSED));
	}
}
