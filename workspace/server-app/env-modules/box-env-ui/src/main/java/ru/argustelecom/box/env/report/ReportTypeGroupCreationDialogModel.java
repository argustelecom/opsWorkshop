package ru.argustelecom.box.env.report;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.model.ReportTypeGroup;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("reportTypeGroupCreationDm")
@PresentationModel
public class ReportTypeGroupCreationDialogModel implements Serializable {

	private static final long serialVersionUID = 8889687570060584570L;

	@Inject
	private ReportTypeGroupRepository rtgRep;

	@Inject
	private ReportTypeGroupDtoTranslator reportTypeGroupDtoTr;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String keyword;

	@Setter
	private Callback<ReportTypeGroupDto> reportTypeGroupCallback;

	public void create() {
		ReportTypeGroup group = rtgRep.createReportTypeGroup(name, keyword);
		reportTypeGroupCallback.execute(reportTypeGroupDtoTr.translate(group));

		name = keyword = null;
	}

}
