package ru.argustelecom.box.env.billing.invoice;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.invoice.model.CancelReason;
import ru.argustelecom.box.env.billing.reason.nls.ReasonMessagesBundle;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class CancelReasonRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService iss;

	/**
	 * Создает экземпляр с reasonNumber = "Задание на пересчет #{jobId}"
	 */
	// TODO изменить параметер jobId на экземпляр job
	public CancelReason create(Date creationDate, AbstractInvoice cancelInvoice, Long jobId) {
		String reasonNumber = getMessages(ReasonMessagesBundle.class).jobCancelReason(checkNotNull(jobId));
		return create(creationDate, cancelInvoice, reasonNumber);
	}

	/**
	 * Создает экземпляр с произвольной reasonNumber
	 */
	public CancelReason create(Date creationDate, AbstractInvoice cancelInvoice, String reasonNumber) {
		checkNotNull(creationDate);
		checkNotNull(cancelInvoice);
		checkNotNull(reasonNumber);

		CancelReason reason = new CancelReason(iss.nextValue(CancelReason.class), creationDate, cancelInvoice,
				reasonNumber);

		em.persist(reason);

		return reason;
	}

	private static final long serialVersionUID = -5399181518653487767L;
}
