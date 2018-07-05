package ru.argustelecom.box.env.commodity.telephony;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionState.INACTIVE;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption.TelephonyOptionQuery;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionSpec;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

/**
 * Репозиторий для работы с {@linkplain TelephonyOptionType опциями телефонии}.
 */
@Repository
public class TelephonyOptionRepository implements Serializable {

	private static final long serialVersionUID = -4887762530141599728L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TypeFactory typeFactory;

	/**
	 * Создание экземпляра опции телефонии.
	 * 
	 * @param type
	 *            тип опции телефонии
	 * @param service
	 *            ссылка на услугу, для которой создается опция
	 * @param contractEntry
	 *            ссылка на запись договора, по которому предоставляется опция
	 * @param tariff
	 *            ссылка на тарифный план, по которому предоставляется опция
	 *
	 * @return экземпляр созданной опции телефонии
	 */
	public TelephonyOption createTelephonyOption(TelephonyOptionType type, Service service, ContractEntry contractEntry,
			AbstractTariff tariff) {
		checkNotNull(type);
		checkRequiredArgument(service, "Service");
		checkRequiredArgument(contractEntry, "ContractEntry");
		checkRequiredArgument(tariff, "Tariff");

		TelephonyOption instance = typeFactory.createInstance(type, TelephonyOption.class);
		instance.setSubject(contractEntry);
		instance.setService(service);
		instance.setTariff(tariff);
		instance.setState(INACTIVE);

		em.persist(instance);

		return instance;
	}

	/**
	 * Создание экземпляра опции телефонии по её спецификации.
	 *
	 * @param optionSpec
	 *            спецификация опции телефонии
	 * @param service
	 *            ссылка на услугу, для которой создается опция
	 * @param contractEntry
	 *            ссылка на запись договора, по которому предоставляется опция
	 * @param tariff
	 *            ссылка на тарифный план, по которому предоставляется опция
	 *
	 * @return экземпляр созданной опции телефонии
	 */
	public TelephonyOption createTelephonyOptionBySpec(TelephonyOptionSpec optionSpec, Service service,
			ContractEntry contractEntry, AbstractTariff tariff) {
		checkNotNull(optionSpec);
		checkRequiredArgument(service, "Service");
		checkRequiredArgument(contractEntry, "ContractEntry");
		checkRequiredArgument(tariff, "Tariff");

		TelephonyOption instance = typeFactory.createInstanceByProto(optionSpec, TelephonyOption.class);
		instance.setSubject(contractEntry);
		instance.setService(service);
		instance.setTariff(tariff);
		instance.setState(INACTIVE);

		em.persist(instance);

		return instance;
	}

	/**
	 * Поиск экземпляров опций телефонии по записи договора.
	 *
	 * @param contractEntry
	 *            ссылка на запись договора, по которому предоставляются опции
	 *
	 * @return коллекция экземпляров найденных опций телефонии
	 */
	public List<TelephonyOption> find(ContractEntry contractEntry) {
		checkNotNull(contractEntry);

		TelephonyOptionQuery<TelephonyOption> query = new TelephonyOptionQuery<>(TelephonyOption.class);
		return query.and(query.subject().equal(contractEntry)).getResultList(em);
	}

	private static final String FIND_TELEPHONY_OPTIONS_BY_CONTRACT = "TelephonyOptionRepository.findTelephonyOptionsByContract";

	/**
	 * Поиск экземпляров опций телефонии по договору.
	 *
	 * @param contract
	 *            договор, по которому предоставляются опции
	 *
	 * @return коллекция экземпляров найденных опций телефонии
	 */
	@NamedQuery(name = FIND_TELEPHONY_OPTIONS_BY_CONTRACT, query = "from TelephonyOption to where to.subject in (:contractEntries)")
	public List<TelephonyOption> find(AbstractContract<?> contract) {
		checkNotNull(contract);

		if (contract.getEntries().isEmpty()) {
			return new ArrayList<>();
		}

		return em.createNamedQuery(FIND_TELEPHONY_OPTIONS_BY_CONTRACT, TelephonyOption.class)
				.setParameter("contractEntries", contract.getEntries()).getResultList();
	}

	/**
	 * Поиск экземпляров опций телефонии по тарифному плану.
	 *
	 * @param tariff
	 *            ссылка на тарифный план, по которому предоставляется опция
	 *
	 * @return коллекция экземпляров найденных опций телефонии
	 */
	public List<TelephonyOption> find(AbstractTariff tariff) {
		checkNotNull(tariff);

		TelephonyOptionQuery<TelephonyOption> query = new TelephonyOptionQuery<>(TelephonyOption.class);
		return query.and(query.tariff().equal(tariff)).getResultList(em);
	}

	/**
	 * Поиск экземпляров опций телефонии по услуге.
	 *
	 * @param service
	 *            ссылка на услугу, по которой предоставляется опция
	 *
	 * @return коллекция экземпляров найденных опций телефонии
	 */
	public List<TelephonyOption> find(Service service) {
		checkNotNull(service);

		TelephonyOptionQuery<TelephonyOption> query = new TelephonyOptionQuery<>(TelephonyOption.class);
		return query.and(query.service().equal(service)).getResultList(em);
	}

	/**
	 * Поиск экземпляров опций телефонии по услуге и позиции договора
	 *
	 * @param service
	 *            ссылка на услугу, по которой предоставляется опция
	 * @param contractEntry
	 *            ссылка на позицию договора, по которому предоставляются опции
	 *
	 * @return коллекция экземпляров найденных опций телефонии
	 */
	public List<TelephonyOption> find(Service service, ContractEntry contractEntry) {
		checkNotNull(service);
		checkNotNull(contractEntry);

		TelephonyOptionQuery<TelephonyOption> query = new TelephonyOptionQuery<>(TelephonyOption.class);
		return query.and(query.service().equal(service), query.subject().equal(contractEntry)).getResultList(em);
	}

	/**
	 * 
	 * @return спислк всех опций телефонии
	 */
	public List<TelephonyOption> findAll() {
		return new TelephonyOptionQuery<>(TelephonyOption.class).getResultList(em);
	}

	public void remove(TelephonyOption option) {
		checkRequiredArgument(option, "TelephonyOption");

		em.remove(option);
	}
}