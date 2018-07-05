package ru.argustelecom.box.env.commodity.lifecycle.action;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.commodity.model.ServiceState.ACTIVE;

import java.time.LocalDateTime;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.invoice.UsageInvoiceSettingsRepository;
import ru.argustelecom.box.env.billing.invoice.queue.ChargeContext;
import ru.argustelecom.box.env.billing.invoice.queue.ChargeHandler;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.ServiceState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.queue.api.QueueProducer;

@LifecycleBean
public class DoScheduleServiceCharging implements LifecycleCdiAction<ServiceState, Service> {

	@Inject
	private QueueProducer producer;

	@Inject
	private UsageInvoiceSettingsRepository settingsRp;

	@Override
	public void execute(ExecutionCtx<ServiceState, ? extends Service> ctx) {
		Service service = ctx.getBusinessObject();
		ServiceState toState = ctx.getEndpoint().getDestination();

		checkState(toState == ACTIVE);

		ChargeContext eventCtx = new ChargeContext(service);
		String queueName = ChargeHandler.genQueueName(service);

		producer.remove(queueName);
		producer.schedule(queueName, null, QueueProducer.Priority.MEDIUM,
				settingsRp.find().nextScheduledTime(LocalDateTime.now()), ChargeHandler.HANDLER_NAME, eventCtx);
	}

}