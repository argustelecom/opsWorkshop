package ru.argustelecom.box.env.billing.bill.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.AddressRdo;
import ru.argustelecom.box.env.billing.account.model.PersonalAccountRdo;
import ru.argustelecom.box.env.contract.model.ContractRdo;
import ru.argustelecom.box.env.party.model.PartyRoleRdo;
import ru.argustelecom.box.env.party.model.role.CustomerRdo;
import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.env.report.api.data.format.ReportBandDef;
import ru.argustelecom.box.env.report.api.data.format.ReportDataFormat;

@Getter
@Setter
public class BillRdo extends ReportData {

	public static final String MONEY_FORMAT_PATTERN = "#,##0.00";

	private String number;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone = "Europe/Moscow")
	private Date creationDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone = "Europe/Moscow")
	private Date billDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone = "Europe/Moscow")
	private Date periodStartDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone = "Europe/Moscow")
	private Date periodEndDate;

	private String period;

	private CustomerRdo customer;

	private PersonalAccountRdo personalAccount;

	private ContractRdo contract;

	private AddressRdo address;

	@ReportBandDef(name = "Addresses")
	private ReportDataList<AddressRdo> addresses;

	private String addressesAsString;

	private PartyRoleRdo provider;

	private PartyRoleRdo broker;

	@ReportDataFormat(value = MONEY_FORMAT_PATTERN)
	private BigDecimal currentBillingPeriodChargesWithTax;

	@ReportDataFormat(value = MONEY_FORMAT_PATTERN)
	private BigDecimal currentBillingPeriodTaxAmount;

	@ReportDataFormat(value = MONEY_FORMAT_PATTERN)
	private BigDecimal currentBillingPeriodChargesWithoutTax;

	@ReportDataFormat(value = MONEY_FORMAT_PATTERN)
	private BigDecimal discountAmount;

	@ReportDataFormat(value = MONEY_FORMAT_PATTERN)
	private BigDecimal totalAmountToPay;

	private Map<String, String> analytics;

	@Builder
	public BillRdo(Long id, String number, Date creationDate, Date billDate, Date periodStartDate, Date periodEndDate,
			String period, CustomerRdo customer, PersonalAccountRdo personalAccount, ContractRdo contract,
			AddressRdo address, ReportDataList<AddressRdo> addresses, String addressesAsString, PartyRoleRdo provider, PartyRoleRdo broker,
			BigDecimal currentBillingPeriodChargesWithTax, BigDecimal currentBillingPeriodTaxAmount,
			BigDecimal currentBillingPeriodChargesWithoutTax, BigDecimal discountAmount, BigDecimal totalAmountToPay,
			Map<String, String> analytics) {
		super(id);
		this.number = number;
		this.creationDate = translate(creationDate);
		this.billDate = translate(billDate);
		this.periodStartDate = translate(periodStartDate);
		this.periodEndDate = translate(periodEndDate);
		this.period = period;
		this.customer = customer;
		this.personalAccount = personalAccount;
		this.contract = contract;
		this.address = address;
		this.addresses = addresses;
		this.addressesAsString = addressesAsString;
		this.provider = provider;
		this.broker = broker;
		this.currentBillingPeriodChargesWithTax = currentBillingPeriodChargesWithTax;
		this.currentBillingPeriodTaxAmount = currentBillingPeriodTaxAmount;
		this.currentBillingPeriodChargesWithoutTax = currentBillingPeriodChargesWithoutTax;
		this.totalAmountToPay = totalAmountToPay;
		this.analytics = analytics;
		this.discountAmount = discountAmount;
	}

}