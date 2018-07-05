package ru.argustelecom.box.env.billing.mediation;

import java.util.Date;

import javax.persistence.EntityManager;

import lombok.Getter;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.queue.api.context.Context;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;

@Getter
public class RechargeJobContext extends Context {

	private static final long serialVersionUID = 5999950278065787947L;

	private Long jobId;
	private Long serviceId;
	private Date minDate;
	private Date maxDate;

	protected RechargeJobContext(QueueEventImpl event) {
		super(event);
	}

	public RechargeJobContext(Long jobId, Long serviceId, Date minDate, Date maxDate) {
		super();
		this.jobId = jobId;
		this.serviceId = serviceId;
		this.minDate = minDate;
		this.maxDate = maxDate;
	}

	public Service getService(EntityManager em) {
		return em.find(Service.class, serviceId);
	}

}
