package ru.argustelecom.box.env.contract.lifecycle.action;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.contract.model.ContractState.CANCELLED;
import static ru.argustelecom.box.env.contract.model.ContractState.REGISTRATION;
import static ru.argustelecom.box.env.contract.model.ContractState.TERMINATED;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.ImmutableSet;

import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionRepository;
import ru.argustelecom.box.env.commodity.telephony.lifecycle.TelephonyOptionRoutingService;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

@LifecycleBean
public class DoDeactivateOptions implements LifecycleCdiAction<ContractState, AbstractContract<?>> {

	private static final Set<ContractState> INTERESTING_STATES = ImmutableSet.of(REGISTRATION, TERMINATED, CANCELLED);

	@Inject
	private TelephonyOptionRoutingService telephonyOptionRoutingSrv;

	@Inject
	private TelephonyOptionRepository telephonyOptionRp;

	@Override
	public void execute(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx) {
		AbstractContract<?> contract = ctx.getBusinessObject();
		ContractState toState = ctx.getEndpoint().getDestination();

		checkState(INTERESTING_STATES.contains(toState));

		List<TelephonyOption> options = telephonyOptionRp.find(contract);
		options.forEach(telephonyOptionRoutingSrv::deactivate);
	}
}