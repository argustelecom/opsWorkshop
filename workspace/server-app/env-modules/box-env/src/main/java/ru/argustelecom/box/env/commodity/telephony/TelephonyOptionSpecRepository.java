package ru.argustelecom.box.env.commodity.telephony;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionSpec;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionSpec.TelephonyOptionSpecQuery;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.service.Repository;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

@Repository
public class TelephonyOptionSpecRepository implements Serializable {

	private static final long serialVersionUID = -8590185842096523844L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TypeFactory typeFactory;

	public TelephonyOptionSpec create(TelephonyOptionType type, ServiceSpec serviceSpec, AbstractTariff tariff) {
		checkRequiredArgument(type, "TelephonyOptionType");
		checkRequiredArgument(serviceSpec, "ServiceSpec");
		checkRequiredArgument(tariff, "Tariff");

		TelephonyOptionSpec instance = typeFactory.createInstance(type, TelephonyOptionSpec.class);
		instance.setServiceSpec(serviceSpec);
		instance.setTariff(tariff);
		em.persist(instance);
		return instance;
	}

	public void remove(TelephonyOptionSpec spec) {
		checkRequiredArgument(spec, "TelephonyOptionSpec");

		em.remove(spec);
	}

	public List<TelephonyOptionSpec> findByServiceSpec(ServiceSpec serviceSpec) {
		checkRequiredArgument(serviceSpec, "ServiceSpec");

		TelephonyOptionSpecQuery query = new TelephonyOptionSpecQuery();
		return query.and(query.serviceSpec().equal(serviceSpec)).getResultList(em);
	}

}
