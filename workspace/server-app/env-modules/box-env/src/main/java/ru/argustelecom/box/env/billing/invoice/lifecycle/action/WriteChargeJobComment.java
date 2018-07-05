package ru.argustelecom.box.env.billing.invoice.lifecycle.action;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.activity.comment.CommentRepository;
import ru.argustelecom.box.env.billing.invoice.JobReportService;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.common.AbstractActionWriteComment;
import ru.argustelecom.box.env.party.model.role.Employee;

public abstract class WriteChargeJobComment extends AbstractActionWriteComment<ChargeJobState, ChargeJob> {

	private static final Logger log = Logger.getLogger(AbstractActionWriteComment.class);

	@Inject
	private CommentRepository commentRp;

	@Inject
	private JobReportService jobReportSvc;

	@Override
	public void execute(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {
		String content = getContent(ctx);
		if (content != null) {
			Employee author = getAuthor(ctx);
			String header = getHeader(ctx);

			commentRp.writeComment(ctx.getBusinessObject(), header, content, author);
			log.debugv("Changing of business object state was commented: {0}", content);

		} else {
			log.debug("Comment content is null. Action skipped");
		}
	}

	@Override
	protected String getContent(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {
		return jobReportSvc.createCommentBody(ctx.getBusinessObject());
	}

	@Override
	protected String getHeader(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {
		return jobReportSvc.createCommentHeader((ctx.getBusinessObject()).getState(),
				(ctx.getEndpoint().getDestination()));
	}
}
