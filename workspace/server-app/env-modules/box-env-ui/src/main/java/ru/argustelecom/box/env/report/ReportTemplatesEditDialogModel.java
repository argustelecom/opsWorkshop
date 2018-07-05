package ru.argustelecom.box.env.report;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;

import ru.argustelecom.box.env.document.type.ReportModelTemplateDto;
import ru.argustelecom.box.env.document.type.ReportModelTemplateDtoTranslator;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.page.upload.FileUploadHelper;
import ru.argustelecom.box.inf.page.upload.FileUploadHelper.UploadedFileInfo;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named("reportTemplatesDm")
public class ReportTemplatesEditDialogModel implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private FileUploadHelper fileHelper;

	@Inject
	private ReportModelTemplateAppService reportModelTemplateAppSrv;

	@Inject
	private ReportModelTemplateDtoTranslator reportModelTemplateDtoTranslator;

	private UploadedFileInfo templateFile;
	@Setter
	private TemplateHolderDto holderDto;
	@Getter
	private ReportModelTemplateDto template;
	@Getter
	@Setter
	private String templateDesc;
	@Getter
	private String templateFileName;


	public boolean isNewTemplate() {
		return template == null;
	}

	public void setTemplate(ReportModelTemplateDto template) {
		if (template != null) {
			templateFileName = template.getFileName();
			templateDesc = template.getDescription();
		} else {
			templateFileName = null;
			templateDesc = null;
		}
		this.template = template;
		this.templateFile = null;
	}

	public boolean canSubmit() {
		return !isNewTemplate() || templateFile != null;
	}

	@SuppressWarnings("unchecked")
	public void submit() {
		if (isNewTemplate()) {
			ReportModelTemplate newTemplate = reportModelTemplateAppSrv.createTemplate(templateFile.getFileName(),
					templateFile.getMimeType(), templateDesc, templateFile.getBytes());
			holderDto.getHasTemplates(em).addTemplate(newTemplate);
			holderDto.getTemplates().add(reportModelTemplateDtoTranslator.translate(newTemplate));
		} else {
			((ReportModelTemplate) template.getIdentifiable(em)).setDescription(templateDesc);
		}
	}

	public void cancel() {
		template = null;
		templateDesc = null;
		templateFile = null;
	}

	public void handleFileUpload(FileUploadEvent event) throws Exception {
		if (event.getFile().getSize() == 0) {
            OverallMessagesBundle messages = LocaleUtils.getMessages(OverallMessagesBundle.class);
            FacesContext.getCurrentInstance().validationFailed();
			Notification.error(messages.attemptToDownloadEmptyFile(), messages.cannotAttachEmptyFile());
			return;
		}
		this.templateFile = fileHelper.getUploadedFileInfo(event);
		this.templateFileName = this.templateFile.getFileName();
	}

	private static final long serialVersionUID = -5257962816815017927L;
}
