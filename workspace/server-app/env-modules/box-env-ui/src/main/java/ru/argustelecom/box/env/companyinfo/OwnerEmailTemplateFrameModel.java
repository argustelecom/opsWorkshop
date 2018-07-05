package ru.argustelecom.box.env.companyinfo;

import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Objects;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.party.OwnerAppService;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.inf.page.upload.FileUploadHelper;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "ownerEmailTemplateFm")
@PresentationModel
public class OwnerEmailTemplateFrameModel implements Serializable {

	@Inject
	private OwnerEmailTemplateDtoTranslator emailTemplateForBillDtoTr;

	@Inject
	private OwnerAppService ownerAs;

	@Inject
	private FileUploadHelper fileHelper;

	@Getter
	private OwnerEmailTemplateDto owner;

	public void preRender(Owner owner) {
		if (this.owner == null || !Objects.equals(this.owner.getId(), owner.getId())) {
			this.owner = emailTemplateForBillDtoTr.translate(owner);
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		if (event.getFile().getSize() == 0) {
			OverallMessagesBundle messages = getMessages(OverallMessagesBundle.class);
			Notification.error(messages.attemptToDownloadEmptyFile(), messages.cannotAttachEmptyFile());
			FacesContext.getCurrentInstance().validationFailed();
			return;
		}

		String fileName = event.getFile().getFileName();
		ownerAs.changeMailTemplate(owner.getId(), fileName, event.getFile().getContents());
		owner.setTemplateName(fileName);
		owner.setTemplate(ownerAs.getTemplate(owner.getId()));
	}

	public StreamedContent download() throws Exception {
		if (owner.getTemplateName() == null)
			return null;

		InputStream is = owner.getTemplate().getBinaryStream();
		String fileName = owner.getTemplateName();
		String mimeType = fileHelper.getMimeType(fileName);

		return new DefaultStreamedContent(is, mimeType, fileName);
	}

	public void remove() {
		owner.setTemplateName(null);
		owner.setTemplate(null);
		ownerAs.removeMailTemplate(owner.getId());
	}

	private static final long serialVersionUID = 7620133189327638883L;
}