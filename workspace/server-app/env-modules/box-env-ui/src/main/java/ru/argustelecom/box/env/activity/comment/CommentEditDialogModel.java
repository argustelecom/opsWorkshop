package ru.argustelecom.box.env.activity.comment;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.activity.comment.model.Comment;
import ru.argustelecom.box.env.activity.comment.model.CommentContext;
import ru.argustelecom.box.env.activity.nls.ActivityMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named("commentEditDlg")
public class CommentEditDialogModel implements Serializable {

	@Inject
	private CommentRepository commentRepository;

	private CommentContext commentContext;
	private Comment comment;

	private String commentHeader;
	private String commentContent;

	public CommentContext getCommentContext() {
		return commentContext;
	}

	public void setCommentContext(CommentContext commentContext) {
		this.commentContext = commentContext;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		if (comment != null) {
			commentHeader = comment.getHeader();
			commentContent = comment.getContent();
		} else {
			commentHeader = null;
			commentContent = null;
		}
		this.comment = comment;
	}

	public String getCommentHeader() {
		return commentHeader;
	}

	public void setCommentHeader(String commentHeader) {
		this.commentHeader = commentHeader;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public boolean isNewComment() {
		return comment == null;
	}

	public void submit() {
		if (comment == null) {
			Comment newComment = commentRepository.createComment(commentHeader, commentContent); 
			commentContext.addComment(newComment);
		} else {
			commentRepository.editComment(comment, commentHeader, commentContent);
		}
	}

	public void cancel() {
		comment = null;
		commentContext = null;
		commentContent = null;
		commentHeader = null;
	}

	public String getDialogHeader() {
		ActivityMessagesBundle messages = LocaleUtils.getMessages(ActivityMessagesBundle.class);
		if (isNewComment())
			return messages.commentCreate();
		else
			return messages.commentEdit();

	}

	private static final long serialVersionUID = -2992637309035708591L;
}
