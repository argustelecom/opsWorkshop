package ru.argustelecom.box.env.activity.comment.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import ru.argustelecom.box.env.activity.AbstractActivity;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "comments")
public class Comment extends AbstractActivity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "context_id")
	private CommentContext context;

	@Column
	private String header;

	@Size(max = 5000)
	@Column(columnDefinition = "text")
	private String content;

	protected Comment() {
		super();
	}

	public Comment(Long id, Employee author) {
		super(id, author);
	}

	public Comment(Long id, Employee author, String content) {
		super(id, author);
		this.content = content;
	}

	public CommentContext getContext() {
		return context;
	}

	protected void setContext(CommentContext context) {
		this.context = context;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public static class CommentQuery extends AbstractActivityQuery<Comment> {

		private EntityQueryEntityFilter<Comment, CommentContext> context = createEntityFilter(Comment_.context);

		public CommentQuery() {
			super(Comment.class);
		}

		public EntityQueryEntityFilter<Comment, CommentContext> context() {
			return context;
		}
	}

	private static final long serialVersionUID = -5993188908605465077L;
}
