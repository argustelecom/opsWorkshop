package ru.argustelecom.box.env.billing.bill.model;

import static ru.argustelecom.box.env.billing.bill.model.BillRdo.MONEY_FORMAT_PATTERN;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.api.data.format.ReportDataFormat;

@Getter
@Setter
public class BillEntryRdo extends ReportData {

	private String subjectName;

	private String subjectDescription;

	@ReportDataFormat(value = MONEY_FORMAT_PATTERN)
	private BigDecimal amountWithTax;

	@ReportDataFormat(value = MONEY_FORMAT_PATTERN)
	private BigDecimal taxAmount;

	@ReportDataFormat(value = MONEY_FORMAT_PATTERN)
	private BigDecimal amountWithoutTax;

	@Builder
	public BillEntryRdo(Long id, String subjectName, String subjectDescription, BigDecimal amountWithTax,
			BigDecimal taxAmount, BigDecimal amountWithoutTax) {
		super(id);
		this.subjectName = subjectName;
		this.subjectDescription = subjectDescription;
		this.amountWithTax = amountWithTax;
		this.taxAmount = taxAmount;
		this.amountWithoutTax = amountWithoutTax;
	}

}
