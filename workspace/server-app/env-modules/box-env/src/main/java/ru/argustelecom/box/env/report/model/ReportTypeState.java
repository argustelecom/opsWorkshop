package ru.argustelecom.box.env.report.model;

import java.io.Serializable;
import java.util.Arrays;

import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.report.nls.ReportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

public enum ReportTypeState implements LifecycleState<ReportTypeState> {

	//@formatter:off
	PUBLISHED("ReportTypePublished", "published"),
	BLOCKED("ReportTypeBlocked", "blocked");
	//@formatter:on

	private String eventQualifier;
	private String key;

	ReportTypeState(String eventQualifier, String key) {
		this.eventQualifier = eventQualifier;
		this.key = key;
	}

	@Override
	public Iterable<ReportTypeState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public String getName() {
		ReportMessagesBundle messages = LocaleUtils.getMessages(ReportMessagesBundle.class);
		switch (this) {
		case PUBLISHED:
			return messages.publishedReportTypeStateName();
		case BLOCKED:
			return messages.blockedReportTypeStateName();
		default:
			throw new SystemException("Unsupported ReportTypeState");
		}
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}

	@Override
	public Serializable getKey() {
		return key;
	}
}
