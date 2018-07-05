package ru.argustelecom.box.env.billing.account.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.party.model.role.CustomerRdo;
import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.api.data.format.ReportDataFormat;

@Getter
@Setter
public class PersonalAccountRdo extends ReportData {

	private String number;

	@ReportDataFormat(value = "#,###.00")
	private BigDecimal availableBalance;

	@ReportDataFormat(value = "#,###.00")
	private BigDecimal balance;

	@ReportDataFormat(value = "#,###.00")
	private BigDecimal threshold;

	private CustomerRdo customer;
	private String state;

	@Builder
	public PersonalAccountRdo(Long id, String number, BigDecimal availableBalance, BigDecimal balance,
			BigDecimal threshold, CustomerRdo customer, String state) {
		super(id);
		this.number = number;
		this.availableBalance = availableBalance;
		this.balance = balance;
		this.threshold = threshold;
		this.customer = customer;
		this.state = state;
	}

}