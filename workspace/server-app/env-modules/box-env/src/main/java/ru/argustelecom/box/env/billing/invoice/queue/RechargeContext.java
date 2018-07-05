package ru.argustelecom.box.env.billing.invoice.queue;

import java.util.Date;

import javax.persistence.EntityManager;

import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.queue.api.context.Context;
import ru.argustelecom.box.inf.queue.api.context.EntityReference;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;

/**
 * Контекст выполнения события перетарификации фактов использования телефонии.
 */
public class RechargeContext extends Context {

	private EntityReference<ChargeJob> rechargeJobRef;

	private EntityReference<Service> serviceRef;

	@Getter
	private Date minDate;

	@Getter
	private Date maxDate;

	public RechargeContext(QueueEventImpl event) {
		super(event);
	}

	public RechargeContext(EntityReference<ChargeJob> rechargeJobRef, EntityReference<Service> serviceRef, Date minDate,
			Date maxDate) {
		super();
		this.rechargeJobRef = rechargeJobRef;
		this.serviceRef = serviceRef;
		this.minDate = minDate;
		this.maxDate = maxDate;
	}

	public ChargeJob getRechargeJob(EntityManager em) {
		return rechargeJobRef.get(em);
	}

	public Service getService(EntityManager em) {
		return serviceRef.get(em);
	}

	private static final long serialVersionUID = -2165276389640383517L;

}