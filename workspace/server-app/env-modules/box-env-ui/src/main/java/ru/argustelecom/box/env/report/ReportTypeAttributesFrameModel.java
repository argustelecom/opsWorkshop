package ru.argustelecom.box.env.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.report.model.ReportParams;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.utils.CDIHelper;

@Named(value = "reportTypeAttributesFm")
@PresentationModel
public class ReportTypeAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = 7612405019126792528L;

	@Inject
	private ReportTypeGroupAppService rtgApp;

	@Inject
	private ReportTypeGroupDtoTranslator reportTypeGroupDtoTr;

	@Inject
	private ReportTypeDtoTranslator reportTypeDtoTr;

	@Inject
	private TypeFactory typeFactory;

	@Getter
	@Setter
	private ReportTypeDto reportType;

	@Getter
	private BusinessObjectDto<ReportType> reportTypeBODto;

	@Getter
	@Setter
	private ReportTypeGroupDto reportTypeGroup;

	@Getter
	@Setter
	private String reportTypeGroupName;

	@Getter
	private ReportParams reportParams;

	private Map<String, ReportTypeGroupDto> groups = new HashMap<>();

	@PostConstruct
	private void postConstruct() {
		List<ReportTypeGroupDto> groupDtos = rtgApp.findGroups().stream().map(g -> reportTypeGroupDtoTr.translate(g))
				.collect(Collectors.toList());
		for (ReportTypeGroupDto g : groupDtos) {
			groups.put(g.getObjectName(), g);
		}
	}

	public void preRender(BusinessObjectDto<ReportType> reportTypeDto, ReportTypeGroupDto reportTypeGroup) {
		this.reportTypeBODto = reportTypeDto;
		this.reportType = reportTypeDto != null ? reportTypeDtoTr.translate(reportTypeDto.getIdentifiable()) : null;
		this.reportTypeGroup = reportTypeGroup;
		reportTypeGroupName = reportType != null && reportType.getReportTypeGroup() != null
				? reportType.getReportTypeGroup().getObjectName()
				: null;
	}

	public void updateReportType() {
		ReportTypeGroupDto newGroup = groups.get(reportTypeGroupName);
		ReportTypeGroupDto prevGroup = reportType.getReportTypeGroup();
		reportType.setReportTypeGroup(newGroup);

		Long groupId = reportType.getReportTypeGroup() != null ? reportType.getReportTypeGroup().getId() : null;
		rtgApp.changeReportType(reportType.getId(), groupId, reportType.getObjectName(), reportType.getDescription());

		CDIHelper.fireEvent(new ReportTypeEditingEvent(reportType, prevGroup));
	}

	public void updateReportTypeGroup() {
		rtgApp.changeReportTypeGroup(reportTypeGroup.getId(), reportTypeGroup.getObjectName(),
				reportTypeGroup.getKeyword());
	}

	public void onOpenBuildReport() {
		reportParams = reportType != null
				? typeFactory.createInstance(reportTypeBODto.getIdentifiable(), ReportParams.class)
				: null;
		RequestContext.getCurrentInstance().execute("PF('reportTypeExportDlgVar').show()");
		RequestContext.getCurrentInstance().update("report_type_export_form");
	}

	public List<String> getGroupsNames() {
		List<String> groupsNamesCopy = new ArrayList<>(groups.keySet());
		if (reportTypeGroupName != null) {
			groupsNamesCopy.remove(reportTypeGroupName);
		}
		return groupsNamesCopy;
	}

	@Getter
	@AllArgsConstructor
	static class ReportTypeEditingEvent {
		private ReportTypeDto reportType;
		private ReportTypeGroupDto prevGroupDto;
	}

}
