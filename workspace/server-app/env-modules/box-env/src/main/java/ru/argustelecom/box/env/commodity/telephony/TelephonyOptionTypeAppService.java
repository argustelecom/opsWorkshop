package ru.argustelecom.box.env.commodity.telephony;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@ApplicationService
public class TelephonyOptionTypeAppService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TelephonyOptionTypeRepository telephoneOptionTypeRp;

	public TelephonyOptionType create(String name, String keyword, CommodityTypeGroup group, String description) {
		return telephoneOptionTypeRp.create(name, keyword, group, description);
	}

	/**
	 * Актуализирует {@linkplain TelephonyZone зоны телефонной нумерации} для {@linkplain TelephonyOptionType
	 * телефонного типа опции}. Те, что изначально небыли добавлены - добавляются. Те, что были убраны - удаляются.
	 */
	public void markZones(Long telephonyOptionTypeId, List<Long> zonesIds) {
		checkNotNull(telephonyOptionTypeId);
		checkNotNull(zonesIds);

		TelephonyOptionType telephonyOptionType = em.find(TelephonyOptionType.class, telephonyOptionTypeId);
		checkNotNull(telephonyOptionType);

		if (zonesIds.isEmpty()) {
			telephonyOptionType.removeZones();
		} else {
			List<TelephonyZone> actualZones = EntityManagerUtils.findList(em, TelephonyZone.class, zonesIds);
			List<TelephonyZone> allZones = telephonyOptionType.getZones();

			// удаляем все зоны, которых нет в новой коллекции
			List<TelephonyZone> removableZones = allZones.stream().filter(zone -> !actualZones.contains(zone))
					.collect(toList());
			removableZones.forEach(telephonyOptionType::removeZone);

			// добавляем новые зоны
			actualZones.stream().filter(zone -> !allZones.contains(zone)).forEach(telephonyOptionType::addZone);

		}

	}

	public List<TelephonyOptionType> findAll() {
		return telephoneOptionTypeRp.findAll();
	}

}