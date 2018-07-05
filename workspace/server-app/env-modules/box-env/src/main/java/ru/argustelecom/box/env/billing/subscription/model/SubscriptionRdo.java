package ru.argustelecom.box.env.billing.subscription.model;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.contract.model.ContractEntryRdo;
import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.api.data.format.ReportDataFormat;

@Getter
@Setter
public class SubscriptionRdo extends ReportData {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone = "Europe/Moscow")
	private Date validFrom;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone = "Europe/Moscow")
	private Date validTo;

	private String state;
	private ContractEntryRdo contractEntry;

	@ReportDataFormat(value = "#,###.00")
	private BigDecimal currentMonthCharge = BigDecimal.ZERO;

	@ReportDataFormat(value = "#,###.00")
	private BigDecimal nextMonthCharge = BigDecimal.ZERO;

	@Builder
	public SubscriptionRdo(Long id, Date validFrom, Date validTo, String state, ContractEntryRdo contractEntry) {
		super(id);
		this.validFrom = translate(validFrom);
		this.validTo = translate(validTo);
		this.state = state;
		this.contractEntry = contractEntry;
	}

}