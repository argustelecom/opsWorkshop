package ru.argustelecom.box.env.contract;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.address.LocationRepository;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.model.OptionContractEntry;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class ContractEntryAppService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ContractEntryRepository contractEntryRp;

	@Inject
	private LocationRepository locationRp;

	public ContractEntry createProductOfferingEntry(Long contractId, Long productOfferingId, Long buildingId,
			Long lodgingTypeId, String lodgingNumber, Long personalAccountId) {
		checkNotNull(contractId);
		checkNotNull(productOfferingId);
		checkNotNull(buildingId);

		AbstractContract<?> contract = em.find(AbstractContract.class, contractId);
		ProductOffering productOffering = em.find(ProductOffering.class, productOfferingId);
		Building building = em.find(Building.class, buildingId);
		LocationType lodgingType = findLocationType(lodgingTypeId);
		PersonalAccount personalAccount = findPersonalAccount(personalAccountId);

		Location location = determineLocation(building, lodgingType, lodgingNumber);

		return contractEntryRp.createProductOfferingEntry(contract, productOffering, location, personalAccount);
	}

	public OptionContractEntry createOptionEntry(Long contractId, Long serviceId, Long optionTypeId, Long tariffId) {
		checkNotNull(contractId);
		checkNotNull(serviceId);
		checkNotNull(optionTypeId);
		checkNotNull(tariffId);

		AbstractContract<?> contract = em.find(AbstractContract.class, contractId);
		checkNotNull(contract);

		Service service = em.find(Service.class, serviceId);
		checkNotNull(service);

		TelephonyOptionType optionType = em.find(TelephonyOptionType.class, optionTypeId);
		checkNotNull(optionType);

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		return contractEntryRp.createOptionContractEntry(contract, service, optionType, tariff);
	}

	public void removeEntry(Long id) {
		checkNotNull(id);
		contractEntryRp.removeEntry(checkNotNull(em.find(ContractEntry.class, id)));
	}

	public void removeExcludedEntry(Long extensionId, Long entryId) {
		ContractExtension contractExtension = em.find(ContractExtension.class, extensionId);
		ContractEntry entry = em.find(ContractEntry.class, entryId);
		contractExtension.removeExcludedEntry(entry);
	}

	public List<ContractEntry> findEntriesThatCanBeExcluded(Long id) {
		ContractExtension contractExtension = em.find(ContractExtension.class, id);
		return contractEntryRp.findEntriesThatCanBeExcluded(contractExtension);
	}

	public List<AbstractPricelist> findPricelists(Long abstractContractId) {
		return contractEntryRp.findPriceLists(em.getReference(AbstractContract.class, abstractContractId));
	}

	private PersonalAccount findPersonalAccount(Long personalAccountId) {
		return ofNullable(personalAccountId).map(id -> em.find(PersonalAccount.class, id)).orElse(null);
	}

	private LocationType findLocationType(Long lodgingTypeId) {
		return ofNullable(lodgingTypeId).map(id -> em.find(LocationType.class, id)).orElse(null);
	}

	/**
	 * Определяет конечный адресный объект, если есть данные для поиска/создания квартиры, то вернётся квартира, в
	 * противном случае здание.
	 */
	private Location determineLocation(Building building, LocationType lodgingType, String lodgingNumber) {
		boolean canCreateLodging = lodgingType != null && lodgingNumber != null;
		return canCreateLodging ? locationRp.findOrCreateLodging(building, lodgingType, lodgingNumber) : building;
	}

}