package ru.argustelecom.box.env.contract;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.commodity.CommodityRepository;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.Service.ServiceQuery;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionRepository;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionSpecRepository;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionSpec;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.contract.model.OptionContractEntry;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.pricing.PricelistRepository;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.util.SecurityUtils;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.integration.nri.ResourceBookingService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@Repository
public class ContractEntryRepository implements Serializable {

	private static final long serialVersionUID = 6075975971003473480L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private CommodityRepository commodityRp;

	@Inject
	private SubscriptionRepository subscriptionRp;

	@Inject
	private PricelistRepository pricelistRp;

	@Inject
	private TelephonyOptionSpecRepository telephonyOptionSpecRp;

	@Inject
	private TelephonyOptionRepository telephonyOptionRp;

	@Inject
	private ResourceBookingService rbs;

	/**
	 * Создаёт {@linkplain ProductOfferingContractEntry позицию договора} на основании {@linkplain ProductOffering
	 * продуктового предложения} и услуги по всем типам услуг, входящих в состав продуктового предложения.
	 * 
	 * @see #createServices(ProductOfferingContractEntry)
	 */
	public ProductOfferingContractEntry createProductOfferingEntry(AbstractContract<?> contract,
			ProductOffering productOffering, Location location, PersonalAccount personalAccount) {

		checkRequiredArgument(contract, "contract");
		checkRequiredArgument(productOffering, "productOffering");
		checkRequiredArgument(location, "location");

		Long entryId = idSequence.nextValue(ProductOfferingContractEntry.class);
		ProductOfferingContractEntry entry = new ProductOfferingContractEntry(entryId);

		entry.setProductOffering(productOffering);
		entry.addLocation(location);
		entry.setPersonalAccount(personalAccount);
		contract.addEntry(entry);

		em.persist(entry);

		createServices(entry);

		em.flush();
		return entry;
	}

	public OptionContractEntry createOptionContractEntry(AbstractContract<?> contract, Service service,
			TelephonyOptionType optionType, AbstractTariff tariff) {
		checkRequiredArgument(contract, "contract");
		checkRequiredArgument(service, "service");
		checkRequiredArgument(optionType, "optionType");
		checkRequiredArgument(tariff, "tariff");

		Long entryId = idSequence.nextValue(OptionContractEntry.class);
		OptionContractEntry entry = new OptionContractEntry(entryId);

		contract.addEntry(entry);

		em.persist(entry);

		TelephonyOption telephonyOption = telephonyOptionRp.createTelephonyOption(optionType, service, entry, tariff);
		entry.addOption(telephonyOption);

		em.flush();
		return entry;
	}

	public void removeEntry(ContractEntry entry) {
		checkNotNull(entry);
		checkState(entry.getContract().inState(ContractState.REGISTRATION),
				"Can not remove contract entry because contract state do not equals registration");

		em.lock(entry.getContract(), LockModeType.OPTIMISTIC);
		ContractEntry initializedEntry = EntityManagerUtils.initializeAndUnproxy(entry);
		if (initializedEntry instanceof ProductOfferingContractEntry) {
			removeProductOfferingEntry((ProductOfferingContractEntry) initializedEntry);
		} else if (initializedEntry instanceof OptionContractEntry) {
			removeOptionEntry((OptionContractEntry) initializedEntry);
		} else {
			throw new SystemException("Unknown contract entry");
		}
	}

	/**
	 * Удаляет позицию договора, со всеми услугами, для которых она является {@linkplain Service#subject основанием}.
	 */
	private void removeProductOfferingEntry(ProductOfferingContractEntry entry) {
		// удаляем только опции связанные с ContractEntry т.к. у услуги могут быть опции, которые предоставляются на
		// основании другой позиции договора
		List<TelephonyOption> options = telephonyOptionRp.find(entry);
		options.forEach(em::remove);

		List<Service> servicesForSubject = findServicesBySubject(entry);
		if (SecurityUtils.isNriIntegrationEnabled()) {
			servicesForSubject.forEach(rbs::releaseBooking);
		}
		servicesForSubject.forEach(em::remove);

		entry.getContract().removeEntry(entry);
	}

	private void removeOptionEntry(OptionContractEntry entry) {
		List<TelephonyOption> options = telephonyOptionRp.find(entry);
		options.forEach(em::remove);

		entry.getContract().removeEntry(entry);
	}

	/**
	 * Создаёт услуги на основании позиции договора. Услуги создаются для всех
	 * {@linkplain ru.argustelecom.box.env.commodity.model.ServiceType типов услуг} входнящих в состав
	 * {@linkplain ProductOffering продуктового предложения}.
	 */
	private List<Service> createServices(ProductOfferingContractEntry subject) {
		List<Service> services = new ArrayList<>();
		subject.getProductOffering().getProductType().collectServiceSpecs().forEach(serviceSpec -> {
			Service newService = commodityRp.createServiceBySpec(serviceSpec, subject);
			services.add(newService);

			// создание опций
			List<TelephonyOptionSpec> optionSpecs = telephonyOptionSpecRp.findByServiceSpec(serviceSpec);
			optionSpecs.forEach(optionSpec -> {
				TelephonyOption option = telephonyOptionRp.createTelephonyOptionBySpec(optionSpec, newService, subject, optionSpec.getTariff());
				subject.addOption(option);
			});
		});
		return services;
	}

