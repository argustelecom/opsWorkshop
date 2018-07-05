package ru.argustelecom.box.env.activity.attachment;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ru.argustelecom.box.env.activity.attachment.model.Attachment;
import ru.argustelecom.box.env.activity.attachment.model.AttachmentContext;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.page.upload.FileUploadHelper;
import ru.argustelecom.box.inf.page.upload.FileUploadHelper.UploadedFileInfo;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("attachmentsFrm")
@PresentationModel
public class AttachmentFrameModel implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private AttachmentRepository attachmentRepository;

	@Inject
	private FileUploadHelper fileHelper;

	private AttachmentContext context;

	@PostConstruct
	protected void postConstruct() {
	}

	public void preRender(AttachmentContext context) {
		this.context = checkNotNull(context);
	}

	public AttachmentContext getContext() {
		return context;
	}

	public List<Attachment> getAttachments() {
		return context != null ? context.getAttachments() : emptyList();
	}

	public void removeAttachment(Attachment attachment) {
		context.removeAttachment(attachment);
	}

	public void handleFileUpload(FileUploadEvent event) throws Exception {
		if (event.getFile().getSize() == 0) {
			OverallMessagesBundle messages = LocaleUtils.getMessages(OverallMessagesBundle.class);

			FacesContext.getCurrentInstance().validationFailed();
			Notification.error(messages.attemptToDownloadEmptyFile(), messages.cannotAttachEmptyFile());
			return;
		}

		UploadedFileInfo file = fileHelper.getUploadedFileInfo(event);
		Attachment attachment = attachmentRepository.createAttachment(file.getFileName(), file.getFileSource(),
				file.getMimeType(), file.getBytes());
		context.addAttachment(attachment);
	}

	public StreamedContent getStreamedContent(Attachment attachment) throws Exception {
		return new DefaultStreamedContent(attachment.getBinaryStream(), attachment.getMimeType(),
				attachment.getFileName());
	}

	private static final long serialVersionUID = 4051798338509144508L;
}
