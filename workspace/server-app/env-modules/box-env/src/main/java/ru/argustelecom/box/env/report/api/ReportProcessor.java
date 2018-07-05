package ru.argustelecom.box.env.report.api;

import java.io.OutputStream;
import java.io.Serializable;

import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.env.report.model.ReportParams;
import ru.argustelecom.box.env.report.model.ReportType;

public interface ReportProcessor extends Serializable {

	OutputStream process(ReportPattern pattern, ReportContext context);

	void process(ReportPattern pattern, ReportContext context, OutputStream output);

	OutputStream process(ReportType reportType, ReportParams reportParams, ReportModelTemplate template,
			ReportOutputFormat outputFormat);

	void process(ReportType reportType, ReportParams reportParams, ReportModelTemplate template,
			ReportOutputFormat outputFormat, OutputStream output);
}