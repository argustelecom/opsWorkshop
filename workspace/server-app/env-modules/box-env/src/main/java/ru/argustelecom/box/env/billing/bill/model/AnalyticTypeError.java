package ru.argustelecom.box.env.billing.bill.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.inf.nls.LocaleUtils;

/**
 * Описание возможных ошибок, при возникновении которых невозможно расчитать аналитики и сырые данные по ним считаются
 * не валидными.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum AnalyticTypeError {

	//@formatter:off
	START_PERIOD_DATE_AFTER_END_DATE("{BillBundle:box.bill.analytic.type.error.period}"),
	SUMMARY_HAS_INVALID_DATA        ("{BillBundle:box.bill.analytic.type.error.data}");
	//@formatter:on

	private String bundleProperty;

	public String getName() {
		return LocaleUtils.getLocalizedMessage(bundleProperty, getClass());
	}

}