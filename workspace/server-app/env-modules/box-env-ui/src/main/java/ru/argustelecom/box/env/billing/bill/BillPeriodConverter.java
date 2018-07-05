package ru.argustelecom.box.env.billing.bill;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.chrono.ChronoUtils;

@FacesConverter(forClass = BillPeriod.class)
public class BillPeriodConverter implements Converter {

	private Pattern calendarPeriod = Pattern.compile("([A-Z]+):([0-9]+)");
	private Pattern customPeriod = Pattern.compile("([0-9]+):([0-9]+)");

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}

		Matcher matcher = calendarPeriod.matcher(value);
		if (matcher.matches()) {
			PeriodUnit billingPeriodUnit = PeriodUnit.valueOf(matcher.group(1));
			Long dateTime = Long.valueOf(matcher.group(2));

			return BillPeriod.of(billingPeriodUnit, ChronoUtils.fromLong(dateTime));
		}

		matcher = customPeriod.matcher(value);
		if (matcher.matches()) {
			LocalDateTime start = ChronoUtils.fromLong(Long.valueOf(matcher.group(1)));
			LocalDateTime end = ChronoUtils.fromLong(Long.valueOf(matcher.group(2)));

			return BillPeriod.of(start, end);
		}

		throw new ConverterException("");
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		BillPeriod billPeriod = (BillPeriod) value;

		if (billPeriod.getPeriodUnit() != null) {
			return billPeriod.getPeriodUnit().name() + ":"
					+ ChronoUtils.fromLocalDateTime(billPeriod.startDateTime()).getTime();
		} else {
			return ChronoUtils.fromLocalDateTime(billPeriod.startDateTime()).getTime() + ":"
					+ ChronoUtils.fromLocalDateTime(billPeriod.endDateTime()).getTime();
		}
	}
}
