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
public class SaldoRegister extends Register {

	public static final String DEFAULT_CHARSET = "cp1251";

	@Element(orderNumber = 1)
	private String number;

	@Element(orderNumber = 2)
	private BigDecimal sum;

	@Element(orderNumber = 3)
	private BigDecimal interestFine;

	@Element(orderNumber = 4)
	private BigDecimal holdSum;

	@Element(orderNumber = 5)
	private BigDecimal transferSum;

	@Element(orderNumber = 6)
	private Long rowCount;

	@Element(orderNumber = 7)
	private String agentCode;

	@Element(orderNumber = 8)
	private String serviceNumber;

	@Element(orderNumber = 9, dateFormat = "dd/MM/yyyy HH:mm:ss")
	private Date creationDate;

	@Element(orderNumber = 10, dateFormat = "dd/MM/yyyy HH:mm:ss")
	private Date startDate;

	@Element(orderNumber = 11, dateFormat = "dd/MM/yyyy HH:mm:ss")
	private Date endDate;

	@Element(orderNumber = 12)
	private String note;

	private String charset;

	@Override
	public String getCharset() {
		return StringUtils.isEmpty(charset) ? DEFAULT_CHARSET : charset;
	}

	@Override
	public String checkParams() {
		String superCheckMsg = super.checkParams();
		StringBuilder aggregateErrorsMsg = new StringBuilder(superCheckMsg);
		SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);

		aggregateErrorsMsg.append(checkSum());
		aggregateErrorsMsg.append(checkCreationDate());
		aggregateErrorsMsg.append(checkStartDate());
		aggregateErrorsMsg.append(checkEndDate());

		return messages.headerHasInvalidData().length() == aggregateErrorsMsg.length() ? StringUtils.EMPTY
				: aggregateErrorsMsg.toString();
	}

	private String checkSum() {
		if (sum == null) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			return messages.totalRegisterSumValue();
		}
		return StringUtils.EMPTY;
	}

	private String checkCreationDate() {
		if (creationDate == null) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			return messages.registerDate();
		}
		return StringUtils.EMPTY;
	}

	private String checkStartDate() {
		if (startDate == null) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			return messages.startDateValue();
		}
		return StringUtils.EMPTY;
	}

	private String checkEndDate() {
		if (endDate == null) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			return messages.endDateValue();
		}
		return StringUtils.EMPTY;
	}

}