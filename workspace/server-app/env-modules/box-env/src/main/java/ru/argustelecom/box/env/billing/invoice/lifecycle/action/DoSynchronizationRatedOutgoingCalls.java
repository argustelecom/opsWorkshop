package ru.argustelecom.box.env.billing.invoice.lifecycle.action;

import static java.lang.String.format;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.commodity.RatedOutgoingCallsRepository;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

@LifecycleBean
public class DoSynchronizationRatedOutgoingCalls implements LifecycleCdiAction<ChargeJobState, ChargeJob> {

	private static final Logger log = Logger.getLogger(DoSynchronizationRatedOutgoingCalls.class);

	@Inject
	private RatedOutgoingCallsRepository ratedOutgoingCallsRp;

	@Override
	public void execute(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {
		// Выполним синхронизацию данных между модулями Предбиллинг и Биллинг: данные об актуальных вызовах в
		// таблице rated_outgoing_calls обновляются данными из "буферной" таблицы rated_outgoing_calls_buffer

		ChargeJob job = ctx.getBusinessObject();

		int syncCount = ratedOutgoingCallsRp.sync(job.getMediationId());
		log.info(format("Sync: %d rows by chargeJobId = %s", syncCount, job.getMediationId()));
	}

}
