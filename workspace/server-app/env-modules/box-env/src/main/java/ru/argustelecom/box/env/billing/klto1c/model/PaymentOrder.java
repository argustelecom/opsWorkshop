package ru.argustelecom.box.env.billing.klto1c.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Контейнер для хранения информации из секции "Платёжном поручении".
 */
public class PaymentOrder implements KLto1CDataObject {

	// Шапка платежного документа

	@Element(name = "Номер", requiredInFormat = true)
	private String number;

	@Element(name = "Дата", requiredInFormat = true, dateFormat = "dd.MM.yyyy")
	private Date date;

	@Element(name = "Сумма", requiredInFormat = true)
	private String sum;

	// Квитанция по платежному документу

	@Element(name = "КвитанцияДата", dateFormat = "dd.MM.yyyy")
	private Date billDate;

	@Element(name = "КвитанцияВремя")
	private String billTime;

	@Element(name = "КвитанцияСодержание")
	private String billDescription;

	// Реквизиты плательщика

	@Element(name = "ПлательщикСчет", requiredInFormat = true)
	private String payerAccount;

	@Element(name = "ДатаСписано", requiredInFormat = true)
	private String writeOffDate;

	@Element(name = "ПлательщикИНН", requiredInFormat = true)
	private String payerINN;

	@Element(name = "ПлательщикКПП")
	private String payerKPP;

	@Element(name = "ПлательщикБИК")
	private String payerBIK;

	@Element(name = "ПлательщикРасчСчет")
	private String payerCheckingAccount;

	@Element(name = "ПлательщикКорсчет")
	private String payerCorAccount;

	@Element(name = "Плательщик[0-9]*", simpleField = false)
	private Set<String> payers = new HashSet<>();

	@Element(name = "ПлательщикБанк[0-9]*", simpleField = false)
	private Set<String> payerBanks = new HashSet<>();

	// Реквизиты банка получателя (поставщика)

	@Element(name = "ПолучательСчет", requiredInFormat = true)
	private String recipientAccount;

	@Element(name = "ДатаПоступило", requiredInFormat = true, dateFormat = "dd.MM.yyyy")
	private Date comingDate;

	@Element(name = "ПолучательИНН", requiredInFormat = true)
	private String recipientINN;

	@Element(name = "ПолучательКПП")
	private String recipientKPP;

	@Element(name = "ПолучательБИК")
	private String recipientBIK;

	@Element(name = "ПолучательРасчСчет")
	private String recipientCheckingAccount;

	@Element(name = "ПолучательКорсчет")
	private String recipientCorAccount;

	@Element(name = "Получатель[0-9]*", simpleField = false)
	private Set<String> recipients = new HashSet<>();

	@Element(name = "ПолучательБанк[0-9]*", simpleField = false)
	private Set<String> recipientBanks = new HashSet<>();

	// Реквизиты платежа

	@Element(name = "ВидПлатежа")
	private String paymentMethod;

	@Element(name = "ВидОплаты")
	private String paymentType;

	@Element(name = "СтатусСоставителя")
	private String documentState;

	@Element(name = "ПоказательКБК")
	private String kbk;

	@Element(name = "ОКАТО")
	private String okato;

	@Element(name = "ПоказательОснования")
	private String markReason;

	@Element(name = "ПоказательПериода")
	private String markPeriod;

	@Element(name = "ПоказательНомера")
	private String markNumber;

	@Element(name = "ПоказательДаты", dateFormat = "dd.MM.yyyy")
	private Date markDate;

	@Element(name = "ПоказательТипа")
	private String markType;

	@Element(name = "СрокПлатежа")
	private String paymentTime;

	@Element(name = "Очередность")
	private String priority;

	@Element(name = "НазначениеПлатежа[0-9]*", simpleField = false)
	private Set<String> paymentPurposes = new HashSet<>();

	@Element(name = "СрокАкцепта")
	private String accentTime;

	@Element(name = "ВидАккредитива")
	private String creditType;

	@Element(name = "УсловиеОплаты[0-9]*", simpleField = false)
	private Set<String> payTypes = new HashSet<>();

	@Element(name = "ПлатежПоПредст")
	private String paymentRepresentation;

	@Element(name = "ДополнУсловия")
	private String addConditions;

	@Element(name = "НомерСчетаПоставщика")
	private String supplierCheckingAccount;

	@Element(name = "ДатаОтсылкиДок", dateFormat = "dd.MM.yyyy")
	private Date sendDate;

	private List<String> warnings = new ArrayList<>();

	private List<String> errors = new ArrayList<>();

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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

