package ru.argustelecom.box.env.billing.bill.model;

import java.time.LocalDateTime;

import lombok.Getter;

public enum BillDateGetter {

	PREVIOUS_PERIOD {
		@Override
		public LocalDateTime getStartDate(BillPeriodDate arg) {
			return arg.getBillingPeriod().prev().startDateTime();
		}

		@Override
		public LocalDateTime getEndDate(BillPeriodDate arg) {
			return arg.getBillingPeriod().prev().endDateTime();
		}
	},

	NEXT_PERIOD {
		@Override
		public LocalDateTime getStartDate(BillPeriodDate arg) {
			return arg.getBillingPeriod().next().startDateTime();
		}

		@Override
		public LocalDateTime getEndDate(BillPeriodDate arg) {
			return arg.getBillingPeriod().next().endDateTime();
		}
	},

	CURRENT_PERIOD {
		@Override
		public LocalDateTime getStartDate(BillPeriodDate arg) {
			return arg.getBillingPeriod().startDateTime();
		}

		@Override
		public LocalDateTime getEndDate(BillPeriodDate arg) {
			return arg.getBillingPeriod().endDateTime();
		}
	},

	NEXT_PERIOD_INCOMES_BEFORE_BILL_DATE {
		@Override
		public LocalDateTime getStartDate(BillPeriodDate arg) {
			return arg.getBillingPeriod().startDateTime();
		}

		@Override
		public LocalDateTime getEndDate(BillPeriodDate arg) {
			return arg.getInvoiceDateTime();
		}

	},

	NEXT_PERIOD_INCOMES_BEFORE_BILL_CREATION_DATE {
		@Override
		public LocalDateTime getStartDate(BillPeriodDate arg) {
			return arg.getBillingPeriod().next().startDateTime();
		}

		@Override
		public LocalDateTime getEndDate(BillPeriodDate arg) {
			return arg.getCreationDateTime();
		}
	},

	PREVIOUS_PERIOD_STARTING_BALANCE {
		@Override
		public LocalDateTime getDate(BillPeriodDate arg) {
			return arg.getBillingPeriod().prev().startDateTime();
		}
	},

	CURRENT_PERIOD_STARTING_BALANCE {
		@Override
		public LocalDateTime getDate(BillPeriodDate arg) {
			return arg.getBillingPeriod().startDateTime();
		}
	},

	CURRENT_PERIOD_ENDING_BALANCE {
		@Override
		public LocalDateTime getDate(BillPeriodDate arg) {
			return arg.getBillingPeriod().endDateTime();
		}
	},

	NEXT_PERIOD_ENDING_BALANCE {
		@Override
		public LocalDateTime getDate(BillPeriodDate arg) {
			return arg.getBillingPeriod().next().endDateTime();
		}
	},

	BILL_DATE_BALANCE {
		@Override
		public LocalDateTime getDate(BillPeriodDate arg) {
			return arg.getInvoiceDateTime();
		}
	},

	BILL_CREATION_DATE_BALANCE {
		@Override
		public LocalDateTime getDate(BillPeriodDate arg) {
			return arg.getCreationDateTime();
		}
	};

	public LocalDateTime getStartDate(BillPeriodDate arg) {
		return arg.getBillingPeriod().startDateTime();
	}

	public LocalDateTime getEndDate(BillPeriodDate arg) {
		return arg.getBillingPeriod().endDateTime();
	}

	public LocalDateTime getDate(BillPeriodDate arg) {
		return null;
	}

	@Getter
	public static class BillPeriodDate {

		/**
		 * Период выставления счёта.
		 */
		private BillPeriod billingPeriod;

		/**
		 * Дата выставления счёта.
		 */
		private LocalDateTime invoiceDateTime;

		/**
		 * Дата создания счёта. Системная дата формирования счёта.
		 */
		private LocalDateTime creationDateTime;

		public BillPeriodDate(BillPeriod billingPeriod, LocalDateTime invoiceDateTime, LocalDateTime creationDateTime) {
			this.billingPeriod = billingPeriod;
			this.invoiceDateTime = invoiceDateTime;
			this.creationDateTime = creationDateTime;
		}
	}

}