	/**
	 * Возвращает все услуги предоставляемые, на определённом основании.
	 */
	public List<Service> findServicesBySubject(ProductOfferingContractEntry subject) {
		ServiceQuery<Service> query = new ServiceQuery<>(Service.class);
		query.and(query.subject().equal(subject));
		return query.getResultList(em);
	}

	private static final String FIND_SERVICES_BY_SUBJECT = "ContractEntryRepository.findServicesBySubject";

	/**
	 * Возвращает все услуги предоставляемые, на определённом основании.
	 */
	@NamedQuery(name = FIND_SERVICES_BY_SUBJECT, query = "from Service s where s.subject in (:subjects)")
	public List<Service> findServicesBySubject(AbstractContract<?> contract) {
		return em.createNamedQuery(FIND_SERVICES_BY_SUBJECT, Service.class)
				.setParameter("subjects", contract.getProductOfferingEntries()).getResultList();
	}

	/**
	 * Возвращает список позиций, которые могут быть исключены в рамках доп. соглашения. Исключить можно любую позицию
	 * договора и всех его действующих доп. соглашений, которая не является единовременным продуктом (т.е.
	 * предоставляется на периодической основе). <br/>
	 * <br/>
	 * <b>При этом нужно проверять:</b><br/>
	 * В случае если активная подписка есть только на одну позицию договора, то эта позиция исключается из возможных для
	 * исключения позиций (BOX-316), т.к. нельзя оставлять договор без активных позиций, этот договор должен быть
	 * закрыт.
	 *
	 * @see Contract#getFinalEntries()
	 */
	public List<ContractEntry> findEntriesThatCanBeExcluded(ContractExtension contractExtension) {
		Contract contract = contractExtension.getContract();

		List<ContractEntry> entriesPossibleToExclusion = contract.getFinalRecurrentEntries();
		entriesPossibleToExclusion.addAll(contract.getOptionEntries());

		entriesPossibleToExclusion.removeAll(contractExtension.getEntries());
		entriesPossibleToExclusion.removeAll(contractExtension.getExcludedEntries());

		List<ContractEntry> entriesWithActiveSubs = entriesPossibleToExclusion.stream()
				.filter(e -> e instanceof ProductOfferingContractEntry).map(e -> (ProductOfferingContractEntry) e)
				.filter(entryWithActiveSubs()).collect(Collectors.toList());
		if (entriesWithActiveSubs.size() == 1) {
			entriesPossibleToExclusion.removeAll(entriesWithActiveSubs);
		}

		return entriesPossibleToExclusion;
	}

	/**
	 * Ищет подходящие для создания позиции договора прайс-листы Подходящими считаются прайс-листы:
	 * <ul>
	 * <li>находящиеся в состоянии {@linkplain ru.argustelecom.box.env.pricing.model.PricelistState#INFORCE
	 * действует}</li>
	 * <li>текущая дата лежит внутри периода действия прайс-листа</li>
	 * <li>подходящие для клиента, на которого оформлен договор</li>
	 * <li>принадлежащие компании-владельцу (Для агентских договоров - компании-агенту, для двусторонних -
	 * компании-поставщику)</li>
	 * <li>имеющие продуктовые предложения, предоставляющиеся на переодической основе</li>
	 * </ul>
	 * 
	 */
	public List<AbstractPricelist> findPriceLists(AbstractContract<?> abstractContract) {
		AbstractContract<?> inizializedContract = EntityManagerUtils.initializeAndUnproxy(abstractContract);
		checkArgument(inizializedContract instanceof Contract || inizializedContract instanceof ContractExtension);

		Date poi = new Date();
		Customer customer = abstractContract.getCustomer();
		Contract contract = inizializedContract instanceof Contract ? (Contract) inizializedContract
				: ((ContractExtension) inizializedContract).getContract();
		ContractType contractType = contract.getType();
		PartyRole partyRole = contractType.getContractCategory().equals(ContractCategory.AGENCY) ? contract.getBroker()
				: contractType.getProvider();
		Owner owner = (Owner) EntityManagerUtils.initializeAndUnproxy(partyRole);
		return pricelistRp.findActivePricelistsWithRecurrentProductsAndSuitableForCustomer(poi, customer, owner);
	}

	private Predicate<ProductOfferingContractEntry> entryWithActiveSubs() {
		return entry -> subscriptionRp.findSubscription(entry).getState().equals(SubscriptionState.ACTIVE);
	}

}