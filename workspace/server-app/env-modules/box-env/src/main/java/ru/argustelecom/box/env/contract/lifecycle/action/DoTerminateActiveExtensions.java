package ru.argustelecom.box.env.contract.lifecycle.action;

import static ru.argustelecom.box.env.contract.lifecycle.AbstractContractLifecycle.warningsSuppressor;
import static ru.argustelecom.box.env.contract.model.ContractState.INFORCE;
import static ru.argustelecom.box.env.contract.model.ContractState.REGISTRATION;

import javax.inject.Inject;

import lombok.val;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

@LifecycleBean
public class DoTerminateActiveExtensions implements LifecycleCdiAction<ContractState, Contract> {

	@Inject
	private LifecycleRoutingService routings;

	@Override
	public void execute(ExecutionCtx<ContractState, ? extends Contract> ctx) {
		val contract = ctx.getBusinessObject();
		contract.getExtensions().forEach(extension -> {
			if (extension.inState(REGISTRATION)) {
				routings.performRouting(extension, ContractState.CANCELLED, true, warningsSuppressor);
			}
			if (extension.inState(INFORCE)) {
				routings.performRouting(extension, ContractState.TERMINATED, true, warningsSuppressor);
			}
		});
	}
}
