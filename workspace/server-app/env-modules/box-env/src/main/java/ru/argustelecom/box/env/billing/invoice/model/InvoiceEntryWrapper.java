package ru.argustelecom.box.env.billing.invoice.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.billing.model.IInvoiceEntry;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Named(value = IInvoiceEntry.WRAPPER_NAME)
public class InvoiceEntryWrapper implements EntityWrapper {

	@PersistenceContext
	private EntityManager em;

	@Override
	public IInvoiceEntry wrap(Identifiable entity) {
		checkNotNull(entity);
		InvoiceEntry invoiceEntry = (InvoiceEntry) entity;
		//@formatter:off
		return IInvoiceEntry.builder()
					.id(invoiceEntry.getId())
					.objectName(invoiceEntry.getObjectName())
					.pricelistProductEntryId(invoiceEntry.getProductOffering().getId())
					.amount(invoiceEntry.getAmount() != null ? invoiceEntry.getAmount().getAmount() : null)
				.build();
		//@formatter:on
	}

	@Override
	public InvoiceEntry unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(InvoiceEntry.class, iEntity.getId());
	}

	private static final long serialVersionUID = -8399660898504526476L;

}