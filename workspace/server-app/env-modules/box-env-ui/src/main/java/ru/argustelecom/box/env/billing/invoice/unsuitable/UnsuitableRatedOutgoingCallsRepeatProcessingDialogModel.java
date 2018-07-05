package ru.argustelecom.box.env.billing.invoice.unsuitable;

import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCardViewModel.VIEW_ID;
import static ru.argustelecom.box.env.billing.invoice.lifecycle.RechargingChargeJobLifecycle.Route.PERFORM_AT_PRE_BILLING_FROM_FORMALIZATION;
import static ru.argustelecom.box.env.billing.invoice.model.JobDataType.UNSUITABLE;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.invoice.ChargeJobAppService;
import ru.argustelecom.box.env.billing.invoice.ChargeJobRepository;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.FilterAggData;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.page.PresentationModel;

@Getter
@Setter
@Named(value = "unsuitableRatedOutgoingCallsRepeatProcessingDm")
@PresentationModel
public class UnsuitableRatedOutgoingCallsRepeatProcessingDialogModel implements Serializable {

	private static final String BILLING_STAGE = "billing_stage";

	@Inject
	private ChargeJobRepository chargeJobRp;

	@Inject
	private ChargeJobAppService chargeJobAs;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	private Date dateFrom;
	private Date dateTo;

	public void onDialogOpen() {
		reset();
	}

	public void onCancel() {
		reset();
	}

	private void reset() {
		dateFrom = null;
		dateTo = null;
	}

	public String onCreate() {
		//@formatter:off
		FilterAggData filter = FilterAggData.builder()
					.dateFrom(dateFrom)
					.dateTo(dateTo)
					.processingStage(BILLING_STAGE)
				.build();
		//@formatter:on
		ChargeJob chargeJob = chargeJobAs.create(UNSUITABLE, filter);
		chargeJobAs.performRouting(chargeJob, PERFORM_AT_PRE_BILLING_FROM_FORMALIZATION);
		return outcomeConstructor.construct(VIEW_ID, IdentifiableOutcomeParam.of("chargeJob", chargeJob));
	}

	private static final long serialVersionUID = -1024638645971417319L;
}