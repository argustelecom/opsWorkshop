package ru.argustelecom.box.env.document.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import lombok.Getter;
import ru.argustelecom.box.env.activity.attachment.model.AttachmentContext;
import ru.argustelecom.box.env.activity.attachment.model.HasAttachments;
import ru.argustelecom.box.env.activity.comment.model.CommentContext;
import ru.argustelecom.box.env.activity.comment.model.HasComments;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class Document<T extends DocumentType> extends TypeInstance<T> implements HasComments, HasAttachments {

	private static final long serialVersionUID = 8285967224809961696L;

	public static final int MAX_NUMBER_LENGTH = 64;

	@Column(length = MAX_NUMBER_LENGTH, nullable = false)
	private String documentNumber;

	@Temporal(TemporalType.DATE)
	private Date documentDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "comment_context_id", insertable = true, updatable = false)
	private CommentContext commentContext;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_context_id", insertable = true, updatable = false)
	private AttachmentContext attachmentContext;

	@Getter
	@Version
	private Long version;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected Document() {
		super();
	}

	/**
	 * Конструктор предназначен для инстанцирования спецификацией. Не делай этот конструктор публичным. Не делай других
	 * публичных конструкторов. Экземпляры спецификаций должны инстанцироваться сугубо спецификацией для обеспечения
	 * корректной инициализации пользовательских свойств или отношений между спецификацией и ее экземпляром.
	 * 
	 * @param id
	 *            - идентификатор экземпляра спецификации, должен быть получен при помощи соответствующего генератора
	 *            через сервис {@link IdSequenceService}
	 * 
	 * @see IdSequenceService
	 * @see Type#createInstance(Class, Long)
	 */
	protected Document(Long id) {
		super(id);
		this.commentContext = new CommentContext(id);
		this.attachmentContext = new AttachmentContext(id);
		this.creationDate = new Date();
	}

	@Override
	public String getObjectName() {
		return documentNumber;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String number) {
		this.documentNumber = number;
	}

	public Date getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(Date date) {
		this.documentDate = date;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public AttachmentContext getAttachmentContext() {
		return attachmentContext;
	}

	@Override
	public CommentContext getCommentContext() {
		return commentContext;
	}

	public abstract static class DocumentQuery<T extends DocumentType, I extends Document<T>>
			extends TypeInstanceQuery<T, I> {

		private EntityQueryStringFilter<I> documentNumber;
		private EntityQueryDateFilter<I> documentDate;
		private EntityQueryDateFilter<I> creationDate;

		public DocumentQuery(Class<I> entityClass) {
			super(entityClass);
			documentNumber = createStringFilter(Document_.documentNumber);
			documentDate = createDateFilter(Document_.documentDate);
			creationDate = createDateFilter(Document_.creationDate);
		}

		public EntityQueryStringFilter<I> documentNumber() {
			return documentNumber;
		}

		public EntityQueryDateFilter<I> documentDate() {
			return documentDate;
		}

		public EntityQueryDateFilter<I> creationDate() {
			return creationDate;
		}
	}
}
