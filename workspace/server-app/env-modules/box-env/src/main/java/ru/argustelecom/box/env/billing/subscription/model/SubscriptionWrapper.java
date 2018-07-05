package ru.argustelecom.box.env.billing.subscription.model;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.billing.model.ISubscription;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import static com.google.common.base.Preconditions.checkNotNull;

@Named(value = ISubscription.WRAPPER_NAME)
public class SubscriptionWrapper implements EntityWrapper {

	@PersistenceContext
	private EntityManager em;

	@Override
	public ISubscription wrap(Identifiable entity) {
		checkNotNull(entity);
		Subscription subscription = (Subscription) entity;
		//@formatter:off
		return ISubscription.builder()
					.id(subscription.getId())
					.objectName(subscription.getObjectName())
					.state(new IState(subscription.getState().toString(), subscription.getState().getName()))
					.subjectId(subscription.getSubject().getId())
					.cost(subscription.getCost().getAmount())
					.subjectCauseId(subscription.getSubjectCause().getId())
					.costCauseId(subscription.getCostCause().getId())
					.provisionTermsId(subscription.getProvisionTerms().getId())
					.personalAccountId(subscription.getPersonalAccount().getId())
					.validFrom(subscription.getValidFrom())
					.validTo(subscription.getValidTo())
					.creationDate(subscription.getCreationDate())
					.closeDate(subscription.getCloseDate())
				.build();
		//@formatter:on
	}

	@Override
	public Subscription unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(Subscription.class, iEntity.getId());
	}

	private static final long serialVersionUID = -1597592693881464788L;

}