package ru.argustelecom.box.env.report.lifecycle;

import static ru.argustelecom.box.env.report.model.ReportTypeState.BLOCKED;
import static ru.argustelecom.box.env.report.model.ReportTypeState.PUBLISHED;

import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.report.model.ReportTypeState;
import ru.argustelecom.box.env.report.nls.ReportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@LifecycleRegistrant
public class ReportTypeLifecycle implements LifecycleFactory<ReportTypeState, ReportType> {

	@Override
	public void buildLifecycle(LifecycleBuilder<ReportTypeState, ReportType> lifecycle) {
		ReportMessagesBundle messages = LocaleUtils.getMessages(ReportMessagesBundle.class);
		lifecycle.keyword(getClass().getSimpleName()).name(messages.reportTypeLifecycleName());
		//@formatter:off
		lifecycle.route(Route.PUBLISH, Route.PUBLISH.getName())
				.from(BLOCKED)
				.to(PUBLISHED)
			.end()
		.end();

		lifecycle.route(Route.BLOCK, Route.BLOCK.getName())
				.from(PUBLISHED)
				.to(BLOCKED)
			.end()
		.end();
		//@formatter:on

	}

	enum Route {
		PUBLISH, BLOCK;

		public String getName() {
			ReportMessagesBundle messages = LocaleUtils.getMessages(ReportMessagesBundle.class);
			switch (this) {
			case PUBLISH:
				return messages.publishReportTypeRouteName();
			case BLOCK:
				return messages.blockReportTypeRouteName();
			default:
				throw new SystemException("Unsupported Route");
			}
		}
	}
}
