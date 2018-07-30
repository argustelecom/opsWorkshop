package ru.argustelecom.box.env.components;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.Date;
import java.util.ResourceBundle;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalDateTime.Property;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import ru.argustelecom.box.env.datetime.model.DateIntervalValue;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.chrono.TZ;

@FacesComponent("inputDateIntervalValue")
public class InputDateIntervalValue extends AbstractCompositeInput {

	private ResourceBundle bundle;

	private DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		DateIntervalValue value = getValue();
		if (value != null) {
			setPrivateState(PropertyKeys.startDate, value.getStartDate());
			setPrivateState(PropertyKeys.endDate, value.getEndDate());
		}
		super.encodeBegin(context);
	}

	public void clean() {
		setValue(null);
		setPrivateState(PropertyKeys.startDate, null);
		setPrivateState(PropertyKeys.endDate, null);
	}

	public void thisDay() {
		LocalDateTime now = now();
		LocalDateTime startMoment = now.withTime(0, 0, 0, 0);
		LocalDateTime endMoment = now.withTime(23, 59, 59, 999);

		setStartDate(startMoment.toDate(TZ.getUserTimeZone()));
		setEndDate(endMoment.toDate(TZ.getUserTimeZone()));
	}

	public void thisWeek() {
		setStandardInterval(now().dayOfWeek());
	}

	public void thisMonth() {
		setStandardInterval(now().dayOfMonth());
	}

	public void thisQuarter() {
		LocalDateTime now = now();
		LocalDateTime startMoment = now.withDayOfMonth(1).withMonthOfYear((((now.getMonthOfYear() - 1) / 3) * 3) + 1)
				.withTime(0, 0, 0, 0);
		LocalDateTime endMoment = startMoment.plusMonths(3).minusDays(1).withTime(23, 59, 59, 999);

		setStartDate(startMoment.toDate(TZ.getUserTimeZone()));
		setEndDate(endMoment.toDate(TZ.getUserTimeZone()));
	}

	public void thisYear() {
		setStandardInterval(now().dayOfYear());
	}

	protected void setStandardInterval(Property factor) {
		LocalDateTime startMoment = factor.withMinimumValue().withTime(0, 0, 0, 0);
		LocalDateTime endMoment = factor.withMaximumValue().withTime(23, 59, 59, 999);

		setStartDate(startMoment.toDate(TZ.getUserTimeZone()));
		setEndDate(endMoment.toDate(TZ.getUserTimeZone()));
	}

	protected LocalDateTime now() {
		return LocalDateTime.now(DateTimeZone.forTimeZone(TZ.getUserTimeZone()));
	}

	protected void updateValue() {
		Date startDate = getStartDate();
		Date endDate = getEndDate();

		if (startDate == null || endDate == null) {
			this.setValue(null);
			return;
		}

		DateIntervalValue value = getValue();
		if (value != null) {
			value.setStartDate(startDate);
			value.setEndDate(endDate);
		} else {
			value = new DateIntervalValue(startDate, endDate);
			this.setValue(value);
		}
	}

	public ResourceBundle getBundle() {
		if (bundle == null) {
			return LocaleUtils.getBundle("InputDateIntervalValueBundle", getClass());
		}
		return bundle;
	}

	@Override
	public Object getSubmittedValue() {
		Date startDate = getStartDate();
		Date endDate = getEndDate();
		if (startDate == null || endDate == null) {
			return "-";
		}
		return formatter.print(startDate.getTime()) + ";" + formatter.print(endDate.getTime());
	}

	@Override
	protected Object getConvertedValue(FacesContext context, Object submittedValue) {
		if ("-".equals(submittedValue)) {
			return null;
		}

		String[] valueParts = ((String) submittedValue).split(";");
		checkState(valueParts.length == 2);

		DateTime startDate = formatter.parseDateTime(valueParts[0]);
		DateTime endDate = formatter.parseDateTime(valueParts[1]);
		return new DateIntervalValue(startDate.toDate(), endDate.toDate());
	}

	@Override
	public DateIntervalValue getValue() {
		return (DateIntervalValue) super.getValue();
	}

	public Date getStartDate() {
		return getPrivateState(PropertyKeys.startDate, Date.class);
	}

	public void setStartDate(Date startDate) {
		setPrivateState(PropertyKeys.startDate, startDate);
		updateValue();
	}

	public Date getEndDate() {
		return getPrivateState(PropertyKeys.endDate, Date.class);
	}

	public void setEndDate(Date endDate) {
		setPrivateState(PropertyKeys.endDate, endDate);
		updateValue();
	}

	static enum PropertyKeys {
		startDate, endDate
	}

}
