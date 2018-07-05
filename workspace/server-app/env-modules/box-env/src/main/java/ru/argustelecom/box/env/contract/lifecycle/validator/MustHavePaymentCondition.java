package ru.argustelecom.box.env.contract.lifecycle.validator;

import lombok.val;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHavePaymentCondition implements LifecycleCdiValidator<ContractState, Contract> {

	@Override
	public void validate(ExecutionCtx<ContractState, ? extends Contract> ctx, ValidationResult<Object> result) {
		val contract = ctx.getBusinessObject();
		if (contract.getPaymentCondition() == null) {
			ContractMessagesBundle messages = LocaleUtils.getMessages(ContractMessagesBundle.class);
			result.errorv(contract, messages.paymentConditionIsNotSpecified(), contract.getDocumentNumber());
		}
	}

}