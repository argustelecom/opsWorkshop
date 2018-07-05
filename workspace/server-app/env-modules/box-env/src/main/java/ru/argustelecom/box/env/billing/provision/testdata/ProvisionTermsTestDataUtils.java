package ru.argustelecom.box.env.billing.provision.testdata;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.provision.ProvisionTermsRepository;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState;
import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;

public class ProvisionTermsTestDataUtils implements Serializable {

    private static final long serialVersionUID = -3013574529942465774L;

    @Inject
    private ProvisionTermsRepository provisionTermsRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * Ищет существующие, или создает условия предоставления.
     */
    public RecurrentTerms findOrCreateTestRecurrentTerms(SubscriptionLifecycleQualifier qualifier, boolean reserveFunds) {

        List<RecurrentTerms> suitableTerms = provisionTermsRepository.findRecurrentTerms(
                qualifier, reserveFunds, RecurrentTermsState.ACTIVE
        );

        if (!suitableTerms.isEmpty()) {
            return suitableTerms.get(0);
        }

        RecurrentTerms recurrentTerms = provisionTermsRepository.createRecurrentTerms(
                "Тестовые условия" + UUID.randomUUID().toString().substring(0, 10),
                PeriodType.CUSTOM,
                PeriodDuration.of(3, PeriodUnit.DAY),
                qualifier,
                "Тестовое описание"
        );
        recurrentTerms.setReserveFunds(reserveFunds);
        recurrentTerms.setState(RecurrentTermsState.ACTIVE);
        recurrentTerms.setRoundingPolicy(RoundingPolicy.UP);
        em.merge(recurrentTerms);

        return recurrentTerms;
    }
}