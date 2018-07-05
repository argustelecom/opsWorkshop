package ru.argustelecom.box.env.contract.lifecycle.validator;

import java.util.Date;

import lombok.val;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustStillValid implements LifecycleCdiValidator<ContractState, AbstractContract<?>> {

	@Override
	public void validate(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx,
			ValidationResult<Object> result) {
		
		val abstractContract = ctx.getBusinessObject();
		val currentDate = new Date();
		if (abstractContract.getValidTo() != null && DateUtils.after(currentDate, abstractContract.getValidTo())) {
			ContractMessagesBundle messages = LocaleUtils.getMessages(ContractMessagesBundle.class);
			result.errorv(abstractContract, messages.contractExpired(abstractContract.getValidTo().toString()));
		}
	}

}
