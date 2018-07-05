package ru.argustelecom.box.env.report.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import com.haulmont.yarg.reporting.Reporting;
import com.haulmont.yarg.reporting.RunParams;
import com.haulmont.yarg.structure.Report;

import ru.argustelecom.box.env.report.api.ReportContext;
import ru.argustelecom.box.env.report.api.ReportOutputFormat;
import ru.argustelecom.box.env.report.api.ReportPattern;
import ru.argustelecom.box.env.report.api.ReportProcessor;
import ru.argustelecom.box.env.report.impl.yarg.FormatterFactory;
import ru.argustelecom.box.env.report.impl.yarg.LoaderFactory;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.env.report.model.ReportParams;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class ReportProcessorImpl implements ReportProcessor {

	private static final Logger log = Logger.getLogger(ReportProcessorImpl.class);

	@Inject
	private DocumentReportBuilder documentReportBuilder;

	@Inject
	private DatabaseReportBuilder databaseReportBuilder;

	@Inject
	private LoaderFactory loaderFactory;

	@Inject
	private FormatterFactory formatterFactory;

	@Override
	public OutputStream process(ReportPattern pattern, ReportContext context) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		process(pattern, context, output);
		return output;
	}

	@Override
	public void process(ReportPattern pattern, ReportContext context, OutputStream output) {
		log.debug("Processing Report...");
		log.debugv("  ReportPattern: {0}", pattern);
		log.debugv("  ReportContext: {0}", context);

		RunParams runParams = documentReportBuilder.createReportData(pattern, context);
		createReporting(runParams, output);
	}

	@Override
	public OutputStream process(ReportType reportType, ReportParams reportParams, ReportModelTemplate template,
			ReportOutputFormat outputFormat) {
		log.debug("Processing Report...");

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		process(reportType, reportParams, template, outputFormat, output);
		return output;
	}

	@Override
	public void process(ReportType reportType, ReportParams reportParams, ReportModelTemplate template,
			ReportOutputFormat outputFormat, OutputStream output) {
		log.debug("Processing Report...");

		Report report = databaseReportBuilder.createReport(reportType, reportParams, template, outputFormat);
		RunParams runParams = new RunParams(report);

		reportParams.getPropertyValueMap().keySet().forEach(key -> {
			Object propertyValue = reportParams.getPropertyValue(key);
			if (propertyValue instanceof Date) {
				propertyValue = new java.sql.Date(((Date) propertyValue).getTime());
			}
			runParams.param(key, propertyValue);
		});

		createReporting(runParams, output);
	}

	private void createReporting(RunParams runParams, OutputStream output) {
		Reporting reporting = new Reporting();
		reporting.setFormatterFactory(formatterFactory);
		reporting.setLoaderFactory(loaderFactory);
		reporting.runReport(runParams, output);
	}

	private static final long serialVersionUID = -2503855848366525729L;
}
