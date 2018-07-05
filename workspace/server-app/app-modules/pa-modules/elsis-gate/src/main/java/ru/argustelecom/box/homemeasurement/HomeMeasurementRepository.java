package ru.argustelecom.box.homemeasurement;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.env.address.model.Street;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.homemeasurement.model.HomeMeasurement;
import ru.argustelecom.box.homemeasurement.model.HomeMeasurementHistory;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@Repository
public class HomeMeasurementRepository implements Serializable {

	private static final long serialVersionUID = -5535665936247077304L;

	private static final String FIND_HOME_MEASUREMENT = "HomeMeasurementRepository.findHomeMeasurement";
	private static final String HOME_MEASUREMENT_HISTORY_LIST = "HomeMeasurementRepository.getHistoryList";

	@PersistenceContext
	private EntityManager em;

	/**
	 * Возвращает карту, где ключём является адресный объект, а значением список последних показаний счётчиков.
	 * Рассматриваются только те адреса, на которых есть активные подписки.
	 * 
	 * @param customer
	 *            клиент, для которого надо получить показания счётчиков.
	 */
	public Map<Location, List<HomeMeasurement>> getHomeMeasurementMap(Customer customer) {
		Map<Location, String> aggregateIdMap = getAggregateIdMap(customer);

		Map<Location, List<HomeMeasurement>> homeMeasurementMap = new HashMap<>();
		aggregateIdMap.keySet().forEach(
				location -> homeMeasurementMap.put(location, findHomeMeasurement(aggregateIdMap.get(location))));

		return homeMeasurementMap;
	}

	@NamedQuery(name = HOME_MEASUREMENT_HISTORY_LIST, query = "from HomeMeasurementHistory h where h.id.registryId = :registryId")
	public List<HomeMeasurementHistory> getHistoryList(HomeMeasurement homeMeasurement) {
		return em.createNamedQuery(HOME_MEASUREMENT_HISTORY_LIST, HomeMeasurementHistory.class)
				.setParameter("registryId", homeMeasurement.getRegistryId()).getResultList();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	@NamedQuery(name = FIND_HOME_MEASUREMENT, query = "from HomeMeasurement hm where hm.aggregateId = :aggregateId")
	private List<HomeMeasurement> findHomeMeasurement(String aggregateId) {
		return em.createNamedQuery(FIND_HOME_MEASUREMENT, HomeMeasurement.class)
				.setParameter("aggregateId", aggregateId).getResultList();
	}

	/**
	 * Формирует карту, где ключём является адресный объект, а значением агрегированный ID, по которому происходит
	 * получение показаницй счётчиков из Light House.
	 * 
	 * @param customer
	 *            клиент, для которого надо получить показания счётчиков.
	 */
	private Map<Location, String> getAggregateIdMap(Customer customer) {
		Set<Location> locations = new HashSet<>();
		customer.getPersonalAccounts().forEach(personalAccount -> personalAccount.getActiveSubscriptions()
				.forEach(subscription -> locations.addAll(subscription.getLocations())));

		Map<Location, String> aggregateIdMap = new HashMap<>();
		locations.forEach(location -> aggregateIdMap.put(location, generateAggregateId(location)));
		return aggregateIdMap;
	}

	/**
	 * Генерирует агрегированный ID для конкретного адресного объекта. Правила генерации следующие:
	 * <li>
	 * <ul>
	 * Рассматриваются только последовательности вида: {@linkplain #generateAggregateIdWithLodging(Location)
	 * квартира/помещение - здание - улица} и {@linkplain #generateAggregateIdWithoutLodging(Location) здание - улица}
	 * </ul>
	 * <ul>
	 * Если последовательность не подходит, то такой адрес отбрасывается.
	 * </ul>
	 * </li>
	 * 
	 * @param location
	 *            адресный объект, для которого необходимо получить агрегированный ID.
	 */
	private String generateAggregateId(Location location) {
		if (location instanceof Lodging)
			return generateAggregateIdWithLodging(location);

		if (location instanceof Building)
			return generateAggregateIdWithoutLodging(location);

		return EMPTY;
	}

	private static final String FULL_AGGREGATE_ID_PATTERN = "%s %s/%s-%s";

	/**
	 * <b>!!! ТАКОЙ ФОРМАТ ОЖИДАЕТМЯ НА ДРУГОЙ СТОРОНЕ !!!</b>
	 * <p/>
	 * Генерит агрегированный ID вида: '<улица> <№ дома>/<Корпус>-<Квартира/Помещение>'. При этом если корпуса нет, то
	 * '/' между номером дома и корпусом не убирается.
	 * 
	 * @param location
	 *            адресный объект, для которого необходимо получить агрегированный ID.
	 */
	private String generateAggregateIdWithLodging(Location location) {
		StringBuilder aggregateIdBuilder = new StringBuilder();
		Location building = initializeAndUnproxy(location.getParent());
		if (building instanceof Building) {
			Location street = initializeAndUnproxy(building.getParent());
			if (street instanceof Street) {
				String streetNameSubValue = street.getName().length() > 4 ? street.getName().substring(0, 4)
						: street.getName();
				String buildingNumber = ((Building) building).getNumber();
				String buildingCorpus = defaultIfBlank(((Building) building).getCorpus(), EMPTY);
				String lodgingNumber = ((Lodging) location).getNumber();
				aggregateIdBuilder.append(format(FULL_AGGREGATE_ID_PATTERN, streetNameSubValue, buildingNumber,
						buildingCorpus, lodgingNumber));
			}
		}
		return aggregateIdBuilder.toString();
	}

	private static final String SHORT_AGGREGATE_ID_PATTERN = "%s %s/%s";

	/**
	 * <b>!!! ТАКОЙ ФОРМАТ ОЖИДАЕТМЯ НА ДРУГОЙ СТОРОНЕ !!!</b>
	 * <p/>
	 * Генерит агрегированный ID вида: '<улица> <№ дома>/<Корпус>'. При этом если корпуса нет, то '/' между номером дома
	 * и корпусом не убирается.
	 *
	 * @param building
	 *            адресный объект, для которого необходимо получить агрегированный ID.
	 */
	private String generateAggregateIdWithoutLodging(Location building) {
		StringBuilder aggregateIdBuilder = new StringBuilder();
		Location street = initializeAndUnproxy(building.getParent());
		if (street instanceof Street) {
			String streetNameSubValue = street.getName().length() > 4 ? street.getName().substring(0, 4)
					: street.getName();
			String buildingNumber = ((Building) building).getNumber();
			String buildingCorpus = defaultIfBlank(((Building) building).getCorpus(), EMPTY);
			aggregateIdBuilder
					.append(format(SHORT_AGGREGATE_ID_PATTERN, streetNameSubValue, buildingNumber, buildingCorpus));
		}
		return aggregateIdBuilder.toString();
	}

}