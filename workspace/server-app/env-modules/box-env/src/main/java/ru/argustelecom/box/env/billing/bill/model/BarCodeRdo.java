package ru.argustelecom.box.env.billing.bill.model;

import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.api.data.ReportDataImage;
import ru.argustelecom.box.env.report.api.data.format.ReportImageFormat;

public class BarCodeRdo extends ReportData {

	@ReportImageFormat
	private ReportDataImage value;

	public BarCodeRdo(Long id, ReportDataImage value) {
		super(id);
		this.value = value;
	}

}
