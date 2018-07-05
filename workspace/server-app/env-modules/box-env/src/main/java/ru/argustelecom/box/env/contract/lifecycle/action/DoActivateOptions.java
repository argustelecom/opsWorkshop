package ru.argustelecom.box.env.contract.lifecycle.action;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.commodity.model.ServiceState.ACTIVE;
import static ru.argustelecom.box.env.contract.model.ContractState.INFORCE;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionRepository;
import ru.argustelecom.box.env.commodity.telephony.lifecycle.TelephonyOptionRoutingService;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

@LifecycleBean
public class DoActivateOptions implements LifecycleCdiAction<ContractState, AbstractContract<?>> {

	@Inject
	private TelephonyOptionRoutingService telephonyOptionRoutingSrv;

	@Inject
	private TelephonyOptionRepository telephonyOptionRp;

	@Override
	public void execute(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx) {
		AbstractContract<?> contract = ctx.getBusinessObject();
		ContractState toState = ctx.getEndpoint().getDestination();

		checkState(toState == INFORCE);

		telephonyOptionRp.find(contract).stream().filter(to -> ACTIVE.equals(to.getService().getState()))
				.forEach(telephonyOptionRoutingSrv::activate);
	}
}
