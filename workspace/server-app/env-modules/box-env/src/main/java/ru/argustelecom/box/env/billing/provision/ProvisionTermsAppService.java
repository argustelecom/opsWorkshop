package ru.argustelecom.box.env.billing.provision;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.provision.model.AbstractProvisionTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class ProvisionTermsAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ProvisionTermsRepository provisionTermsRp;

	public RecurrentTerms createRecurrentTerms(String name, String description) {
		return provisionTermsRp.createRecurrentTerms(name, description);
	}

	public RecurrentTerms createRecurrentTerms(String name, PeriodType periodType, int chargingAmount,
			PeriodUnit chargingUnit, SubscriptionLifecycleQualifier lifecycleQualifier, String description) {
		return provisionTermsRp.createRecurrentTerms(name, periodType, chargingAmount, chargingUnit, lifecycleQualifier,
				description);
	}

	public void changeChargingPeriod(Long recurrentTermsId, PeriodType type, PeriodUnit unit, int amount) {
		RecurrentTerms recurrentTerms = em.find(RecurrentTerms.class, recurrentTermsId);
		recurrentTerms.setPeriodType(type);
		recurrentTerms.setChargingDuration(PeriodDuration.of(amount, unit));
	}

	public void changeLifecycleQualifier(Long recurrentTermsId, SubscriptionLifecycleQualifier qualifier,
			Boolean manualControl) {
		checkNotNull(recurrentTermsId);
		checkNotNull(manualControl);

		RecurrentTerms recurrentTerms = em.find(RecurrentTerms.class, recurrentTermsId);
		if (!Objects.equals(qualifier, recurrentTerms.getSubscriptionLifecycleQualifier())) {
			recurrentTerms.setSubscriptionLifecycleQualifier(qualifier);

			if (qualifier.equals(SubscriptionLifecycleQualifier.SHORT)) {
				recurrentTerms.setManualControl(false);
			}
		}
		if (!Objects.equals(manualControl, recurrentTerms.isManualControl())) {
			recurrentTerms.setManualControl(manualControl);
		}
		if (!Objects.equals(manualControl, recurrentTerms.isManualControl())) {
			recurrentTerms.setManualControl(manualControl);
		}
	}

	public void changeReserveFunds(Long recurrentTermsId, Boolean reserveFunds) {
		checkNotNull(recurrentTermsId);
		checkNotNull(reserveFunds);

		RecurrentTerms recurrentTerms = em.find(RecurrentTerms.class, recurrentTermsId);
		if (!reserveFunds.equals(recurrentTerms.isManualControl())) {
			recurrentTerms.setReserveFunds(reserveFunds);
		}
	}

	public void changeRoundingPolicy(Long recurrentTermsId, RoundingPolicy roundingPolicy) {
		checkNotNull(recurrentTermsId);

		RecurrentTerms recurrentTerms = em.find(RecurrentTerms.class, recurrentTermsId);
		if (!Objects.equals(roundingPolicy, recurrentTerms.getRoundingPolicy())) {
			recurrentTerms.setRoundingPolicy(roundingPolicy);
		}
	}

	public List<AbstractProvisionTerms> findAll() {
		return provisionTermsRp.getAllProvisionTerms();
	}

	private static final long serialVersionUID = 4716034622753380567L;

}