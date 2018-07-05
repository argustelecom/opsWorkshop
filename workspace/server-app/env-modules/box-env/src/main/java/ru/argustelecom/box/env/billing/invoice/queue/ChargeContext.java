package ru.argustelecom.box.env.billing.invoice.queue;

import javax.persistence.EntityManager;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.queue.api.context.Context;
import ru.argustelecom.box.inf.queue.api.context.EntityReference;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;

/**
 * Контекст выполнения события тарификации фактов использования телефонии.
 */
public class ChargeContext extends Context {

	private EntityReference<Service> serviceRef;

	public ChargeContext(QueueEventImpl event) {
		super(event);
	}

	public ChargeContext(Service service) {
		super();
		this.serviceRef = new EntityReference<>(service);
	}

	public Service getService(EntityManager em) {
		return serviceRef.get(em);
	}

	private static final long serialVersionUID = -6670047648236162611L;

}