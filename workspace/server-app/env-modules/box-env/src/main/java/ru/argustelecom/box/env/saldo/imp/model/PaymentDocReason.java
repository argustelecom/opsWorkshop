package ru.argustelecom.box.env.saldo.imp.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ru.argustelecom.box.env.billing.transaction.model.TransactionReason;
import ru.argustelecom.box.env.saldo.nls.SaldoImportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.chrono.TZ;

@Entity
@Access(AccessType.FIELD)
public class PaymentDocReason extends TransactionReason {

	private static final long serialVersionUID = -7813396514792049077L;

	private String paymentDocId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDocDate;

	private String paymentDocSource;

	protected PaymentDocReason() {
	}

	public PaymentDocReason(Long id, String paymentDocId, String paymentDocNumber, Date paymentDocDate,
			String paymentDocSource) {
		super(id);
		this.paymentDocId = paymentDocId;
		this.reasonNumber = paymentDocNumber;
		this.paymentDocDate = paymentDocDate;
		this.paymentDocSource = paymentDocSource;
	}

	@Override
	public String getDescription() {
		SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
		return messages.paymentDocReason(
				getReasonNumber(),
				DateUtils.format(getPaymentDocDate(), DateUtils.DATETIME_DEFAULT_PATTERN, TZ.getUserTimeZone())
		);
	}

	@Override
	public String getReasonType() {
		SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
		return messages.paymentDoc();
	}

	public String getPaymentDocId() {
		return paymentDocId;
	}

	public Date getPaymentDocDate() {
		return paymentDocDate;
	}

	public String getPaymentDocSource() {
		return paymentDocSource;
	}

}
