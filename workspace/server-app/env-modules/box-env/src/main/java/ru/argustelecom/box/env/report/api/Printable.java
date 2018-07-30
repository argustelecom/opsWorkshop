package ru.argustelecom.box.env.report.api;

import ru.argustelecom.box.env.report.api.data.ReportData;

public interface Printable {

	default void fillReportContext(ReportContext reportContext) {
	}

	ReportData createReportData();

}