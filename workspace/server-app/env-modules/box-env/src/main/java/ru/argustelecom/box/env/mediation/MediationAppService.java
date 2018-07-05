package ru.argustelecom.box.env.mediation;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;

import ru.argustelecom.box.env.mediation.model.ReleaseCode;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class MediationAppService implements Serializable {

	private static final long serialVersionUID = 5858238243557867434L;

	@Inject
	private MediationRepository mediationRp;

	public void updateUnsuitableCalls(Long id, Date callDate, Long duration, PeriodUnit cdrUnit, String callingResource,
			String outgoingChannel, String outgoingTrunk, String calledResource, String incomingChannel,
			String incomingTrunk, ReleaseCode releaseCode, String callDirection) {

		mediationRp.updateUnsuitableCalls(id, callDate, duration, cdrUnit, callingResource, outgoingChannel, outgoingTrunk,
				calledResource, incomingChannel, incomingTrunk, releaseCode, callDirection);
	}
}
