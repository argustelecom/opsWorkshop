package ru.argustelecom.box.env.billing.klto1c.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Контейнер для хранения информации из секции "Расчётный счёт".
 */
public class CheckingAccount implements KLto1CDataObject {

	@Element(name = "РасчСчет", requiredInFormat = true)
	private String number;

	@Element(name = "ДатаНачала", requiredInFormat = true)
	private Date startDate;

	@Element(name = "ДатаКонца")
	private Date endDate;

	@Element(name = "НачальныйОстаток", requiredInFormat = true)
	private String startBalance;

	@Element(name = "КонечныйОстаток")
	private String endBalance;

	@Element(name = "ВсегоПоступило")
	private String receiptAmount;

	@Element(name = "ВсегоСписано")
	private String writeOffAmount;

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

	public String getStartBalance() {
		return startBalance;
	}

	public void setStartBalance(String startBalance) {
		this.startBalance = startBalance;
	}

	public String getEndBalance() {
		return endBalance;
	}

	public void setEndBalance(String endBalance) {
		this.endBalance = endBalance;
	}

	public String getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(String receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getWriteOffAmount() {
		return writeOffAmount;
	}

	public void setWriteOffAmount(String writeOffAmount) {
		this.writeOffAmount = writeOffAmount;
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