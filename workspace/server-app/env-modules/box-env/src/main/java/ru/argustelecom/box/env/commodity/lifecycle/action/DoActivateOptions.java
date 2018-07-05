package ru.argustelecom.box.env.commodity.lifecycle.action;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.commodity.model.ServiceState.ACTIVE;
import static ru.argustelecom.box.env.contract.model.ContractState.INFORCE;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.ServiceState;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionRepository;
import ru.argustelecom.box.env.commodity.telephony.lifecycle.TelephonyOptionRoutingService;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

@LifecycleBean
public class DoActivateOptions implements LifecycleCdiAction<ServiceState, Service> {

	@Inject
	private TelephonyOptionRoutingService telephonyOptionRoutingSrv;

	@Inject
	private TelephonyOptionRepository telephonyOptionRp;

	@Override
	public void execute(ExecutionCtx<ServiceState, ? extends Service> ctx) {
		Service service = ctx.getBusinessObject();
		ServiceState toState = ctx.getEndpoint().getDestination();

		checkState(toState == ACTIVE);

		AbstractContract<?> contract = initializeAndUnproxy(service.getSubject().getContract());

		if (contract.getState() == INFORCE) {
			//@formatter:off
			telephonyOptionRp.find(service).stream()
					.filter(option -> option.getSubject().getContract().getState().equals(INFORCE))
					.forEach(telephonyOptionRoutingSrv::activate);
			//@formatter:on
		}
	}
}
