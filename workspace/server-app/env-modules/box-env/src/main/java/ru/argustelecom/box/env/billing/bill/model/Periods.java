package ru.argustelecom.box.env.billing.bill.model;

import java.time.LocalDateTime;

import ru.argustelecom.box.env.billing.bill.nls.BillMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

public final class Periods {

	private Periods() {
	}

	public enum Month {

		//@formatter:off
		JANUARY,
		FEBRUARY,
		MARCH,
		APRIL,
		MAY,
		JUNE,
		JULY,
		AUGUST,
		SEPTEMBER,
		OCTOBER,
		NOVEMBER,
		DECEMBER;
		//@formatter:on
		
		public static Month of(LocalDateTime poi) {
			return values()[poi.getMonthValue() - 1];
		}
		
		public String getName() {
			BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);

			switch (this) {
				case JANUARY:
					return messages.monthJanuary();
				case FEBRUARY:
					return messages.monthFebruary();
				case MARCH:
					return messages.monthMarch();
				case APRIL:
					return messages.monthApril();
				case MAY:
					return messages.monthMay();
				case JUNE:
					return messages.monthJune();
				case JULY:
					return messages.monthJuly();
				case AUGUST:
					return messages.monthAugust();
				case SEPTEMBER:
					return messages.monthSeptember();
				case OCTOBER:
					return messages.monthOctober();
				case NOVEMBER:
					return messages.monthNovember();
				case DECEMBER:
					return messages.monthDecember();
				default:
					throw new SystemException("Unsupported Periods.Month");
			}
		}
	}

	public enum Quarter {

		//@formatter:off
		I,
		II,
		III,
		IV;
		//@formatter:on
		
		public static Quarter of(LocalDateTime poi) {
			return values()[(poi.getMonthValue() - 1) / 3];
		}
		
		public String getName() {
			BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);

			switch (this) {
				case I:
					return messages.quarterFirst();
				case II:
					return messages.quarterSecond();
				case III:
					return messages.quarterThird();
				case IV:
					return messages.quarterFourth();
				default:
					throw new SystemException("Unsupported Periods.Quarter");
			}
		}
	}

	public enum Semester {

		//@formatter:off
		FIRST,
		SECOND;
		//@formatter:on
		
		public static Semester of(LocalDateTime poi) {
			return values()[(poi.getMonthValue() - 1) / 6];
		}
		
		public String getName() {
			BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);

			switch (this) {
				case FIRST:
					return messages.semesterFirst();
				case SECOND:
					return messages.semesterSecond();
				default:
					throw new SystemException("Unsupported Periods.Semester");
			}
		}
	}
}
