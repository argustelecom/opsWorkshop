package ru.argustelecom.box.env.billing.provision;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.Getter;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "recurrentTermsParamsFm")
@PresentationModel
public class RecurrentTermsParamsFrameModel implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ProvisionTermsAppService provisionTermsAs;

	@Inject
	private RecurrentTermsParamsDtoTranslator recurrentTermsParamsDtoTr;

	private RecurrentTerms recurrentTerms;

	@Getter
	private RecurrentTermsParamsDto recurrentTermsParams;

	public void preRender(RecurrentTerms recurrentTerms) {
		checkNotNull(recurrentTerms, "Recurrent terms is required for recurrent terms params frame");
		if (!Objects.equals(this.recurrentTerms, recurrentTerms)) {
			this.recurrentTerms = recurrentTerms;
			initRecurrentTermsParams();
		}
	}

	public void onPeriodTypeChanged() {
		recurrentTermsParams.setPeriodUnit(null);
		recurrentTermsParams.setAmount(0);
	}

	public void onParamEditionCanceled() {
		recurrentTerms = em.find(RecurrentTerms.class, recurrentTerms.getId());
		recurrentTermsParams = recurrentTermsParamsDtoTr.translate(recurrentTerms);
	}

	public void onChargingPeriodChanged() {
		provisionTermsAs.changeChargingPeriod(recurrentTerms.getId(), recurrentTermsParams.getPeriodType(),
				recurrentTermsParams.getPeriodUnit(), recurrentTermsParams.getAmount());
	}

	public void onLifecycleQualifierChanged() {
		provisionTermsAs.changeLifecycleQualifier(recurrentTerms.getId(), recurrentTermsParams.getLifecycleQualifier(),
				recurrentTermsParams.getManualControl());
	}

	public void onReserveFundsChanged() {
		provisionTermsAs.changeReserveFunds(recurrentTerms.getId(), recurrentTermsParams.getReserveFunds());
	}

	public void onRoundingPolicyChanged() {
		provisionTermsAs.changeRoundingPolicy(recurrentTerms.getId(), recurrentTermsParams.getRoundingPolicy());
	}

	public PeriodType[] getPeriodTypes() {
		return PeriodType.availableValues();
	}

	public PeriodUnit[] getPossibleChargingPeriodUnits() {
		return recurrentTermsParams.getPeriodType() == null ? null
				: recurrentTermsParams.getPeriodType().getChargingPeriodUnits();
	}

	public SubscriptionLifecycleQualifier[] getSubscriptionQualifiers() {
		return SubscriptionLifecycleQualifier.availableSubscriptionLifecycleQualifiers();
	}

	private void initRecurrentTermsParams() {
		recurrentTermsParams = recurrentTermsParamsDtoTr.translate(recurrentTerms);
	}

	private static final long serialVersionUID = 142367019737758494L;

}