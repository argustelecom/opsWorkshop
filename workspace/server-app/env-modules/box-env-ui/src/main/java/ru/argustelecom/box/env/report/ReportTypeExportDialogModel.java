package ru.argustelecom.box.env.report;

import static ru.argustelecom.box.env.dto.DefaultDtoConverterUtils.translate;

import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.report.api.ReportOutputFormat;
import ru.argustelecom.box.env.report.model.ReportParams;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.env.type.model.TypePropertyAccessor;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("reportTypeExportDm")
@PresentationModel
public class ReportTypeExportDialogModel implements Serializable {

	private static final String REPORT_DATE_FORMAT = "dd.MM.yyyy HH.mm.ss";

	@Inject
	private ReportModelTemplateDtoTranslator reportModelTemplateDtoTr;

	@Inject
	private TypeFactory typeFactory;

	@Inject
	private ReportTypeAppService reportTypeAppSrv;

	@Getter
	private BusinessObjectDto<ReportType> reportType;

	@Getter
	@Setter
	private ReportModelTemplateDto selectedTemplate;

	@Getter
	private ReportParams reportParams;

	@Getter
	private List<ReportModelTemplateDto> templates;

	@Getter
	@Setter
	private ReportOutputFormat selectedOutputFormat;

	@Getter
	private List<TypePropertyAccessor<?>> accessors;

	private SimpleDateFormat dateFormat;

	@PostConstruct
	private void postConstruct() {
		this.dateFormat = new SimpleDateFormat(REPORT_DATE_FORMAT, LocaleUtils.getCurrentLocale());
	}

	public void setReportType(BusinessObjectDto<ReportType> reportType) {
		this.reportType = reportType;
		templates = translate(reportModelTemplateDtoTr, reportType.getIdentifiable().getTemplates());
		selectedTemplate = !templates.isEmpty() ? templates.get(0) : null;
	}

	public void setReportParams(ReportParams reportParams) {
		this.reportParams = reportParams;
		accessors = typeFactory.createAccessors(reportParams);
	}

	public StreamedContent onExport() {
		InputStream is = reportTypeAppSrv.generateReport(reportType.getId(), reportParams, selectedTemplate.getId(),
				selectedOutputFormat);
		return new DefaultStreamedContent(is, selectedTemplate.getMimeType(),
				String.format("%s - %s.%s",
						selectedTemplate.getName().substring(0, selectedTemplate.getName().lastIndexOf('.')),
						dateFormat.format(new Date()), selectedOutputFormat.name().toLowerCase()));
	}

	private static final long serialVersionUID = 4967370289971717738L;
}
