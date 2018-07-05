package ru.argustelecom.box.env.report.model;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ru.argustelecom.box.inf.modelbase.SequenceDefinition;
import ru.argustelecom.system.inf.modelbase.SuperClass;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "report_model_template")
@SequenceDefinition(name = "system.gen_directory_id")
public class ReportModelTemplate extends SuperClass {

	@Column(length = 128)
	private String fileName;

	@Column(length = 128)
	private String mimeType;

	@Column(length = 256)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Lob
	private Blob template;

	protected ReportModelTemplate() {
	}

	public ReportModelTemplate(Long id) {
		super(id, SuperClass.ENT_SUPER_CLASS);
		creationDate = new Date();
	}

	@Id
	@Override
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return super.getId();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Blob getTemplate() {
		return template;
	}

	public void setTemplate(Blob template) {
		this.template = template;
	}

	public InputStream getBinaryStream() throws SQLException {
		return template.getBinaryStream();
	}

	private static final long serialVersionUID = -8399806863971906631L;

}