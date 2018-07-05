package ru.argustelecom.box.env.report.impl;

import java.io.IOException;
import java.io.Serializable;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;
import com.haulmont.yarg.reporting.RunParams;
import com.haulmont.yarg.structure.Report;
import com.haulmont.yarg.structure.ReportTemplate;
import com.haulmont.yarg.structure.impl.ReportBuilder;
import com.haulmont.yarg.structure.impl.ReportTemplateImpl;

import ru.argustelecom.box.env.report.api.ReportContext;
import ru.argustelecom.box.env.report.api.ReportPattern;
import ru.argustelecom.box.env.report.impl.data.ReportDataMapper;
import ru.argustelecom.box.env.report.impl.utils.ReportOutputFormatMappings;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.exception.SystemException;

import static ru.argustelecom.box.env.report.impl.utils.ReportOutputFormatMappings.validate;

@ApplicationService
public class DocumentReportBuilder implements Serializable {

	private static final Logger log = Logger.getLogger(DocumentReportBuilder.class);

	public Report createReport(ReportPattern template, ReportContext context) {
		validate(template.getTemplateFormat(), template.getReportOutputFormat());

		ReportBuilder reportBuilder = new ReportBuilder();
		reportBuilder.template(createTemplate(template));
		new ReportMetadata(reportBuilder, context).createMetadata();
		return reportBuilder.build();
	}

	public RunParams createReportData(ReportPattern template, ReportContext context) {
		RunParams runParams = new RunParams(createReport(template, context));
		context.forEach((bandName, reportData) -> {
			String mappedData = ReportDataMapper.map(reportData);
			log.debugv("TopLevel Band:{0} - {1}", bandName, mappedData);
			runParams.param(ReportMetadata.getDataName(bandName), mappedData);
		});
		return runParams;
	}

	private ReportTemplate createTemplate(ReportPattern pattern) {
		String reportName = getReportName(pattern);
		try {
			//@formatter:off
			return new ReportTemplateImpl(
					ReportTemplate.DEFAULT_TEMPLATE_CODE,
					reportName,
					reportName,
					pattern.getTemplateContent(),
					ReportOutputFormatMappings.OUTPUT_MAPPING.get(pattern.getReportOutputFormat())
			);
			//@formatter:on
		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

	private String getReportName(ReportPattern pattern) {
		String reportName = pattern.getName();
		if (Strings.isNullOrEmpty(reportName)) {
			reportName = ReportOutputFormatMappings.DEFAULT_REPORT_NAME;
		}
		if (!reportName.endsWith(pattern.getTemplateFormat().extension())) {
			reportName += pattern.getTemplateFormat().extension();
		}
		return reportName;
	}

	private static final long serialVersionUID = 8551948040404630647L;
}
