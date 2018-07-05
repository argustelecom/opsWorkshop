package ru.argustelecom.box.env.billing.invoice.demo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceDto;
import ru.argustelecom.box.env.stl.period.AccountingPeriod;
import ru.argustelecom.box.env.stl.period.AbstractPeriod;
import ru.argustelecom.system.inf.chrono.TZ;

public enum AccountingModeType {
	ONE("Один расчетный период", 1) {
		@Override
		public AccountingPeriod getCorrectPeriod(AccountingPeriod accountingPeriod, Date subscriptionStartDate) {
			return accountingPeriod;
		}

		@Override
		public AccountingPeriod getAccountingPeriodByInvoice(LongTermInvoiceDto invoiceEntryDto, AccountingPeriod currentStep) {
			return currentStep;
		}

		@Override
		public AccountingPeriod correctionStep(AccountingPeriod currentStep) {
			return currentStep.next();
		}

		@Override
		public Long calculateZoomMin(AccountingPeriod currentStep) {
			long currentMillis =  periodEndDate(currentStep).getTime() -  periodStartDate(currentStep).getTime();

			return currentMillis / currentStep.chargingPeriods().size();
		}

		@Override
		public Date getTimeLineStart(AccountingPeriod currentStep) {
			return periodStartDate(currentStep);
		}

		@Override
		public Date getTimeLineEnd(AccountingPeriod currentStep) {
			return periodEndDate(currentStep);
		}

		@Override
		public AccountingPeriod previousStep(AccountingPeriod currentStep, Date subscriptionStartDate) {
			if (periodEndDate(currentStep.prev()).after(subscriptionStartDate)) {
				return currentStep.prev();
			} else {
				return currentStep;
			}
		}

		@Override
		public AccountingPeriod nextStep(AccountingPeriod currentStep) {
			if (periodStartDate(currentStep.next()).before(new LocalDate().plusYears(1).toDate())) {
				return currentStep.next();
			} else {
				return currentStep;
			}
		}

		@Override
		public List<AccountingPeriod> calculateAccountingPeriods(AccountingPeriod currentStep) {
			return Collections.singletonList(currentStep);
		}
	},

	THREE("Три расчетных периода", 3){
		@Override
		public AccountingPeriod getCorrectPeriod(AccountingPeriod accountingPeriod, Date subscriptionStartDate) {
			try {
				if (periodEndDate(accountingPeriod.prev()).after(subscriptionStartDate)) {
					return accountingPeriod;
				} else {
					return getCorrectPeriod(accountingPeriod.next(), subscriptionStartDate);
				}
			} catch (IllegalArgumentException e) {
				return getCorrectPeriod(accountingPeriod.next(), subscriptionStartDate);
			}
		}

		@Override
		public AccountingPeriod getAccountingPeriodByInvoice(LongTermInvoiceDto invoiceEntryDto, AccountingPeriod currentStep) {
			if (checkDate(currentStep.next(), invoiceEntryDto.getStartDate())) {
				return currentStep.next();
			} else if (checkDate(currentStep.prev(), invoiceEntryDto.getStartDate())) {
				return currentStep.prev();
			} else {
				return currentStep;
			}
		}

		@Override
		public AccountingPeriod correctionStep(AccountingPeriod currentStep) {
			return currentStep.prev();
		}

		@Override
		public Long calculateZoomMin(AccountingPeriod currentStep) {

			return periodEndDate(currentStep).getTime() -  periodStartDate(currentStep).getTime();
		}

		@Override
		public Date getTimeLineStart(AccountingPeriod currentStep) {
			return periodStartDate(currentStep.prev());
		}

		@Override
		public Date getTimeLineEnd(AccountingPeriod currentStep) {
			return periodEndDate(currentStep.next());
		}

		@Override
		public AccountingPeriod previousStep(AccountingPeriod currentStep, Date subscriptionStartDate) {
			if (periodEndDate(currentStep.prev().prev()).after(subscriptionStartDate)) {
				return currentStep.prev();
			} else {
				return currentStep;
			}
		}

		@Override
		public AccountingPeriod nextStep(AccountingPeriod currentStep) {
			if (periodStartDate(currentStep.next().next()).before(new LocalDate().plusYears(1).toDate())) {
				return currentStep.next();
			} else {
				return currentStep;
			}
		}

		@Override
		public List<AccountingPeriod> calculateAccountingPeriods(AccountingPeriod currentStep) {
			return Arrays.asList(currentStep.prev(), currentStep, currentStep.next());
		}
	};

	private String name;
	private int lenght;

	private AccountingModeType(String name, int lenght) {
		this.name = name;
		this.lenght = lenght;
	}

	public String getName() {
		return name;
	}

	public int getLenght() {
		return lenght;
	}

	public Date periodStartDate(AbstractPeriod period) {
		return Date.from(period.boundaries().lowerEndpoint().atZone(TZ.getServerZoneId())
				.toInstant());
	}

	public Date periodEndDate(AbstractPeriod period) {
		return Date.from(period.boundaries().upperEndpoint().atZone(TZ.getServerZoneId())
				.toInstant());
	}

	public Boolean checkDate(AccountingPeriod accountingPeriod, Date checkDate) {
		Interval interval = new Interval(new DateTime(periodStartDate(accountingPeriod)),
				new DateTime(periodEndDate(accountingPeriod)));
		return interval.contains(new DateTime(checkDate));
	}

	public abstract AccountingPeriod getCorrectPeriod(AccountingPeriod accountingPeriod, Date subscriptionStartDate);
	public abstract AccountingPeriod getAccountingPeriodByInvoice(LongTermInvoiceDto invoiceEntryDto, AccountingPeriod currentStep);
	public abstract AccountingPeriod correctionStep(AccountingPeriod currentStep);
	public abstract Long calculateZoomMin(AccountingPeriod currentStep);
	public abstract Date getTimeLineStart(AccountingPeriod currentStep);
	public abstract Date getTimeLineEnd(AccountingPeriod currentStep);
	public abstract AccountingPeriod previousStep(AccountingPeriod currentStep, Date subscriptionStartDate);
	public abstract AccountingPeriod nextStep(AccountingPeriod currentStep);
	public abstract List<AccountingPeriod> calculateAccountingPeriods(AccountingPeriod currentStep);

}
