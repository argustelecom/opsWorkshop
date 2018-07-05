package ru.argustelecom.box.env.lifecycle.common;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.activity.comment.CommentRepository;
import ru.argustelecom.box.env.activity.comment.model.HasComments;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.nls.LifecycleMessagesBundle;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.login.EmployeePrincipal;
import ru.argustelecom.box.inf.nls.LocaleUtils;

public abstract class AbstractActionWriteComment<S extends LifecycleState<S>, O extends LifecycleObject<S> & HasComments>
		implements LifecycleCdiAction<S, O> {

	private static final Logger log = Logger.getLogger(AbstractActionWriteComment.class);

	@Inject
	private CommentRepository commentRp;

	@PersistenceContext
	private EntityManager em;

	@Override
	public void execute(ExecutionCtx<S, ? extends O> ctx) {
		String content = getContent(ctx);
		if (!Strings.isNullOrEmpty(content)) {
			Employee author = getAuthor(ctx);
			String header = getHeader(ctx);

			commentRp.writeComment(ctx.getBusinessObject(), header, content, author);
			log.debugv("Changing of business object state was commented: {0}", content);

		} else {
			log.debug("Comment content is null or empty. Action skipped");
		}
	}

	protected Employee getAuthor(ExecutionCtx<S, ? extends O> ctx) {
		EmployeePrincipal principal = checkNotNull(EmployeePrincipal.instance());
		return em.find(Employee.class, principal.getEmployeeId());
	}

	protected String getHeader(ExecutionCtx<S, ? extends O> ctx) {
		O object = ctx.getBusinessObject();
		S oldState = ctx.getBusinessObject().getState();
		S newState = ctx.getEndpoint().getDestination();

		LifecycleMessagesBundle messages = LocaleUtils.getMessages(LifecycleMessagesBundle.class);
		return messages.stateTransition(object.toString(), oldState.getName(), newState.getName());
	}

	protected abstract String getContent(ExecutionCtx<S, ? extends O> ctx);
}
