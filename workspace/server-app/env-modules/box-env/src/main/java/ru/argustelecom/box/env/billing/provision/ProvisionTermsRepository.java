package ru.argustelecom.box.env.billing.provision;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.billing.provision.model.AbstractProvisionTerms;
import ru.argustelecom.box.env.billing.provision.model.AbstractProvisionTerms.AbstractProvisionTermsQuery;
import ru.argustelecom.box.env.billing.provision.model.NonRecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class ProvisionTermsRepository implements Serializable {

	private static final long serialVersionUID = -8947597665016564412L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequenceService;

	public RecurrentTerms createRecurrentTerms(String name, String description) {
		checkNotNull(name);

		RecurrentTerms instance = new RecurrentTerms(idSequenceService.nextValue(RecurrentTerms.class));

		instance.setObjectName(name);
		instance.setDescription(description);

		em.persist(instance);

		return instance;
	}

	public RecurrentTerms createRecurrentTerms(String name, PeriodType periodType, int chargingAmount,
			PeriodUnit chargingUnit, SubscriptionLifecycleQualifier qualifier, String description) {

		//@formatter:off
		return createRecurrentTerms(
			name, 
			periodType,
			PeriodDuration.of(chargingAmount, chargingUnit),
			qualifier,
			description
		); //@formatter:on
	}

	public RecurrentTerms createRecurrentTerms(String name, PeriodType periodType, PeriodDuration chargingDuration,
			SubscriptionLifecycleQualifier qualifier, String description) {

		//@formatter:off
		RecurrentTerms recurrentTerms = new RecurrentTerms.RecurrentTermsBuilder()
			.withId(idSequenceService.nextValue(AbstractProvisionTerms.class))
			.setPeriodType(periodType)
			.setChargingDuration(chargingDuration)
			.setSubscriptionLifecycleQualifier(qualifier)
			.build();
		//@formatter:on

		recurrentTerms.setObjectName(name);
		recurrentTerms.setDescription(description);

		em.persist(recurrentTerms);

		return recurrentTerms;
	}

	public NonRecurrentTerms createNonRecurrentTerms(@NotNull String name) {
		NonRecurrentTerms terms = new NonRecurrentTerms(idSequenceService.nextValue(AbstractProvisionTerms.class));
		terms.setObjectName(name);
		em.persist(terms);
		return terms;
	}

	public void removeRecurrentTerms(RecurrentTerms recurrentTerms) {
		em.remove(recurrentTerms);
	}

	public List<AbstractProvisionTerms> getAllProvisionTerms() {
		return new AbstractProvisionTermsQuery<>(AbstractProvisionTerms.class).createTypedQuery(em).getResultList();
	}

	public List<RecurrentTerms> findRecurrentTerms(
			SubscriptionLifecycleQualifier qualifier, boolean reserveFunds, RecurrentTermsState state
	) {

		RecurrentTerms.RecurrentTermsQuery<RecurrentTerms> query =
				new RecurrentTerms.RecurrentTermsQuery<>(RecurrentTerms.class);

		//@formatter:off
		query.and(
				query.lifecycleQualifier().equal(qualifier),
				query.reserveFunds().equal(reserveFunds),
				query.state().equal(state)
		);
		//@formatter:on

		return query.getResultList(em);
	}
}