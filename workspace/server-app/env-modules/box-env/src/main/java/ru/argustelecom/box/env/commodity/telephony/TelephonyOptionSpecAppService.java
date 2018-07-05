package ru.argustelecom.box.env.commodity.telephony;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionSpec;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class TelephonyOptionSpecAppService implements Serializable {

	private static final long serialVersionUID = 7918608380448504992L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TelephonyOptionSpecRepository telephonyOptionSpecRp;

	public TelephonyOptionSpec create(Long telephonyOptionTypeId, Long serviceSpecId, Long tariffId) {
		checkRequiredArgument(telephonyOptionTypeId, "TelephonyOptionTypeId");
		checkRequiredArgument(serviceSpecId, "ServiceSpecId");
		checkRequiredArgument(tariffId, "TariffId");

		TelephonyOptionType type = em.find(TelephonyOptionType.class, telephonyOptionTypeId);
		ServiceSpec serviceSpec = em.find(ServiceSpec.class, serviceSpecId);
		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		return telephonyOptionSpecRp.create(type, serviceSpec, tariff);
	}

	public TelephonyOptionSpec changeTariff(Long telephonyOptionSpecId, Long tariffId) {
		checkRequiredArgument(telephonyOptionSpecId, "TelephonyOptionSpecId");
		checkRequiredArgument(tariffId, "TariffId");

		TelephonyOptionSpec spec = em.find(TelephonyOptionSpec.class, telephonyOptionSpecId);
		spec.setTariff(em.find(AbstractTariff.class, tariffId));
		return spec;
	}

	public void remove(Long telephonyOptionSpecId) {
		checkRequiredArgument(telephonyOptionSpecId, "TelephonyOptionSpecId");

		telephonyOptionSpecRp.remove(em.find(TelephonyOptionSpec.class, telephonyOptionSpecId));
	}

	public List<TelephonyOptionSpec> findByServiceSpec(Long serviceSpecId) {
		checkRequiredArgument(serviceSpecId, "ServiceSpecId");

		return telephonyOptionSpecRp.findByServiceSpec(em.find(ServiceSpec.class, serviceSpecId));
	}
}
