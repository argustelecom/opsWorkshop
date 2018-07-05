package ru.argustelecom.box.env.contract.lifecycle.action;

import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionRepository;
import ru.argustelecom.box.env.commodity.telephony.lifecycle.TelephonyOptionRoutingService;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

@LifecycleBean
public class DoDeactivateExcludedOptions implements LifecycleCdiAction<ContractState, AbstractContract<?>> {

	@Inject
	private TelephonyOptionRoutingService telephonyOptionRoutingSrv;

	@Inject
	private TelephonyOptionRepository telephonyOptionRp;

	@Override
	public void execute(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx) {
		ContractExtension extension = (ContractExtension) ctx.getBusinessObject();
		extension.getExcludedEntries().forEach(excludedEntry -> {
			List<TelephonyOption> options = telephonyOptionRp.find(excludedEntry);
			options.forEach(telephonyOptionRoutingSrv::deactivate);
		});

	}

}
