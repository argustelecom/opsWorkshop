package ru.argustelecom.box.env.billing.provision;

import static java.util.Optional.ofNullable;

import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class RecurrentTermsParamsDtoTranslator {

	public RecurrentTermsParamsDto translate(RecurrentTerms recurrentTerms) {
		//@formatter:off
		return RecurrentTermsParamsDto.builder()
			.provisionTermsId(recurrentTerms.getId())
			.state(recurrentTerms.getState())
			.periodType(recurrentTerms.getPeriodType())
			.periodUnit(ofNullable(recurrentTerms.getChargingDuration()).map(PeriodDuration::getUnit).orElse(null))
			.amount(ofNullable(recurrentTerms.getChargingDuration()).map(PeriodDuration::getAmount).orElse(null))
			.lifecycleQualifier(recurrentTerms.getSubscriptionLifecycleQualifier())
			.reserveFunds(recurrentTerms.isReserveFunds())
			.roundingPolicy(recurrentTerms.getRoundingPolicy())
			.manualControl(recurrentTerms.isManualControl())
		.build();
		//@formatter:on
	}

}