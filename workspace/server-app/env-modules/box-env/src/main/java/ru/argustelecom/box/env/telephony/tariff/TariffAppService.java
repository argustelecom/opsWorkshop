package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.findList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CustomTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TariffState;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@ApplicationService
public class TariffAppService implements Serializable {

	private static final long serialVersionUID = 6396838962904779242L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TariffRepository tariffRp;

	@Inject
	private TariffEntryService tariffEntrySvc;

	public CommonTariff createCommonTariff(String name, PeriodUnit ratedUnit, RoundingPolicy roundingPolicy,
			Date validFrom, Date validTo) {
		checkArgument(name != null && !name.isEmpty());
		checkNotNull(ratedUnit);
		checkNotNull(roundingPolicy);
		checkNotNull(validFrom);

		return tariffRp.createCommonTariff(name, ratedUnit, roundingPolicy, validFrom, validTo);
	}

	public CustomTariff createCustomTariff(String name, PeriodUnit ratedUnit, RoundingPolicy roundingPolicy,
			Date validFrom, Date validTo, Long parentId, Long customerId) {
		checkArgument(name != null && !name.isEmpty());
		checkNotNull(ratedUnit);
		checkNotNull(roundingPolicy);
		checkNotNull(validFrom);
		checkNotNull(parentId);
		checkNotNull(customerId);

		Customer customer = em.find(Customer.class, customerId);
		checkNotNull(customer);

		CommonTariff parent = em.find(CommonTariff.class, parentId);
		checkNotNull(parent);

		return tariffRp.createCustomTariff(name, ratedUnit, roundingPolicy, validFrom, validTo, parent, customer);
	}

	public List<AbstractTariff> findActiveTariffs() {
		return tariffRp.findActiveTariffs();
	}

	public List<AbstractTariff> findBy(Collection<TariffState> states) {
		return tariffRp.findBy(states);
	}

	public List<AbstractTariff> findNonFormalizationTariffs() {
		return tariffRp.findNonFormalizationTariffs();
	}

	public void removeTariff(Long tariffId) {
		checkNotNull(tariffId);

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		tariffRp.removeTariff(tariff);
	}

	public List<CommonTariff> findAvailableCommonTariffs() {
		return tariffRp.findAvailableCommonTariffs();
	}

	public void updateTariffName(Long tariffId, String name) {
		checkNotNull(tariffId);
		checkArgument(name != null && !name.isEmpty());

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		tariffRp.updateTariffName(tariff, name);
	}

	public void updateTariffDates(Long tariffId, Date validFrom, Date validTo) {
		checkNotNull(validFrom);

		if (validTo != null) {
			checkArgument(validTo.after(validFrom));
		}

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		tariffRp.updateTariffDates(tariff, validFrom, validTo);
	}

	public void updateTariffRoundPolicy(Long tariffId, RoundingPolicy roundingPolicy) {
		checkNotNull(tariffId);
		checkNotNull(roundingPolicy);

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		tariffRp.updateTariffRoundPolicy(tariff, roundingPolicy);
	}

	public void updateTariffRatedUnit(Long tariffId, PeriodUnit ratedUnit) {
		checkNotNull(tariffId);
		checkNotNull(ratedUnit);
		checkArgument(ratedUnit.equals(PeriodUnit.SECOND) || ratedUnit.equals(PeriodUnit.MINUTE));

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		tariffRp.updateTariffRatedUnit(tariff, ratedUnit);
	}

	public List<CustomTariff> findCustomTariffByCommonTariffByPrefix(Long tariffId, List<Integer> prefix) {
		checkNotNull(tariffId);
		checkArgument(prefix != null && !prefix.isEmpty());

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		return findCustomTariffByCommonTariffByPrefix(tariff, prefix);
	}

	private List<CustomTariff> findCustomTariffByCommonTariffByPrefix(AbstractTariff tariff, List<Integer> prefix) {
		List<AbstractTariff> allTariffs = findList(em, AbstractTariff.class, tariffEntrySvc
				.findByPrefixes(tariff, prefix).stream().map(TariffEntryQueryResult::getTariffId).collect(toList()));

		List<CustomTariff> customTariffs = new ArrayList<>();
		for (AbstractTariff abstractTariff : allTariffs) {
			if (EntityManagerUtils.initializeAndUnproxy(abstractTariff) instanceof CustomTariff) {
				customTariffs.add((CustomTariff) EntityManagerUtils.initializeAndUnproxy(abstractTariff));
			}
		}

		return customTariffs;
	}

	public List<CustomTariff> findAllCustomTariffsByCommonTariff(Long tariffId) {
		checkNotNull(tariffId);

		CommonTariff tariff = em.find(CommonTariff.class, tariffId);

		return tariffRp.findAllCustomTariffsByCommonTariff(tariff);
	}

	public AbstractTariff findById(Long tariffId) {
		checkNotNull(tariffId);

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		return tariff;
	}
}
