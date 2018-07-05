package ru.argustelecom.box.env.commodity.telephony;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionSpec;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.inf.service.ApplicationService;

import static com.google.common.base.Preconditions.checkNotNull;

@ApplicationService
public class TelephonyOptionAppService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TelephonyOptionRepository telephonyOptionRp;

	/**
	 * Поиск экземпляров опций телефонии по услуге.
	 *
	 * @param serviceId
	 *            идентификатор услуги, по которой предоставляется опция
	 *
	 * @return коллекция экземпляров найденных опций телефонии
	 */
	public List<TelephonyOption> find(Long serviceId) {
		checkNotNull(serviceId);
		return telephonyOptionRp.find(em.find(Service.class, serviceId));
	}

	/**
	 * Поиск всех экземпляров опций телефонии.
	 *
	 * @return коллекция экземпляров найденных опций телефонии
	 */
	public List<TelephonyOption> findAll() {
		return telephonyOptionRp.findAll();
	}

	public TelephonyOption changeTariff(Long telephonyOption, Long tariffId) {
		checkNotNull(telephonyOption);
		checkNotNull(tariffId);

		TelephonyOption option = em.find(TelephonyOption.class, telephonyOption);
		checkNotNull(option);

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		option.setTariff(tariff);
		return option;
	}

	public TelephonyOption createTelephonyOptionBySpec(Long telephonySpecId, Long serviceId, Long contractEntryId,
												 Long tariffId) {
		checkNotNull(telephonySpecId);
		checkNotNull(serviceId);
		checkNotNull(contractEntryId);
		checkNotNull(tariffId);

		TelephonyOptionSpec spec = em.find(TelephonyOptionSpec.class, telephonySpecId);
		checkNotNull(spec);

		Service service = em.find(Service.class, serviceId);
		checkNotNull(service);

		ContractEntry contractEntry = em.find(ContractEntry.class, contractEntryId);
		checkNotNull(contractEntry);

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		return telephonyOptionRp.createTelephonyOptionBySpec(spec, service, contractEntry, tariff);
	}

	public void remove(Long telephonyOptionId) {
		checkNotNull(telephonyOptionId);

		TelephonyOption option = em.find(TelephonyOption.class, telephonyOptionId);
		checkNotNull(option);

		telephonyOptionRp.remove(option);
	}

}
