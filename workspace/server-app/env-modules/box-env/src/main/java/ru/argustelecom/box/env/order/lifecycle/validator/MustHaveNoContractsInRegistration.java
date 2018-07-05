package ru.argustelecom.box.env.order.lifecycle.validator;

import java.util.Objects;

import lombok.val;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.order.model.OrderState;
import ru.argustelecom.box.env.order.nls.OrderMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHaveNoContractsInRegistration implements LifecycleCdiValidator<OrderState, Order> {

	public static final String HAVE_CONTRACT_IN_REGISTRATION_STATE_MESSAGE = "Договор {0} в состоянии оформления";

	@Override
	public void validate(ExecutionCtx<OrderState, ? extends Order> ctx, ValidationResult<Object> result) {
		val order = ctx.getBusinessObject();

		AbstractContract<?> contractInRegistration = order.getUnmodifiableContracts().stream()
				.filter(c -> Objects.equals(c.getState(), ContractState.REGISTRATION)).findFirst().orElse(null);

		if (contractInRegistration != null) {
			OrderMessagesBundle messages = LocaleUtils.getMessages(OrderMessagesBundle.class);
			result.errorv(order, messages.contractInRegistration(contractInRegistration.getDocumentNumber()));
		}
	}
}
