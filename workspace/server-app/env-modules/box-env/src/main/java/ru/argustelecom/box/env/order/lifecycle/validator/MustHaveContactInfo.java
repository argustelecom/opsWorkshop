package ru.argustelecom.box.env.order.lifecycle.validator;

import lombok.val;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.order.model.OrderState;
import ru.argustelecom.box.env.order.nls.OrderMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHaveContactInfo implements LifecycleCdiValidator<OrderState, Order> {

	@Override
	public void validate(ExecutionCtx<OrderState, ? extends Order> ctx, ValidationResult<Object> result) {
		val order = ctx.getBusinessObject();
		val customer = order.getCustomer();

		OrderMessagesBundle messages = LocaleUtils.getMessages(OrderMessagesBundle.class);

		if (customer == null) {
			result.error(order, messages.customerNotSpecified());
			return;
		}

		val contacts = customer.getParty().getContactInfo().getContacts();
		if (contacts.isEmpty()) {
			result.error(order, messages.contactInfoNotSpecified());
		}
	}
}
