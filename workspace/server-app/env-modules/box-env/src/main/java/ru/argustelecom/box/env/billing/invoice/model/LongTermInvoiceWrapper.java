package ru.argustelecom.box.env.billing.invoice.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.account.model.ReserveWrapper;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.billing.model.ILongTermInvoice;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Named(value = ILongTermInvoice.WRAPPER_NAME)
public class LongTermInvoiceWrapper implements EntityWrapper {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ReserveWrapper rw;

	@Override
	public ILongTermInvoice wrap(Identifiable entity) {
		checkNotNull(entity);
		LongTermInvoice longTermInvoice = (LongTermInvoice) entity;

		//@formatter:off
		return ILongTermInvoice.builder()
					.id(longTermInvoice.getId())
					.objectName(longTermInvoice.getObjectName())
					.personalAccountId(longTermInvoice.getPersonalAccount().getId())
					.transactionId(longTermInvoice.getTransaction() != null ? longTermInvoice.getTransaction().getId() : null)
					.state(new IState(longTermInvoice.getState().toString(), longTermInvoice.getState().getName()))
					.startDate(longTermInvoice.getStartDate())
					.endDate(longTermInvoice.getEndDate())
					.closingDate(longTermInvoice.getClosingDate())
					.totalPrice(longTermInvoice.getTotalPrice().getAmount())
					.price(longTermInvoice.getPrice().getAmount())
					.discountValue(longTermInvoice.getDiscountValue().getAmount())
					.reserve(longTermInvoice.getReserve() != null ? rw.wrap(longTermInvoice.getReserve()) : null)
				.build();
		//@formatter:on
	}

	@Override
	public LongTermInvoice unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(LongTermInvoice.class, iEntity.getId());
	}

	private static final long serialVersionUID = 8705320032279744225L;

}