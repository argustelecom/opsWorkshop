package ru.argustelecom.box.env.telephony.tariff;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntry;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.inf.service.Repository;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static ru.argustelecom.box.env.telephony.tariff.model.TariffEntry.TariffEntryStatus.ACTIVE;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Repository
public class TariffEntryRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService iss;

	TariffEntry create(AbstractTariff tariff, TelephonyZone zone, String name, List<Integer> prefix,
							  Money chargePerUnit) {
		checkNotNull(tariff);
		checkNotNull(zone);
		checkState(isNotBlank(name));
		checkState(checkNotNull(prefix).stream().noneMatch(Objects::isNull));
		checkNotNull(chargePerUnit);

		TariffEntry tariffEntry = new TariffEntry(iss.nextValue(TariffEntry.class), tariff);
		tariffEntry.update(zone, name, prefix, chargePerUnit);

		em.persist(tariffEntry);

		return tariffEntry;
	}

	private static final long serialVersionUID = 531798537344217092L;
}