	public Date getBillDate() {
		return billDate;
	}

	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}

	public String getBillTime() {
		return billTime;
	}

	public void setBillTime(String billTime) {
		this.billTime = billTime;
	}

	public String getBillDescription() {
		return billDescription;
	}

	public void setBillDescription(String billDescription) {
		this.billDescription = billDescription;
	}

	public String getPayerAccount() {
		return payerAccount;
	}

	public void setPayerAccount(String payerAccount) {
		this.payerAccount = payerAccount;
	}

	public String getWriteOffDate() {
		return writeOffDate;
	}

	public void setWriteOffDate(String writeOffDate) {
		this.writeOffDate = writeOffDate;
	}

	public String getPayerINN() {
		return payerINN;
	}

	public void setPayerINN(String payerINN) {
		this.payerINN = payerINN;
	}

	public String getPayerKPP() {
		return payerKPP;
	}

	public void setPayerKPP(String payerKPP) {
		this.payerKPP = payerKPP;
	}

	public String getPayerBIK() {
		return payerBIK;
	}

	public void setPayerBIK(String payerBIK) {
		this.payerBIK = payerBIK;
	}

	public String getPayerCheckingAccount() {
		return payerCheckingAccount;
	}

	public void setPayerCheckingAccount(String payerCheckingAccount) {
		this.payerCheckingAccount = payerCheckingAccount;
	}

	public String getPayerCorAccount() {
		return payerCorAccount;
	}

	public void setPayerCorAccount(String payerCorAccount) {
		this.payerCorAccount = payerCorAccount;
	}

	public Set<String> getPayers() {
		return payers;
	}

	public void setPayers(Set<String> payers) {
		this.payers = payers;
	}

	public Set<String> getPayerBanks() {
		return payerBanks;
	}

	public void setPayerBanks(Set<String> payerBanks) {
		this.payerBanks = payerBanks;
	}

	public String getRecipientAccount() {
		return recipientAccount;
	}

	public void setRecipientAccount(String recipientAccount) {
		this.recipientAccount = recipientAccount;
	}

	public Date getComingDate() {
		return comingDate;
	}

	public void setComingDate(Date comingDate) {
		this.comingDate = comingDate;
	}

	public String getRecipientINN() {
		return recipientINN;
	}

	public void setRecipientINN(String recipientINN) {
		this.recipientINN = recipientINN;
	}

	public String getRecipientKPP() {
		return recipientKPP;
	}

	public void setRecipientKPP(String recipientKPP) {
		this.recipientKPP = recipientKPP;
	}

	public String getRecipientBIK() {
		return recipientBIK;
	}

	public void setRecipientBIK(String recipientBIK) {
		this.recipientBIK = recipientBIK;
	}

	public String getRecipientCheckingAccount() {
		return recipientCheckingAccount;
	}

	public void setRecipientCheckingAccount(String recipientCheckingAccount) {
		this.recipientCheckingAccount = recipientCheckingAccount;
	}

	public String getRecipientCorAccount() {
		return recipientCorAccount;
	}

	public void setRecipientCorAccount(String recipientCorAccount) {
		this.recipientCorAccount = recipientCorAccount;
	}

	public Set<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(Set<String> recipients) {
		this.recipients = recipients;
	}

	public Set<String> getRecipientBanks() {
		return recipientBanks;
	}

	public void setRecipientBanks(Set<String> recipientBanks) {
		this.recipientBanks = recipientBanks;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getDocumentState() {
		return documentState;
	}

	public void setDocumentState(String documentState) {
		this.documentState = documentState;
	}

	public String getKbk() {
		return kbk;
	}

	public void setKbk(String kbk) {
		this.kbk = kbk;
	}

	public String getOkato() {
		return okato;
	}

	public void setOkato(String okato) {
		this.okato = okato;
	}

	public String getMarkReason() {
		return markReason;
	}

	public void setMarkReason(String markReason) {
		this.markReason = markReason;
	}

	public String getMarkPeriod() {
		return markPeriod;
	}

	public void setMarkPeriod(String markPeriod) {
		this.markPeriod = markPeriod;
	}

	public String getMarkNumber() {
		return markNumber;
	}

	public void setMarkNumber(String markNumber) {
		this.markNumber = markNumber;
	}

	public Date getMarkDate() {
		return markDate;
	}

	public void setMarkDate(Date markDate) {
		this.markDate = markDate;
	}

	public String getMarkType() {
		return markType;
	}

	public void setMarkType(String markType) {
		this.markType = markType;
	}

	public String getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(String paymentTime) {
		this.paymentTime = paymentTime;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Set<String> getPaymentPurposes() {
		return paymentPurposes;
	}

	public void setPaymentPurposes(Set<String> paymentPurposes) {
		this.paymentPurposes = paymentPurposes;
	}

	public String getAccentTime() {
		return accentTime;
	}

	public void setAccentTime(String accentTime) {
		this.accentTime = accentTime;
	}

	public String getCreditType() {
		return creditType;
	}

	public void setCreditType(String creditType) {
		this.creditType = creditType;
	}

	public Set<String> getPayTypes() {
		return payTypes;
	}

	public void setPayTypes(Set<String> payTypes) {
		this.payTypes = payTypes;
	}

	public String getPaymentRepresentation() {
		return paymentRepresentation;
	}

	public void setPaymentRepresentation(String paymentRepresentation) {
		this.paymentRepresentation = paymentRepresentation;
	}

	public String getAddConditions() {
		return addConditions;
	}

	public void setAddConditions(String addConditions) {
		this.addConditions = addConditions;
	}

	public String getSupplierCheckingAccount() {
		return supplierCheckingAccount;
	}

	public void setSupplierCheckingAccount(String supplierCheckingAccount) {
		this.supplierCheckingAccount = supplierCheckingAccount;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
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