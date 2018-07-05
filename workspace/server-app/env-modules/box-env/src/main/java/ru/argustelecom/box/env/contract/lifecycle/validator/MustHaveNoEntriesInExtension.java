package ru.argustelecom.box.env.contract.lifecycle.validator;

import lombok.val;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHaveNoEntriesInExtension implements LifecycleCdiValidator<ContractState, ContractExtension> {

	@Override
	public void validate(ExecutionCtx<ContractState, ? extends ContractExtension> ctx, ValidationResult<Object> result) {
		val extension = ctx.getBusinessObject();
		if (!extension.getEntries().isEmpty() || !extension.getExcludedEntries().isEmpty()) {
			ContractMessagesBundle messages = LocaleUtils.getMessages(ContractMessagesBundle.class);
			result.errorv(extension, messages.contractExtensionHasEntries());
		}
	}
}
