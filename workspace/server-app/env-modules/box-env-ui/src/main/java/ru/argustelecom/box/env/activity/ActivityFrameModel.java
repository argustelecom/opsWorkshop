package ru.argustelecom.box.env.activity;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import ru.argustelecom.box.env.activity.attachment.model.AttachmentContext;
import ru.argustelecom.box.env.activity.attachment.model.HasAttachments;
import ru.argustelecom.box.env.activity.comment.model.CommentContext;
import ru.argustelecom.box.env.activity.comment.model.HasComments;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("activityFrm")
@PresentationModel
public class ActivityFrameModel implements Serializable {

	private Serializable context;
	private int frameHeight;

	private ActivityType currentActivity;

	@PostConstruct
	protected void postConstruct() {
	}

	public void preRender(Serializable context, Integer frameHeight) {
		this.context = checkNotNull(context);
		this.frameHeight = frameHeight == null ? 500 : frameHeight;
	}

	public boolean isRendered() {
		return isCommentsSupported() || isAttachmentsSupported() || isHistorySupported();
	}

	public boolean isCommentsSupported() {
		return context instanceof HasComments;
	}

	public CommentContext getCommentsContext() {
		return isCommentsSupported() ? ((HasComments) context).getCommentContext() : null;
	}

	public int getCommentsHeight() {
		return frameHeight - COMMENTS_FRAME_STATIC_HEIGHT_FACTOR;
	}

	public boolean isAttachmentsSupported() {
		return context instanceof HasAttachments;
	}

	public AttachmentContext getAttachmentsContext() {
		return isAttachmentsSupported() ? ((HasAttachments) context).getAttachmentContext() : null;
	}

	public int getAttachmentsHeight() {
		return frameHeight - ATTACHMENTS_FRAME_STATIC_HEIGHT_FACTOR;
	}

	public boolean isHistorySupported() {
		return context instanceof LifecycleObject;
	}

	public LifecycleObject getHistoryContext() {
		return isHistorySupported() ? (LifecycleObject) context : null;
	}

	public ActivityType getCurrentActivity() {
		if (currentActivity == null) {
			if (isCommentsSupported()) {
				currentActivity = ActivityType.COMMENTS;
			} else if (isAttachmentsSupported()) {
				currentActivity = ActivityType.ATTACHMENTS;
			} else if (isHistorySupported()) {
				currentActivity = ActivityType.HISTORY;
			} else {
				throw new SystemException(
						"Activity context should implement one of 'HasComments', 'HasAttachments', 'LifecycleObject' or all interfaces");
			}
		}
		return currentActivity;
	}

	public void currentActivity(ActivityType currentActivity) {
		this.currentActivity = currentActivity;
	}

	public enum ActivityType {
		COMMENTS, ATTACHMENTS, HISTORY
	}

	private static final int COMMENTS_FRAME_STATIC_HEIGHT_FACTOR = 113;
	private static final int ATTACHMENTS_FRAME_STATIC_HEIGHT_FACTOR = 167;

	private static final long serialVersionUID = -8191717741302297665L;

}
