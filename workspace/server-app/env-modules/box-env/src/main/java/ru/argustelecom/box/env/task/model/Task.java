package ru.argustelecom.box.env.task.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.criteria.Predicate;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.activity.attachment.model.AttachmentContext;
import ru.argustelecom.box.env.activity.attachment.model.HasAttachments;
import ru.argustelecom.box.env.activity.comment.model.CommentContext;
import ru.argustelecom.box.env.activity.comment.model.HasComments;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;

@Entity
@Table(schema = "system")
@Access(AccessType.FIELD)
@Setter
@Getter
public class Task extends BusinessObject implements LifecycleObject<TaskState>, HasComments, HasAttachments {

	public static final int MAX_NUMBER_LENGTH = 64;

	@Column(length = MAX_NUMBER_LENGTH)
	private String number;

	@Enumerated(EnumType.STRING)
	private TaskType taskType;

	@ManyToOne
	@JoinColumn(name = "subscription_id")
	private Subscription subscription;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@ManyToOne
	@JoinColumn(name = "assignee_id")
	private Employee assignee;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createDateTime;

	private String comment;

	@Enumerated(EnumType.STRING)
	private TaskState state;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "comment_context_id", insertable = true, updatable = false)
	private CommentContext commentContext;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_context_id", insertable = true, updatable = false)
	private AttachmentContext attachmentContext;

	protected Task() {
	}

	public Task(Long id) {
		super(id);
		this.commentContext = new CommentContext(id);
		this.attachmentContext = new AttachmentContext(id);
	}

	@Override
	public TaskState getState() {
		return state;
	}

	@Override
	public void setState(TaskState state) {
		this.state = state;
	}

	public static class TaskQuery extends EntityQuery<Task> {

		public TaskQuery() {
			super(Task.class);
		}

		public Predicate byNumber(String value) {
			return value == null ? null
					: criteriaBuilder().like(criteriaBuilder().upper(root().get(Task_.number)),
							String.format("%%%s%%", value.toUpperCase()));
		}

		public Predicate byTaskType(TaskType value) {
			return value == null ? null : criteriaBuilder().equal(root().get(Task_.taskType), value);
		}

		public Predicate byFromCreationDate(Date value) {
			return createDateFilter(Task_.createDateTime).greaterOrEqualTo(value);
		}

		public Predicate byToCreationDate(Date value) {
			return createDateFilter(Task_.createDateTime).lessOrEqualTo(value);
		}

		public Predicate byRole(Role value) {
			return createFilter(Task_.role).equal(value);
		}

		public Predicate byEmployeeIsNull() {
			return criteriaBuilder().isNull(root().get(Task_.assignee));
		}

		public Predicate byEmployee(Employee value) {
			return createFilter(Task_.assignee).equal(value);
		}

		public Predicate byState(TaskState value) {
			return value == null ? null : criteriaBuilder().equal(root().get(Task_.state), value);
		}

	}

	private static final long serialVersionUID = -7752036536683313575L;

	@Override
	public AttachmentContext getAttachmentContext() {
		return attachmentContext;
	}

	@Override
	public CommentContext getCommentContext() {
		return commentContext;
	}

}
