package ru.argustelecom.box.env.activity.attachment.model;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import ru.argustelecom.system.inf.modelbase.SuperClass;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "attachments_context")
public class AttachmentContext extends SuperClass {

	@OneToMany(mappedBy = "context", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("creationDate desc")
	private List<Attachment> attachments = new ArrayList<>();

	@Column(length = 100)
	private String downloadPermission;

	protected AttachmentContext() {
		super();
	}

	public AttachmentContext(Long id) {
		super(id, ENT_SUPER_CLASS);
	}

	@Id
	@Override
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return super.getId();
	}

	public String getDownloadPermission() {
		return downloadPermission;
	}

	public void setDownloadPermission(String downloadPermission) {
		this.downloadPermission = downloadPermission;
	}

	public List<Attachment> getAttachments() {
		return unmodifiableList(attachments);
	}

	public boolean hasAttachment(Attachment attachment) {
		return Objects.equals(this, attachment.getContext());
	}

	public boolean addAttachment(Attachment attachment) {
		if (!hasAttachment(attachment)) {
			if (attachment.getContext() != null) {
				attachment.getContext().removeAttachment(attachment);
			}
			attachments.add(0, attachment);
			attachment.setContext(this);
			return true;
		}
		return false;
	}

	public boolean removeAttachment(Attachment attachment) {
		if (hasAttachment(attachment)) {
			attachment.setContext(null);
			attachments.remove(attachment);
			return true;
		}
		return false;
	}

	private static final long serialVersionUID = 7090417502059393738L;
}
