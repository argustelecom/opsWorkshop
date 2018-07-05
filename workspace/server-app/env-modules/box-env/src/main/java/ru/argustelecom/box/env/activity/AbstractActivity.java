package ru.argustelecom.box.env.activity;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.modelbase.SequenceDefinition;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.modelbase.SuperClass;

@MappedSuperclass
@Access(AccessType.FIELD)
@SequenceDefinition
public abstract class AbstractActivity extends SuperClass {

	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id")
	private Employee author;

	@Temporal(TemporalType.TIMESTAMP)
	private Date editDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "editor_id")
	private Employee editor;

	@Version
	private Long version;

	protected AbstractActivity() {
		super();
	}

	protected AbstractActivity(Long id, Employee author) {
		super(id, ENT_SUPER_CLASS);
		this.author = checkNotNull(author);
		this.creationDate = new Date();
	}

	@Id
	@Override
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return super.getId();
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Employee getAuthor() {
		return author;
	}

	public Date getEditDate() {
		return editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public Employee getEditor() {
		return editor;
	}

	public void setEditor(Employee editor) {
		this.editor = editor;
	}

	public static abstract class AbstractActivityQuery<T extends AbstractActivity> extends EntityQuery<T> {

		private EntityQueryEntityFilter<T, Employee> author = createEntityFilter(AbstractActivity_.author);
		private EntityQueryEntityFilter<T, Employee> editor = createEntityFilter(AbstractActivity_.editor);
		private EntityQueryDateFilter<T> creationDate = createDateFilter(AbstractActivity_.creationDate);
		private EntityQueryDateFilter<T> editDate = createDateFilter(AbstractActivity_.editDate);

		protected AbstractActivityQuery(Class<T> entityClass) {
			super(entityClass);
		}

		public EntityQueryEntityFilter<T, Employee> author() {
			return author;
		}

		public EntityQueryEntityFilter<T, Employee> editor() {
			return editor;
		}

		public EntityQueryDateFilter<T> creationDate() {
			return creationDate;
		}

		public EntityQueryDateFilter<T> editDate() {
			return editDate;
		}
	}

	private static final long serialVersionUID = -5993188908605465077L;
}
