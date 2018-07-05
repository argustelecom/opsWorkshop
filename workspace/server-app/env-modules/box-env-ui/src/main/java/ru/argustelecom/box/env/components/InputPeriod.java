package ru.argustelecom.box.env.components;

import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDate;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDate;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.Periods.Month;
import ru.argustelecom.box.env.billing.bill.model.Periods.Quarter;
import ru.argustelecom.box.env.billing.bill.model.Periods.Semester;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@FacesComponent("inputPeriod")
public class InputPeriod extends UIInput implements NamingContainer {

	private static final String ATTR_PERIOD_UNIT = "periodUnit";
	private static final String ATTR_VALUE = "value";
	private static final String ATTR_WITH_TIME = "withTime";
	public static final String INPUT_PERIOD_BUNDLE = "InputPeriod";
	private ResourceBundle bundle;

	@Override
	public String getFamily() {
		return UINamingContainer.COMPONENT_FAMILY;
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		BillPeriod period = (BillPeriod) getAttributes().get(ATTR_VALUE);

		if (period != null) {

			LocalDate startDate = toLocalDate(period.startDate());

			setYear(startDate.getYear());
			setMonth(Month.values()[startDate.getMonthValue() - 1]);
			setQuarter(Quarter.values()[((startDate.getMonthValue() - 1) / 3)]);
			setSemester(Semester.values()[((startDate.getMonthValue() - 1) / 6)]);
			setStartDate(getPeriodStartDate(period));
			setEndDate(getPeriodEndDate(period));
		}

		super.encodeBegin(context);
	}

	private Date getPeriodStartDate(BillPeriod period) {
		boolean withTime = (Boolean) getAttributes().get(ATTR_WITH_TIME);
		if (period != null) {
			return withTime ? fromLocalDateTime(period.startDateTime()) : period.startDate();
		}
		return withTime ? fromLocalDateTime(LocalDateTime.now()) : fromLocalDate(LocalDate.now());
	}

	private Date getPeriodEndDate(BillPeriod period) {
		boolean withTime = (Boolean) getAttributes().get(ATTR_WITH_TIME);
		if (period != null) {
			return withTime ? fromLocalDateTime(period.endDateTime()) : period.endDate();
		}
		return withTime ? fromLocalDateTime(LocalDateTime.now()) : fromLocalDate(LocalDate.now());
	}

	@Override
	public Object getSubmittedValue() {
		if (!hasSubmittedValue()) {
			return null;
		}

		PeriodUnit periodUnit = getPeriodUnit();

		if (periodUnit != null) {
			LocalDateTime poi;
			switch (periodUnit) {
			case MONTH:
				poi = LocalDateTime.of(getYear(), getMonth().ordinal() + 1, 1, 0, 0);
				break;
			case QUARTER:
				poi = LocalDateTime.of(getYear(), (getQuarter().ordinal() + 1) * 3, 1, 0, 0);
				break;
			case SEMESTER:
				poi = LocalDateTime.of(getYear(), (getSemester().ordinal() + 1) * 6, 1, 0, 0);
				break;
			case YEAR:
				poi = LocalDateTime.of(getYear(), 1, 1, 0, 0);
				break;
			default:
				throw new SystemException();
			}
			return BillPeriod.of(periodUnit, poi);
		}

		return BillPeriod.of(toLocalDateTime(getStartDate()), toLocalDateTime(getEndDate()));
	}

	@Override
	protected Object getConvertedValue(FacesContext context, Object newSubmittedValue) throws ConverterException {
		return newSubmittedValue;
	}

	/**
	 * Формирует интервал годов, верхней границей которого является текущий + 1, а нижней текущий - 100.
	 */
	public int[] getYears() {
		int from = LocalDateTime.now().getYear() + 2;
		int to = from - 101;
		return IntStream.range(to, from).map(i -> to - i + from - 1).toArray();
	}

	public ResourceBundle getBundle() {
		if (bundle == null) {
			return bundle = LocaleUtils.getBundle(INPUT_PERIOD_BUNDLE, getClass());
		}
		return bundle;
	}

	public Month getMonth() {
		return (Month) getStateHelper().get("month");
	}

	public void setMonth(Month month) {
		getStateHelper().put("month", month);
	}

	public Quarter getQuarter() {
		return (Quarter) getStateHelper().get("quarter");
	}

	public void setQuarter(Quarter quarter) {
		getStateHelper().put("quarter", quarter);
	}

	public Semester getSemester() {
		return (Semester) getStateHelper().get("semester");
	}

	public void setSemester(Semester semester) {
		getStateHelper().put("semester", semester);
	}

	public int getYear() {
		return (int) Optional.ofNullable(getStateHelper().get("year")).orElse(LocalDateTime.now().getYear());
	}

	public void setYear(int year) {
		getStateHelper().put("year", year);
	}

	public Date getStartDate() {
		return (Date) getStateHelper().get("startDate");
	}

	public void setStartDate(Date startDate) {
		getStateHelper().put("startDate", startDate);
	}

	public Date getEndDate() {
		return (Date) getStateHelper().get("endDate");
	}

	public void setEndDate(Date endDate) {
		getStateHelper().put("endDate", endDate);
	}

	private PeriodUnit getPeriodUnit() {
		return (PeriodUnit) getAttributes().get(ATTR_PERIOD_UNIT);
	}

	/**
	 * Проверяет был ли введён корректный период (календарный или произвольный).
	 */
	private boolean hasSubmittedValue() {
		boolean hasPeriodUnit = getPeriodUnit() != null;
		boolean hasCalendarPeriodValue = ((getMonth() != null || getQuarter() != null || getSemester() != null)
				&& getYear() != 0) || (hasPeriodUnit && getPeriodUnit() == PeriodUnit.YEAR && getYear() != 0);

		boolean validCalendarPeriod = hasPeriodUnit && hasCalendarPeriodValue;
		boolean validCustomPeriod = getStartDate() != null && getEndDate() != null;

		return validCalendarPeriod || validCustomPeriod;
	}

}