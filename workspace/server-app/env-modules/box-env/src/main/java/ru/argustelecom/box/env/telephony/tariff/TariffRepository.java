package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.telephony.tariff.model.TariffState.ACTIVE;
import static ru.argustelecom.box.env.telephony.tariff.model.TariffState.CANCELLED;
import static ru.argustelecom.box.env.telephony.tariff.model.TariffState.FORMALIZATION;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff.TariffQuery;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CustomTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CustomTariff.CustomTariffQuery;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntry;
import ru.argustelecom.box.env.telephony.tariff.model.TariffState;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

/**
 * Репозиторий для работы с тарифами
 */
@Repository
public class TariffRepository implements Serializable {

	private static final long serialVersionUID = 4991863878082315765L;

	private static final String BY_STATES_QUERY = "TariffRepository.byStates";
	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private TariffEntryService tariffEntryDs;

	public CommonTariff createCommonTariff(String name, PeriodUnit ratedUnit, RoundingPolicy roundingPolicy,
			Date validFrom, Date validTo) {
		checkNotNull(name);
		checkNotNull(ratedUnit);
		checkNotNull(roundingPolicy);
		checkNotNull(validFrom);

		CommonTariff newCommonTariff = new CommonTariff(idSequence.nextValue(CommonTariff.class));

		newCommonTariff.setObjectName(name);
		newCommonTariff.setRatedUnit(ratedUnit);
		newCommonTariff.setRoundingPolicy(roundingPolicy);
		newCommonTariff.setValidFrom(validFrom);
		newCommonTariff.setValidTo(validTo);

		em.persist(newCommonTariff);

		return newCommonTariff;
	}

	public CustomTariff createCustomTariff(String name, PeriodUnit ratedUnit, RoundingPolicy roundingPolicy,
			Date validFrom, Date validTo, CommonTariff parent, Customer customer) {
		checkNotNull(name);
		checkNotNull(ratedUnit);
		checkNotNull(roundingPolicy);
		checkNotNull(validFrom);
		checkNotNull(customer);
		checkNotNull(parent);

		CustomTariff newCustomTariff = new CustomTariff(idSequence.nextValue(CustomTariff.class), customer);

		copyCommonEntries(parent, newCustomTariff);
		newCustomTariff.setParent(parent);
		newCustomTariff.setObjectName(name);
		newCustomTariff.setRatedUnit(ratedUnit);
		newCustomTariff.setRoundingPolicy(roundingPolicy);
		newCustomTariff.setValidFrom(validFrom);
		newCustomTariff.setValidTo(validTo);

		em.persist(newCustomTariff);

		return newCustomTariff;
	}

	private void copyCommonEntries(CommonTariff parentTariff, CustomTariff tariff) {
		for (TariffEntry entry : parentTariff.getEntries()) {
			tariff.addEntry(tariffEntryDs.create(tariff, entry.getZone(), entry.getObjectName(), entry.getPrefixes(),
					entry.getChargePerUnit(), false));
		}
	}

	public List<AbstractTariff> findAllTariffs() {
		return new TariffQuery<>(AbstractTariff.class).getResultList(em);
	}

	public List<AbstractTariff> findActiveTariffs() {
		Date currentDate = new Date();
		TariffQuery<AbstractTariff> query = new TariffQuery<>(AbstractTariff.class);
		query.and(query.validTo().greaterThen(currentDate)).or(query.validTo().isNull())
				.or(query.validTo().greaterThen(currentDate)).and(query.state().equal(ACTIVE));

		return query.getResultList(em);
	}

	@NamedQuery(name = BY_STATES_QUERY, query = "from AbstractTariff where state in (:states)")
	public List<AbstractTariff> findBy(Collection<TariffState> states) {
		checkArgument(states != null && !states.isEmpty());

		return em.createNamedQuery(BY_STATES_QUERY, AbstractTariff.class).setParameter("states", states)
				.getResultList();
	}

	public List<CommonTariff> findAvailableCommonTariffs() {
		Date currentDate = new Date();
		TariffQuery<CommonTariff> query = new TariffQuery<>(CommonTariff.class);
		query.and(query.validTo().greaterThen(currentDate)).or(query.validTo().isNull())
				.or(query.validTo().greaterThen(currentDate)).and(query.state().equal(ACTIVE));

		return query.getResultList(em);
	}

	public List<CustomTariff> findAllTariffsByCustomer(Customer customer) {
		checkNotNull(customer);

		CustomTariffQuery query = new CustomTariffQuery();
		query.and(query.customer().equal(customer));

		return query.getResultList(em);
	}

	/**
	 * Возвращает список индивидуальных тарифов, созданых на основании данного публичного тарифа и в состоянии state
	 *
	 * @param parent
	 *            публичный тариф
	 * @param state
	 *            состояние тарифа
	 * @return все тарифы, созданные на основании parent
	 */
	public List<CustomTariff> findBy(CommonTariff parent, TariffState state) {
		checkNotNull(parent);
		checkNotNull(state);
		CustomTariffQuery query = new CustomTariffQuery();
		query.and(query.parentTariff().equal(parent), query.state().equal(state));
		return query.getResultList(em);
	}

	public void removeTariff(AbstractTariff tariff) {
		checkNotNull(tariff);
		checkState(CANCELLED.equals(tariff.getState()));

		em.remove(tariff);
	}

	public void updateTariffName(AbstractTariff tariff, String name) {
		checkNotNull(tariff);
		checkArgument(name != null && !name.isEmpty());

		tariff.setObjectName(name);
	}

	public void updateTariffDates(AbstractTariff tariff, Date validFrom, Date validTo) {
		checkNotNull(tariff);
		checkNotNull(validFrom);

		if (validTo != null) {
			checkArgument(validTo.after(validFrom));
		}

		tariff.setValidFrom(validFrom);
		tariff.setValidTo(validTo);
	}

	public void updateTariffRoundPolicy(AbstractTariff tariff, RoundingPolicy roundingPolicy) {
		checkNotNull(tariff);
		checkNotNull(roundingPolicy);

		tariff.setRoundingPolicy(roundingPolicy);
	}

	public void updateTariffRatedUnit(AbstractTariff tariff, PeriodUnit ratedUnit) {
		checkNotNull(tariff);
		checkNotNull(ratedUnit);
		checkArgument(ratedUnit.equals(PeriodUnit.SECOND) || ratedUnit.equals(PeriodUnit.MINUTE));

		tariff.setRatedUnit(ratedUnit);
	}

	public List<CustomTariff> findAllCustomTariffsByCommonTariff(CommonTariff tariff) {
		checkNotNull(tariff);

		CustomTariffQuery query = new CustomTariffQuery();

		query.and(query.parentTariff().equal(tariff));
		query.and(query.state().equal(ACTIVE));

		return query.getResultList(em);
	}

	public List<AbstractTariff> findNonFormalizationTariffs() {
		TariffQuery<AbstractTariff> query = new TariffQuery<>(AbstractTariff.class);
		query.and(query.state().notEqual(FORMALIZATION));

		return query.getResultList(em);
	}
}
