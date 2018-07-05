package ru.argustelecom.box.env.billing.invoice.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceDto;
import ru.argustelecom.box.env.stl.period.AccountingPeriod;

public class AccountingMode {

	private AccountingPeriod currentStep;
	private AccountingModeType mode = AccountingModeType.THREE;
	private List<AccountingPeriod> periods = new ArrayList<>();
	private Date subscriptionStartDate;

	public AccountingMode(AccountingPeriod accountingPeriod, Date subscriptionStartDate) {
		this.subscriptionStartDate = subscriptionStartDate;
		initCurrentStep(accountingPeriod);
		initAccountingPeriods();
	}

	public void updateModel(AccountingPeriod accountingPeriod) {
		initCurrentStep(accountingPeriod);
		initAccountingPeriods();
	}

	public boolean nextStep() {
		try {
			AccountingPeriod nextStep = mode.nextStep(currentStep);
			if (nextStep != currentStep) {
				setCurrentStep(nextStep);
				initAccountingPeriods();
				return true;
			}
		} catch (IllegalArgumentException e) {
			return false;
		}

		return false;
	}

	public boolean previousStep() {
		try {
			AccountingPeriod prevStep = mode.previousStep(currentStep, subscriptionStartDate);
			if (prevStep != currentStep) {
				setCurrentStep(prevStep);
				initAccountingPeriods();
				return true;
			}

		} catch (IllegalArgumentException e) {
			return false;
		}

		return false;
	}

	public AccountingPeriod getAccountingPeriodByInvoice(LongTermInvoiceDto invoiceEntryDto) {
		return mode.getAccountingPeriodByInvoice(invoiceEntryDto, currentStep);
	}

	private void initAccountingPeriods() {
		periods = mode.calculateAccountingPeriods(currentStep);
	}

	public Date getTimeLineStart() {
		return mode.getTimeLineStart(currentStep);
	}

	public Date getTimeLineEnd() {
		return mode.getTimeLineEnd(currentStep);
	}

	private void initCurrentStep(AccountingPeriod accountingPeriod) {
		currentStep = getCorrectPeriod(accountingPeriod, subscriptionStartDate);
	}

	private AccountingPeriod getCorrectPeriod(AccountingPeriod accountingPeriod, Date subscriptionStartDate) {

		return mode.getCorrectPeriod(accountingPeriod, subscriptionStartDate);
	}

	public AccountingPeriod getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(AccountingPeriod currentStep) {
		this.currentStep = currentStep;
	}

	public AccountingModeType getMode() {
		return mode;
	}

	public void setMode(AccountingModeType mode) {
		if (this.mode != mode) {

			setCurrentStep(this.mode.correctionStep(currentStep));
			this.mode = mode;
			initAccountingPeriods();
		}
	}

	public List<AccountingPeriod> getPeriods() {
		return periods;
	}

	public Date getSubscriptionStartDate() {
		return subscriptionStartDate;
	}

}
