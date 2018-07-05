package ru.argustelecom.box.env.billing.mediation;

import static java.lang.String.format;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.PERFORMED_BILLING;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.SYNCHRONIZATION;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.billing.invoice.lifecycle.ChargeJobRoutingService;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;

/**
 * Сервис для выполнения синхронизации задания на тарификацию
 * {@link ru.argustelecom.box.env.billing.invoice.model.ChargeJob}. Операция выполняется в новой транзакции. Мыслится
 * как часть {@link MediationSyncService}. Не следует использовать отдельно.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class JobSynchronizerService {

	private static final Logger log = Logger.getLogger(JobSynchronizerService.class);

	@Inject
	private ChargeJobRoutingService routingSvc;

	public void synchronize(ChargeJob job) {
		if (job != null) {
			log.info(format("Next job for synchronization is %s", job));
			log.info(format("Job state: %s", job.getState()));
			log.info(format("Job data type: %s", job.getDataType()));

			if (job.inState(PERFORMED_BILLING)) {
				routingSvc.tryClose(job);
			}

			if (job.inState(SYNCHRONIZATION)) {
				routingSvc.synchronize(job);
			}
		} else {
			log.info("Nothing to synchronize");
		}
	}

}
