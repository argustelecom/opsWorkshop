package ru.argustelecom.box.env.contract.lifecycle.validator;

import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.contract.model.ContractState.INFORCE;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.model.OptionContractEntry;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHaveInforcedOptionAgencyContract implements LifecycleCdiValidator<ContractState, AbstractContract<?>> {

	@Override
	public void validate(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx,
			ValidationResult<Object> result) {
		AbstractContract<?> contract = ctx.getBusinessObject();

		// список опций, предоставляемых по неактивным (статус != Действует) другим договорам
		List<OptionContractEntry> optionEntries = contract.getOptionEntries().stream()
				.filter(optionForServiceFromAnotherNotInforceContract(contract)).collect(toList());

		if (!optionEntries.isEmpty()) {
			OptionContractEntry entry = optionEntries.get(0);
			ContractMessagesBundle messages = LocaleUtils.getMessages(ContractMessagesBundle.class);
			AbstractContract<?> serviceContract = entry.getOptions().get(0).getService().getSubject().getContract();
			result.errorv(contract, messages.contractHasNotActiveAgencyContract(serviceContract.getObjectName()));
		}
	}

	private Predicate<OptionContractEntry> optionForServiceFromAnotherNotInforceContract(AbstractContract<?> contract) {
		return e -> {
			AbstractContract<?> serviceContract = e.getOptions().get(0).getService().getSubject().getContract();
			return !Objects.equals(contract, serviceContract) && !Objects.equals(serviceContract.getState(), INFORCE);
		};
	}

}