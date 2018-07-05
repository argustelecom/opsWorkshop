package ru.argustelecom.box.env.report.model;

import java.util.List;

public interface HasTemplates {
	List<ReportModelTemplate> getTemplates();
	boolean addTemplate(ReportModelTemplate template);
	boolean removeTemplate(ReportModelTemplate template);
}
