package ru.argustelecom.box.env.saldo.imp.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.saldo.nls.SaldoImportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@Getter
@Setter
public class ED108Register extends Register {

	@Element(orderNumber = 1)
	private Long rowCount;

	@Element(orderNumber = 2)
	private BigDecimal takenSum;

	@Element(orderNumber = 3)
	private BigDecimal sum;

	@Element(orderNumber = 4)
	private BigDecimal commission;

	@Element(orderNumber = 5)
	private String number;

	@Element(orderNumber = 6, dateFormat = "dd-MM-yyyy")
	private Date paymentDocDate;

	private String charset;

	@Override
	public String checkParams() {
		String superCheckMsg = super.checkParams();
		StringBuilder aggregateErrorsMsg = new StringBuilder(superCheckMsg);

		aggregateErrorsMsg.append(checkSum());
		aggregateErrorsMsg.append(checkPaymentDocDate());

		SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);

		return messages.headerHasInvalidData().length() == aggregateErrorsMsg.length() ? StringUtils.EMPTY
				: aggregateErrorsMsg.toString();
	}

	private String checkSum() {
		if (sum == null) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			return messages.ed108totalAmount();
		}
		return StringUtils.EMPTY;
	}

	private String checkPaymentDocDate() {
		if (sum == null) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			return messages.ed108date();
		}
		return StringUtils.EMPTY;
	}

	@Override
	public Date getCreationDate() {
		return paymentDocDate;
	}

	@Override
	public Date getStartDate() {
		return paymentDocDate;
	}

	@Override
	public Date getEndDate() {
		return paymentDocDate;
	}
}