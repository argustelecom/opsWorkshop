package ru.argustelecom.box.env.mediation;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.page.PresentationModel;

import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewModel.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@PresentationModel
@Named("unsuitableCallsEditDm")
public class UnsuitableCallsEditDialogModel implements Serializable {

	private static final long serialVersionUID = 4859225802795004186L;

	@Inject
	private MediationAppService mediationAs;

	@Getter
	@Setter
	private UnsuitableCallDto unsuitableCall;

	@Getter
	@Setter
	private UnsuitableCallsContext context;

	public void change() {
		mediationAs.updateUnsuitableCalls(unsuitableCall.getId(), unsuitableCall.getCallDate(), unsuitableCall.getDuration(),
				unsuitableCall.getCdrUnit(), unsuitableCall.getCallingNumber(),
				unsuitableCall.getOutgoingChannel(), unsuitableCall.getOutgoingTrunk(),
				unsuitableCall.getCalledNumber(), unsuitableCall.getIncomingChannel(),
				unsuitableCall.getIncomingTrunk(), unsuitableCall.getReleaseCode(),
				unsuitableCall.getCallDirection());
	}

	public List<PeriodUnit> getPossibleRatedUnits() {
		return Arrays.asList(PeriodUnit.SECOND, PeriodUnit.MINUTE);
	}
}
