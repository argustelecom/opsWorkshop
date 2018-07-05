package ru.argustelecom.box.env.telephony.tariff;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.login.LoginService;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntry;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntryHistory;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class TariffEntryHistoryRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService iss;

	@Inject
	private LoginService loginSvc;

	public TariffEntryHistory create(TariffEntry entry) {
		TariffEntryHistory tariffEntryHistory = new TariffEntryHistory(iss.nextValue(TariffEntryHistory.class), entry,
				loginSvc.getCurrentEmployee());
		em.persist(tariffEntryHistory);
		return tariffEntryHistory;
	}

	private static final long serialVersionUID = -6637413639491166229L;
}
