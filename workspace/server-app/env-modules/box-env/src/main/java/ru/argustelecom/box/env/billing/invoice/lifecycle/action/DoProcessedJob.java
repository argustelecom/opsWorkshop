package ru.argustelecom.box.env.billing.invoice.lifecycle.action;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobWrapper;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.publang.billing.model.IChargeJob;

import static ru.argustelecom.box.integration.mediation.impl.BillingToMediationWSClient.getEndpoint;

@LifecycleBean
public class DoProcessedJob implements LifecycleCdiAction<ChargeJobState, ChargeJob> {

	@Inject
	private ChargeJobWrapper chargeJobWrapper;

	@Override
	public void execute(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {
		//Проинформируем предбиллинг о возможности очистки буфера

		ChargeJob job = ctx.getBusinessObject();

		IChargeJob iChargeJob = chargeJobWrapper.wrap(job);
		getEndpoint().processedJob(iChargeJob);
	}

}
