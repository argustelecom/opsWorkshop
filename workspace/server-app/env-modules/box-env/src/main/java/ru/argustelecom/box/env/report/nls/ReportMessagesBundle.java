package ru.argustelecom.box.env.report.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface ReportMessagesBundle {

	@Message("Опубликовано")
	String publishedReportTypeStateName();

	@Message("Заблокировано")
	String blockedReportTypeStateName();

	@Message("Жизненый цикл типа отчета")
	String reportTypeLifecycleName();

	@Message("Опубликовать")
	String publishReportTypeRouteName();

	@Message("Заблокировать")
	String blockReportTypeRouteName();

	@Message("Ошибка генерации отчета")
	String reportGenerationError();
}
