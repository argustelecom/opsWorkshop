package ru.argustelecom.box.env.mediation;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.mediation.model.ReleaseCode;
import ru.argustelecom.box.env.mediation.model.UnsuitableCall;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.publang.billing.model.IUnsuitableCall;

import static ru.argustelecom.box.integration.mediation.impl.BillingToMediationWSClient.getEndpoint;

@Repository
public class MediationRepository implements Serializable {

	private static final long serialVersionUID = 497697506220072655L;

	@PersistenceContext
	private EntityManager em;

	public void updateUnsuitableCalls(Long id, Date callDate, Long duration, PeriodUnit cdrUnit, String callingNumber,
			String outgoingChannel, String outgoingTrunk, String calledNumber, String incomingChannel,
			String incomingTrunk, ReleaseCode releaseCode, String callDirection) {

		//Апдейтим view через правило в случае работы с заглушкой
		UnsuitableCall instance = em.find(UnsuitableCall.class, id);
		instance.setCallDate(callDate);
		instance.setDuration(duration);
		instance.setCdrUnit(cdrUnit);
		instance.setCallingNumber(callingNumber);
		instance.setOutgoingChannel(outgoingChannel);
		instance.setOutgoingTrunk(outgoingTrunk);
		instance.setIncomingChannel(incomingChannel);
		instance.setCalledNumber(calledNumber);
		instance.setIncomingTrunk(incomingTrunk);
		instance.setReleaseCode(releaseCode);
		instance.setCallDirection(callDirection);

		em.flush();

		//При работе в режиме интеграции с предбиллингом отправляем изменения через web-сервис
		IUnsuitableCall iUnsuitableCall = IUnsuitableCall.builder()
				.callDate(callDate)
				.callDirection(callDirection)
				.callId(id)
				.calledNumber(calledNumber)
				.callingNumber(callingNumber)
				.cdrUnit(cdrUnit.name())
				.duration(duration.intValue())
				.releaseCode(releaseCode.name())
				.incomingChannel(incomingChannel)
				.incomingTrunk(incomingTrunk)
				.outgoingChannel(outgoingChannel)
				.outgoingTrunk(outgoingTrunk)
				.processingStage(instance.getProcessingStage().name())
				.build();

		getEndpoint().updateUnsuitableCalls(Collections.singletonList(iUnsuitableCall));
	}
}
