package ru.argustelecom.box.env.billing.invoice;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceEntry;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceEntryContainer;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceEntryData;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class UsageInvoiceEntryRepository implements Serializable {
	private static final long serialVersionUID = 8017580206947416155L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService iss;

	public UsageInvoiceEntry create(UsageInvoiceEntryContainer usageInvoiceEntryContainer) {
		UsageInvoiceEntry usageInvoiceEntry = new UsageInvoiceEntry(iss.nextValue(UsageInvoiceEntry.class),
				usageInvoiceEntryContainer);

		em.persist(usageInvoiceEntry);

		return usageInvoiceEntry;
	}
}
