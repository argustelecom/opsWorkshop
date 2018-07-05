package ru.argustelecom.box.env.activity.comment.model;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import ru.argustelecom.system.inf.modelbase.SuperClass;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "comments_context")
public class CommentContext extends SuperClass {

	@OneToMany(mappedBy = "context", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("creationDate desc")
	private List<Comment> comments = new ArrayList<>();
	
	protected CommentContext() {
		super();
	}

	public CommentContext(Long id) {
		super(id, ENT_SUPER_CLASS);
	}

	@Id
	@Override
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return super.getId();
	}

	public List<Comment> getComments() {
		return unmodifiableList(comments);
	}

	public boolean hasComment(Comment comment) {
		return Objects.equals(this, comment.getContext());
	}

	public boolean addComment(Comment comment) {
		if (!hasComment(comment)) {
			if (comment.getContext() != null) {
				comment.getContext().removeComment(comment);
			}
			comments.add(0, comment);
			comment.setContext(this);
			return true;
		}
		return false;
	}

	public boolean removeComment(Comment comment) {
		if (hasComment(comment)) {
			comment.setContext(null);
			comments.remove(comment);
			return true;
		}
		return false;
	}

	private static final long serialVersionUID = 5234916010056314633L;
}
