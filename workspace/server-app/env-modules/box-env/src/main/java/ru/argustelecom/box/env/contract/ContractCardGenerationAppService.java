package ru.argustelecom.box.env.contract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import com.google.common.base.Preconditions;

import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.report.api.ReportContext;
import ru.argustelecom.box.env.report.api.ReportOutputFormat;
import ru.argustelecom.box.env.report.api.ReportPattern;
import ru.argustelecom.box.env.report.api.ReportProcessor;
import ru.argustelecom.box.env.report.api.ReportTemplateFormat;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class ContractCardGenerationAppService implements Serializable {

	private static final long serialVersionUID = -4099492454439956220L;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	@Inject
	private ReportProcessor processor;

	public InputStream generate(AbstractContract<?> contract, ReportModelTemplate template,
			ReportOutputFormat reportOutputFormat) throws ContractGenerationException {
		Preconditions.checkNotNull(contract, "contract is required");
		Preconditions.checkNotNull(template, "template is required");
		Preconditions.checkNotNull(reportOutputFormat, "reportOutputFormat is required");

		try {
			ByteArrayOutputStream contractCardOs = new ByteArrayOutputStream();
			processor.process(createReportPattern(template, reportOutputFormat), createReportContext(contract),
					contractCardOs);
			return new ByteArrayInputStream(contractCardOs.toByteArray());
		} catch (SQLException e) {
			throw new ContractGenerationException("Ошибка генерации карточки договора", e);
		}
	}

	private ReportPattern createReportPattern(ReportModelTemplate template, ReportOutputFormat reportOutputFormat)
			throws SQLException {
		ReportTemplateFormat templateFormat = ReportTemplateFormat.getReportTemplateFormatBy(template.getMimeType());
		InputStream templateIs = template.getBinaryStream();
		String reportName = String.format("%s - %s", template.getFileName(), dateFormat.format(new Date()));

		//@formatter:off
		return ReportPattern.builder()
					.templateFormat(templateFormat)
					.templateContent(templateIs)
					.reportOutputFormat(reportOutputFormat)
					.name(reportName)
				.build();
		//@formatter:on
	}

	private ReportContext createReportContext(AbstractContract<?> contract) {
		ReportContext reportContext = new ReportContext();
		contract.fillReportContext(reportContext);
		return reportContext;
	}

}