package ru.argustelecom.box.env.billing.provision.model;

import static java.util.Optional.ofNullable;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.billing.model.IRecurrentTerms;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Named(value = IRecurrentTerms.WRAPPER_NAME)
public class RecurrentTermsWrapper implements EntityWrapper {

	@PersistenceContext
	private EntityManager em;

	@Override
	public IRecurrentTerms wrap(Identifiable entity) {
		RecurrentTerms recurrentTerms = (RecurrentTerms) entity;
		//@formatter:off
		return IRecurrentTerms.builder()
					.id(recurrentTerms.getId())
					.objectName(recurrentTerms.getObjectName())
					.state(new IState(recurrentTerms.getState().toString(), recurrentTerms.getState().getName()))
					.periodType(ofNullable(recurrentTerms.getPeriodType()).map(PeriodType::name).orElse(null))
					.chargingPeriodUnit(ofNullable(recurrentTerms.getChargingDuration()).map(pd -> pd.getUnit().name()).orElse(null))
					.amount(ofNullable(recurrentTerms.getChargingDuration()).map(PeriodDuration::getAmount).orElse(null))
					.reserveFunds(recurrentTerms.isReserveFunds())
					.roundingPolicy(recurrentTerms.getRoundingPolicy())
					.subscriptionLifecycleQualifier(ofNullable(recurrentTerms.getSubscriptionLifecycleQualifier()).map(SubscriptionLifecycleQualifier::name).orElse(null))
				.build();
		//@formatter:on
	}

	@Override
	public Identifiable unwrap(IEntity iEntity) {
		return em.find(RecurrentTerms.class, iEntity.getId());
	}

	private static final long serialVersionUID = 4030206121001425460L;

}