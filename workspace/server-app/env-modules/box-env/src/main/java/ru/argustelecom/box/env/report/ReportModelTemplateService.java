package ru.argustelecom.box.env.report;

import ru.argustelecom.box.env.report.api.ReportModelTemplateProcessor;
import ru.argustelecom.box.env.report.api.ReportTemplateFormat;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.inf.service.DomainService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.ofNullable;
import static ru.argustelecom.box.env.report.api.ReportTemplateFormat.XLSX;
import static ru.argustelecom.box.env.report.api.ReportTemplateFormat.getReportTemplateFormatBy;

@DomainService
public class ReportModelTemplateService implements Serializable {

	private static Map<ReportTemplateFormat, Function<ReportModelTemplate, ReportModelTemplateProcessor>> processors;

	static {
		Map<ReportTemplateFormat, Function<ReportModelTemplate, ReportModelTemplateProcessor>> processors = new HashMap<>();
		processors.put(XLSX, XLSXReportModelTemplateProcessor::new);
		ReportModelTemplateService.processors = unmodifiableMap(processors);
	}

	public void processTemplate(ReportModelTemplate template) {
		checkNotNull(template);

		ofNullable(processors.get(getReportTemplateFormatBy(template.getMimeType())))
				.ifPresent(handler -> handler.apply(template).process());
	}

	private static final long serialVersionUID = -2412679551337257909L;

}
