package ru.argustelecom.box.env.billing.bill;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.model.BillRawData;
import ru.argustelecom.box.env.billing.bill.model.RawDataContainer;
import ru.argustelecom.box.env.billing.bill.model.RawDataHolder;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class BillRawDataRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService iss;

	public BillRawData create(RawDataHolder rawDataHolder) {
		BillRawData billRawData = new BillRawData(iss.nextValue(BillRawData.class), RawDataContainer.of(rawDataHolder));

		em.persist(billRawData);

		return billRawData;
	}

	private static final long serialVersionUID = -3769787968787132215L;
}
