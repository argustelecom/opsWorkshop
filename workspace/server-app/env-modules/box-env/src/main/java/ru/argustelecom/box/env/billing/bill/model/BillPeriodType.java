package ru.argustelecom.box.env.billing.bill.model;

import static ru.argustelecom.box.env.stl.period.PeriodUnit.MONTH;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.QUARTER;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.SEMESTER;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.YEAR;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.arrayOf;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.billing.bill.nls.BillMessagesBundle;
import ru.argustelecom.box.env.stl.nls.PeriodMessagesBundle;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BillPeriodType {

	//@formatter:off
	CALENDARIAN (arrayOf(MONTH, QUARTER, SEMESTER, YEAR)),
	CUSTOM		(null);
	//@formatter:on

	@Getter
	private PeriodUnit[] units;

	public String getName() {
		PeriodMessagesBundle messages = LocaleUtils.getMessages(PeriodMessagesBundle.class);

		switch (this) {
			case CALENDARIAN:
				return messages.periodTypeCalendarian();
			case CUSTOM:
				return messages.periodTypeCustom();
			default:
				throw new SystemException("Unsupported BillPeriodType");
		}
	}

}