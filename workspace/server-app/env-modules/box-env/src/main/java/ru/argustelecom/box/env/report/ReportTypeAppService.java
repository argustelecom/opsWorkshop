package ru.argustelecom.box.env.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.report.api.ReportOutputFormat;
import ru.argustelecom.box.env.report.api.ReportProcessor;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.env.report.model.ReportParams;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.report.nls.ReportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.exception.SystemException;

@ApplicationService
public class ReportTypeAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ReportProcessor reportProcessor;

	@Inject
	private ReportTypeRepository reportTypeRp;

	public InputStream generateReport(Long reportTypeId, ReportParams reportParams, Long reportModelTemplateId,
			ReportOutputFormat outputFormat) {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ReportType reportType = em.find(ReportType.class, reportTypeId);
			ReportModelTemplate template = em.find(ReportModelTemplate.class, reportModelTemplateId);
			reportProcessor.process(reportType, reportParams, template, outputFormat, os);
			return new ByteArrayInputStream(os.toByteArray());
		} catch (Exception e) {
			throw new SystemException(LocaleUtils.getMessages(ReportMessagesBundle.class).reportGenerationError(), e);
		}
	}

	public List<ReportType> findTypesWithoutGroup() {
		return reportTypeRp.findWithoutGroup();
	}

	private static final long serialVersionUID = 6864333347998304014L;
}
