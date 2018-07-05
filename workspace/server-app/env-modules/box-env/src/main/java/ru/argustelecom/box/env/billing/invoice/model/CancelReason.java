package ru.argustelecom.box.env.billing.invoice.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.persistence.AccessType.FIELD;
import static javax.persistence.TemporalType.DATE;
import static lombok.AccessLevel.PROTECTED;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.billing.reason.nls.ReasonMessagesBundle;
import ru.argustelecom.box.env.billing.transaction.model.TransactionReason;

/**
 * <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=6718554">Сторно-документ</a> - документ,
 * который отменяет основной документ.
 */
@Getter
@Entity
@Table(schema = "system")
@Access(FIELD)
@NoArgsConstructor(access = PROTECTED)
public class CancelReason extends TransactionReason {

	/**
	 * Фактическая дата создания документа
	 */
	@Temporal(DATE)
	private Date creationDate;

	/**
	 * Ссылка на сторнируемый инвойс
	 */
	@OneToOne(optional = false)
	@JoinColumn(name = "cancel_invoice_id")
	private AbstractInvoice cancelInvoice;

	protected CancelReason(Long id) {
		super(id);
	}

	public CancelReason(Long id, Date creationDate, AbstractInvoice cancelInvoice, String reasonNumber) {
		super(id);
		this.creationDate = checkNotNull(creationDate);
		this.cancelInvoice = checkNotNull(cancelInvoice);
		this.reasonNumber = checkNotNull(reasonNumber);
	}

	@Override
	public String getDescription() {
		return cancelInvoice.getObjectName();
	}

	@Override
	public String getReasonType() {
		return getMessages(ReasonMessagesBundle.class).invoice();
	}

	private static final long serialVersionUID = -7847023449239136251L;
}
