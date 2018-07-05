package ru.argustelecom.box.env.billing.invoice;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.billing.invoice.lifecycle.RechargingChargeJobLifecycle.Route.PERFORM_AT_PRE_BILLING;
import static ru.argustelecom.box.integration.mediation.impl.BillingToMediationWSClient.getEndpoint;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobWrapper;
import ru.argustelecom.box.env.billing.invoice.model.FilterAggData;
import ru.argustelecom.box.env.billing.invoice.model.JobDataType;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.publang.billing.model.IChargeJob;

@ApplicationService
public class ChargeJobAppService implements Serializable {

	private static final long serialVersionUID = -1866769573804878919L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ChargeJobRepository chargeJobRp;

	@Inject
	private ChargeJobWrapper chargeJobWrapper;

	@Inject
	private LifecycleRoutingService lifecycleRoutingSvc;

	public ChargeJob create(JobDataType dataType, FilterAggData filter) {
		checkNotNull(dataType);
		checkNotNull(filter);

		return chargeJobRp.create(dataType, filter);
	}

	public void performRouting(ChargeJob chargeJob, Serializable route) {
		checkNotNull(chargeJob);
		checkNotNull(route);

		lifecycleRoutingSvc.performRouting(chargeJob, route);
	}

	public void doRechargeJob(ChargeJob chargeJob) {
		IChargeJob iChargeJob = chargeJobWrapper.wrap(chargeJob);
		getEndpoint().doRechargeJob(iChargeJob);
		lifecycleRoutingSvc.performRouting(chargeJob, PERFORM_AT_PRE_BILLING);
	}

	public ChargeJob findMostEarlyForSynchronization() {
		return chargeJobRp.findMostEarlyForSynchronization();
	}
}
