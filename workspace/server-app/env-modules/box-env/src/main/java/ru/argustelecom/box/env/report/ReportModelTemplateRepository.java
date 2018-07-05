package ru.argustelecom.box.env.report;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.hibernate.engine.spi.ArgusSessionImplementor;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

@Repository
public class ReportModelTemplateRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public ReportModelTemplate createTemplate(String fileName, String mimeType, String description, byte[] content) {
		ReportModelTemplate template = new ReportModelTemplate(idSequence.nextValue(ReportModelTemplate.class));
		template.setFileName(fileName);
		template.setMimeType(mimeType);
		template.setDescription(description);
		template.setTemplate(((ArgusSessionImplementor) em.getDelegate()).getLobHelper().createBlob(content));

		return template;
	}

	private static final long serialVersionUID = -8369335782264071214L;
}
