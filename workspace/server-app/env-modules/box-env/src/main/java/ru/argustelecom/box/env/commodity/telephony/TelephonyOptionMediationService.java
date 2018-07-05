package ru.argustelecom.box.env.commodity.telephony;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.commodity.telephony.TelephonyOptionMediationService.INSERT_INTO_PNTH;
import static ru.argustelecom.box.env.commodity.telephony.TelephonyOptionMediationService.INSERT_INTO_TTH;
import static ru.argustelecom.box.env.commodity.telephony.TelephonyOptionMediationService.UPDATE_PNTH;
import static ru.argustelecom.box.env.commodity.telephony.TelephonyOptionMediationService.UPDATE_TTH;
import static ru.argustelecom.box.env.type.model.TypePropertyRef.TEXT_ARRAY;
import static ru.argustelecom.box.integration.nri.service.model.ResourceType.PHONE_NUMBER;

import java.io.Serializable;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyRef;
import ru.argustelecom.box.env.type.model.properties.TextArrayProperty;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.box.integration.nri.service.ServiceInfoService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQueries;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

// @formatter:off
@NamedNativeQueries({
		@NamedNativeQuery(
				name = INSERT_INTO_PNTH,
				query = "INSERT INTO mediation.phone_number_tariff_history " +
						"(phone_number, service_id, option_id, tariff_id, valid_from) " +
						"VALUES (:phoneNumber, :serviceId, :optionId, :tariffId, :validFrom)"
		),
		@NamedNativeQuery(
				name = INSERT_INTO_TTH,
				query = "INSERT INTO mediation.trunk_tariff_history " +
						"(trunk_number, service_id, option_id, tariff_id, valid_from) " +
						"VALUES (:trunkNumber, :serviceId, :optionId, :tariffId, :validFrom)"
		),
		@NamedNativeQuery(
				name = UPDATE_PNTH,
				query = "UPDATE mediation.phone_number_tariff_history SET valid_to = :validTo " +
						"WHERE option_id = :optionId"
		),
		@NamedNativeQuery(
				name = UPDATE_TTH,
				query = "UPDATE mediation.trunk_tariff_history SET valid_to = :validTo " +
						"WHERE option_id = :optionId"
		),
})
// @formatter:on
@DomainService
public class TelephonyOptionMediationService implements Serializable {

	private static final Logger log = Logger.getLogger(TelephonyOptionMediationService.class);

	protected static final String INSERT_INTO_PNTH = "TelephonyOptionMediationService.createInsertIntoPnthQuery";
	protected static final String INSERT_INTO_TTH = "TelephonyOptionMediationService.createInsertIntoTthQuery";
	protected static final String UPDATE_PNTH = "TelephonyOptionMediationService.createUpdatePnthQuery";
	protected static final String UPDATE_TTH = "TelephonyOptionMediationService.createUpdateTthQuery";

	private static final String SERVICE_TRUNK_PROPERTY_KEYWORD = "trunk";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ServiceInfoService serviceInfoSvc;

	/**
	 * Добавляет записи в mediation.phone_number_tariff_history и mediation.trunk_tariff_history
	 * 
	 * @param option
	 *            опция на телефонию
	 * @param validFrom
	 *            дата создания
	 */
	public void createEntries(TelephonyOption option, Date validFrom) {
		checkNotNull(option);
		checkNotNull(validFrom);

		Long serviceId = option.getService().getId();
		Long optionId = option.getId();
		Long tariffId = option.getTariff().getId();

		//@formatter:off
		Function<Supplier<Query>, BiConsumer<String, String>> partialFun = querySupplier -> (numberParam, number) ->
				insert(querySupplier, numberParam, number, serviceId, optionId, tariffId, validFrom);
		//@formatter:on

		//@formatter:off
		serviceInfoSvc.allLoadedResourcesByService(option.getService()).stream()
				.filter(resource -> PHONE_NUMBER.equals(resource.getResourceType()))
				.forEach(resource -> partialFun.apply(this::createInsertIntoPnthQuery)
												.accept("phoneNumber", resource.getName()));
		//@formatter:on

		TypeProperty<?> trunkProperty = findTrunkProperty(option);
		if (trunkProperty != null) {
			TypePropertyRef trunkPropertyType = trunkProperty.getType();
			if (TEXT_ARRAY.equals(trunkPropertyType)) {
				Consumer<String> forEachFun = number -> partialFun.apply(this::createInsertIntoTthQuery)
						.accept("trunkNumber", number);
				((TextArrayProperty) trunkProperty).getValue(option.getService()).forEach(forEachFun);
			} else {
				log.infov("У услуги c id = {0} определенно свойство с ключевым словом {1}, но c типом {2}, вместо {3}",
						serviceId, SERVICE_TRUNK_PROPERTY_KEYWORD, trunkPropertyType.getName(), TEXT_ARRAY.getName());
			}
		}
	}

	/**
	 * Обновляет записи в mediation.phone_number_tariff_history и mediation.trunk_tariff_history, проставляя valid_to
	 * для записей, у которой option_id = option.getId()
	 * 
	 * @param option
	 *            опция на телефонию
	 * @param validTo
	 *            дата
	 */
	public void updateEntries(TelephonyOption option, Date validTo) {
		checkNotNull(option);
		checkNotNull(validTo);

		Consumer<Supplier<Query>> partialFun = query -> update(query, option.getId(), validTo);

		partialFun.accept(this::createUpdatePnthQuery);
		partialFun.accept(this::createUpdateTthQuery);
	}

	private void insert(Supplier<Query> querySupplier, String numberParameter, String number, Long serviceId,
			Long optionId, Long tariffId, Date validFrom) {
		//@formatter:off
		querySupplier.get()
				.setParameter(numberParameter, number)
				.setParameter("serviceId", serviceId)
				.setParameter("optionId", optionId)
				.setParameter("tariffId", tariffId)
				.setParameter("validFrom", validFrom)
				.executeUpdate();
		//@formatter:on
	}

	private void update(Supplier<Query> querySupplier, Long optionId, Date validTo) {
		// @formatter:off
		querySupplier.get()
				.setParameter("optionId", optionId)
				.setParameter("validTo", validTo)
				.executeUpdate();
		// @formatter:on
	}

	private TypeProperty findTrunkProperty(TelephonyOption option) {
		//@formatter:off
		return option.getService().getType().getProperties().stream()
				.filter(property -> SERVICE_TRUNK_PROPERTY_KEYWORD.equals(property.getKeyword()))
				.findFirst()
				.orElse(null);
		//@formatter:on
	}

	private Query createInsertIntoPnthQuery() {
		return em.createNamedQuery(INSERT_INTO_PNTH);
	}

	private Query createInsertIntoTthQuery() {
		return em.createNamedQuery(INSERT_INTO_TTH);
	}

	private Query createUpdatePnthQuery() {
		return em.createNamedQuery(UPDATE_PNTH);
	}

	private Query createUpdateTthQuery() {
		return em.createNamedQuery(UPDATE_TTH);
	}

	private static final long serialVersionUID = -4251423864182960237L;
}
