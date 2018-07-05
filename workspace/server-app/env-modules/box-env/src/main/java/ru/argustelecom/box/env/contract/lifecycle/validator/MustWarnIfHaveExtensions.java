package ru.argustelecom.box.env.contract.lifecycle.validator;

import static ru.argustelecom.box.env.contract.model.ContractState.CANCELLED;
import static ru.argustelecom.box.env.contract.model.ContractState.TERMINATED;

import java.util.Objects;

import lombok.val;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustWarnIfHaveExtensions implements LifecycleCdiValidator<ContractState, Contract> {

	@Override
	public void validate(ExecutionCtx<ContractState, ? extends Contract> ctx, ValidationResult<Object> result) {
		val contract = ctx.getBusinessObject();
		val builder = new StringBuilder();
		boolean needWarn = false;

		for (ContractExtension extension : contract.getExtensions()) {
			if (!Objects.equals(extension.getState(), TERMINATED) && !Objects.equals(extension.getState(), CANCELLED)) {
				if (needWarn) {
					builder.append(", ");
				} else {
					needWarn = true;
				}
				builder.append(extension.getDocumentNumber());
			}
		}

		if (needWarn) {
			ContractMessagesBundle messages = LocaleUtils.getMessages(ContractMessagesBundle.class);

			result.warnv(contract, messages.contractClosureWillCauseContractExtensionClosure(builder.toString()));
		}
	}

}
