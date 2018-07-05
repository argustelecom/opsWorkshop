package ru.argustelecom.box.env.activity.attachment.model;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ru.argustelecom.box.env.activity.AbstractActivity;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "attachments")
public class Attachment extends AbstractActivity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "context_id")
	private AttachmentContext context;

	@Column(length = 128)
	private String fileName;

	@Column(length = 256)
	private String sourceFileName;

	@Column(length = 128)
	private String mimeType;

	@Lob
	private Blob attachment;

	protected Attachment() {
		super();
	}

	public Attachment(Long id, Employee author) {
		super(id, author);
	}

	public AttachmentContext getContext() {
		return context;
	}

	protected void setContext(AttachmentContext context) {
		this.context = context;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Blob getAttachment() {
		return attachment;
	}

	public void setAttachment(Blob attachment) {
		this.attachment = attachment;
	}

	public InputStream getBinaryStream() throws SQLException {
		return attachment.getBinaryStream();
	}

	public static class AttachmentQuery extends AbstractActivityQuery<Attachment> {

		private EntityQueryEntityFilter<Attachment, AttachmentContext> context = createEntityFilter(
				Attachment_.context);

		public AttachmentQuery() {
			super(Attachment.class);
		}

		public EntityQueryEntityFilter<Attachment, AttachmentContext> context() {
			return context;
		}
	}

	private static final long serialVersionUID = -6502253534966074764L;
}
