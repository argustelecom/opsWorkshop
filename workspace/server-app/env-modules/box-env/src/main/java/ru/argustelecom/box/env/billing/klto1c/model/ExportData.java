package ru.argustelecom.box.env.billing.klto1c.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Контейнер для хранения информации из выгрузки "KL to 1C".
 */
public class ExportData implements KLto1CDataObject {

	@Element(name = "ВерсияФормата", requiredInFormat = true)
	private String version;

	@Element(name = "Кодировка", requiredInFormat = true)
	private String encoding;

	@Element(name = "Отправитель")
	private String sender;

	@Element(name = "Получатель", requiredInFormat = true)
	private String recipient;

	@Element(name = "ДатаСоздания", dateFormat = "dd.MM.yyyy")
	private Date creationDate;

	@Element(name = "ВремяСоздания")
	private String creationTime;

	@Element(name = "ДатаНачала", requiredInFormat = true, dateFormat = "dd.MM.yyyy")
	private Date startDate;

	@Element(name = "ДатаКонца", requiredInFormat = true, dateFormat = "dd.MM.yyyy")
	private Date endDate;

	private List<CheckingAccount> checkingAccounts = new ArrayList<>();

	private List<PaymentOrder> paymentOrders = new ArrayList<>();

	private List<String> warnings = new ArrayList<>();

	private List<String> errors = new ArrayList<>();

	public void addCheckingAccount(CheckingAccount checkingAccount) {
		checkingAccounts.add(checkingAccount);
	}

	public void addPaymentOrder(PaymentOrder paymentOrder) {
		paymentOrders.add(paymentOrder);
	}

	@Override
	public void addWarning(String warning) {
		warnings.add(warning);
	}

	@Override
	public void addError(String error) {
		errors.add(error);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<CheckingAccount> getCheckingAccounts() {
		return Collections.unmodifiableList(checkingAccounts);
	}

	public void setCheckingAccounts(List<CheckingAccount> checkingAccounts) {
		this.checkingAccounts = checkingAccounts;
	}

	public List<PaymentOrder> getPaymentOrders() {
		return Collections.unmodifiableList(paymentOrders);
	}

	public void setPaymentOrders(List<PaymentOrder> paymentOrders) {
		this.paymentOrders = paymentOrders;
	}

	@Override
	public List<String> getWarnings() {
		return Collections.unmodifiableList(warnings);
	}

	@Override
	public List<String> getErrors() {
		return Collections.unmodifiableList(errors);
	}

}