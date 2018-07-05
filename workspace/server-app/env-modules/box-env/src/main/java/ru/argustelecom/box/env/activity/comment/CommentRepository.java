package ru.argustelecom.box.env.activity.comment;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.activity.comment.model.Comment;
import ru.argustelecom.box.env.activity.comment.model.HasComments;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.login.EmployeePrincipal;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class CommentRepository implements Serializable {

	private static final long serialVersionUID = -7103343321391307578L;

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private IdSequenceService sequence;

	public void writeComment(HasComments context, String header, String content) {
		writeComment(context, header, content, getCurrentEmployee());
	}

	public void writeComment(HasComments context, String header, String content, Employee author) {
		Comment comment = createComment(header, content, author);
		context.getCommentContext().addComment(comment);
	}

	public Comment createComment(String header, String content) {
		return createComment(header, content, getCurrentEmployee());
	}

	public Comment createComment(String header, String content, Employee author) {
		Comment comment = new Comment(sequence.nextValue(Comment.class), author);
		comment.setHeader(header);
		comment.setContent(content);

		return comment;
	}

	public void editComment(Comment comment, String newHeader, String newContent) {
		editComment(comment, newHeader, newContent, getCurrentEmployee());
	}

	public void editComment(Comment comment, String newHeader, String newContent, Employee editor) {
		comment.setEditDate(new Date());
		comment.setEditor(editor);
		comment.setHeader(newHeader);
		comment.setContent(newContent);
	}

	protected Employee getCurrentEmployee() {
		EmployeePrincipal principal = checkNotNull(EmployeePrincipal.instance());
		return em.find(Employee.class, principal.getEmployeeId());
	}

}
