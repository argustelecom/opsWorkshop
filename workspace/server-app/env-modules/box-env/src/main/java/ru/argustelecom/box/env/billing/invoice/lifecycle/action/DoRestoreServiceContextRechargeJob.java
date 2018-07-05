package ru.argustelecom.box.env.billing.invoice.lifecycle.action;

import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.billing.invoice.queue.RechargeHandler.HANDLER_NAME;
import static ru.argustelecom.box.inf.queue.api.QueueProducer.Priority.MEDIUM;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.billing.invoice.queue.RechargeContext;
import ru.argustelecom.box.env.billing.invoice.queue.RechargeHandler;
import ru.argustelecom.box.env.commodity.RatedOutgoingCallsRechargeContext;
import ru.argustelecom.box.env.commodity.RatedOutgoingCallsRepository;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.queue.api.QueueProducer;
import ru.argustelecom.box.inf.queue.api.context.EntityReference;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@LifecycleBean
public class DoRestoreServiceContextRechargeJob implements LifecycleCdiAction<ChargeJobState, ChargeJob> {

	@Inject
	private RatedOutgoingCallsRepository ratedOutgoingCallsRp;

	@Inject
	private QueueProducer queueProducer;

	@PersistenceContext
	private EntityManager em;

	@Override
	public void execute(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {
		ChargeJob job = ctx.getBusinessObject();

		List<RatedOutgoingCallsRechargeContext> rocContext = ratedOutgoingCallsRp
				.restoreServiceContext(job.getMediationId());

		// проинициализируем сразу все услуги, чтобы дальнейшие em.find не провоцировали хиты в БД
		List<Long> ids = rocContext.stream().map(RatedOutgoingCallsRechargeContext::getServiceId).collect(toList());
		EntityManagerUtils.findList(em, Service.class, ids);

		EntityReference<ChargeJob> chargeJobRef = new EntityReference<>(job);
		rocContext.forEach(c -> {
			Service service = em.find(Service.class, c.getServiceId());
			String queueId = RechargeHandler.genQueueName(service);
			RechargeContext context = new RechargeContext(chargeJobRef, new EntityReference<>(service),
					c.getMinCallDate(), c.getMaxCallDate());

			queueProducer.remove(queueId);
			queueProducer.schedule(queueId, job.getId().toString(), MEDIUM, new Date(), HANDLER_NAME, context);
		});
	}
}
