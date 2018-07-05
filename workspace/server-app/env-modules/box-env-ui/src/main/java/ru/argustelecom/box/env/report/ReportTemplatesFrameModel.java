package ru.argustelecom.box.env.report;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import ru.argustelecom.box.env.document.type.ReportModelTemplateDto;
import ru.argustelecom.box.env.document.type.ReportModelTemplateDtoTranslator;
import ru.argustelecom.box.env.report.model.HasTemplates;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.inf.page.upload.FileUploadHelper;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "reportTemplatesFm")
@PresentationModel
public class ReportTemplatesFrameModel implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private FileUploadHelper fileHelper;

	@Inject
	private ReportModelTemplateDtoTranslator reportModelTemplateDtoTr;

	@Getter
	private TemplateHolderDto holderDto;
	@Getter
	private List<ReportModelTemplateDto> templates = new ArrayList<>();
	private List<ReportModelTemplateDto> selectedTemplates = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public void preRender(HasTemplates templateHolder) {
		if (templateHolder != null) {
			this.holderDto = new TemplateHolderDto(templateHolder);
			this.templates = templateHolder.getTemplates().stream().map(reportModelTemplateDtoTr::translate)
					.collect(Collectors.toList());
		}
	}

	public List<ReportModelTemplateDto> getSelectedTemplates() {
		return selectedTemplates;
	}

	public void setSelectedTemplates(List<ReportModelTemplateDto> selectedTemplates) {
		this.selectedTemplates = selectedTemplates;
	}

	public void removeSelectedTemplates() {
		if (selectedTemplates != null) {
			selectedTemplates.forEach(template -> {
				holderDto.getHasTemplates(em).removeTemplate((ReportModelTemplate) template.getIdentifiable(em));
				templates.remove(template);
			});
			selectedTemplates.clear();
		}
	}

	public StreamedContent getStreamedContent(ReportModelTemplate template) throws Exception {
		if (template == null)
			return null;

		InputStream stream = template.getBinaryStream();
		String fileName = template.getFileName();
		String mimeType = fileHelper.getMimeType(template.getFileName());

		return new DefaultStreamedContent(stream, mimeType, fileName);
	}

	private static final long serialVersionUID = -7339067973736393338L;
}
