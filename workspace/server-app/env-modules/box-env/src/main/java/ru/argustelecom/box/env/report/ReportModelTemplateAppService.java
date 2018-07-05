package ru.argustelecom.box.env.report;

import java.io.Serializable;

import javax.inject.Inject;

import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class ReportModelTemplateAppService implements Serializable {

	@Inject
	private ReportModelTemplateRepository reportModelTemplateRp;

	@Inject
	private ReportModelTemplateService reportModelTemplateSrv;

	public ReportModelTemplate createTemplate(String fileName, String mimeType, String desc, byte[] content) {
		ReportModelTemplate template = reportModelTemplateRp.createTemplate(fileName, mimeType, desc, content);
		reportModelTemplateSrv.processTemplate(template);
		return template;
	}

	private static final long serialVersionUID = -546861470882993118L;
}
