package ru.argustelecom.box.env.activity.comment;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import ru.argustelecom.box.env.activity.comment.model.Comment;
import ru.argustelecom.box.env.activity.comment.model.CommentContext;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("commentsFrm")
@PresentationModel
public class CommentFrameModel implements Serializable {

	@PersistenceContext
	private EntityManager em;

	private CommentContext context;

	private Parser markdownParser;
	private HtmlRenderer htmlRenderer;
	private Map<Comment, String> commentHtmlCache = new HashMap<>();

	private Date filterFrom;
	private Date filterTo;
	private boolean filtered;
	private List<Comment> filteredComments;

	@PostConstruct
	protected void postConstruct() {
	}

	public void preRender(CommentContext context) {
		this.context = checkNotNull(context);
	}

	public CommentContext getContext() {
		return context;
	}

	public List<Comment> getComments() {
		if (context == null)
			return Collections.emptyList();

		if (filteredComments == null && filtered) {
			filteredComments = context.getComments().stream().filter(c -> {
				if (filterFrom != null && filterFrom.compareTo(c.getCreationDate()) > 0) {
					return false;
				}
				if (filterTo != null && filterTo.compareTo(c.getCreationDate()) < 0) {
					return false;
				}
				return true;
			}).collect(toList());
		}

		return filteredComments == null ? context.getComments() : filteredComments;
	}

	public Date getFilterFrom() {
		return filterFrom;
	}

	public void setFilterFrom(Date filterFrom) {
		this.filterFrom = filterFrom;
	}

	public Date getFilterTo() {
		return filterTo;
	}

	public void setFilterTo(Date filterTo) {
		this.filterTo = filterTo;
	}

	public boolean isFiltered() {
		return filtered;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	public void filter() {
		if (filterFrom != null || filterTo != null) {
			filteredComments = null;
			filtered = true;
		}
	}

	public void clearFilters() {
		if (filtered) {
			filterFrom = null;
			filterTo = null;
			filtered = false;
			filteredComments = null;
		}
	}

	public void removeComment(Comment comment) {
		if (context.removeComment(comment)) {
			commentHtmlCache.remove(comment);
		}
	}

	public void clearCommentHtml(Comment comment) {
		commentHtmlCache.remove(comment);
	}

	public String getCommentHtml(Comment comment) {
		String htmlText = commentHtmlCache.get(comment);
		if (htmlText == null) {
			htmlText = getHtmlRenderer().render(getMarkdownParser().parse(comment.getContent()));
			commentHtmlCache.put(comment, htmlText);
		}
		return htmlText;
	}

	public Parser getMarkdownParser() {
		if (markdownParser == null) {
			markdownParser = Parser.builder().build();
		}
		return markdownParser;
	}

	public HtmlRenderer getHtmlRenderer() {
		if (htmlRenderer == null) {
			htmlRenderer = HtmlRenderer.builder().build();
		}
		return htmlRenderer;
	}

	private static final long serialVersionUID = 4891700666611667111L;
}
