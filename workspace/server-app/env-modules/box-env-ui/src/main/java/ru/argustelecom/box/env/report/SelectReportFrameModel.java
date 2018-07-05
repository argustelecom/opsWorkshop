package ru.argustelecom.box.env.report;

import lombok.Getter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

import static ru.argustelecom.box.env.report.impl.utils.ReportOutputFormatMappings.getReportTypesBy;

@PresentationModel
@Named(value = "selectReportFm")
public class SelectReportFrameModel implements Serializable {

	private static final long serialVersionUID = 1388468643046586226L;

	@Getter
	private List<SelectItem> reports = new ArrayList<>();

	private List<ReportModelTemplate> templates;

	private Function<ReportItem, StreamedContent> exportFnc;

	@Getter
	private ReportItem selectedReport;

	public void preRender(List<ReportModelTemplate> templates, Function<ReportItem, StreamedContent> exportFnc) {
		if (!Objects.equals(templates, this.templates)) {
			this.templates = templates;
			if (templates != null)
				prepareReportList();
		}

		this.exportFnc = exportFnc;
	}

	private List<SelectItem> prepareReportList() {
		reports.clear();
		templates.forEach(template -> {
			SelectItemGroup templateGroup = createGroupBy(template);
			putReportItemsTo(templateGroup, template);
			reports.add(templateGroup);
		});
		return reports;
	}

	private static final Pattern TEMPLATE_NAME_PATTERN = Pattern.compile("^(.+)\\.([a-zA-Z]+)$");

	private SelectItemGroup createGroupBy(ReportModelTemplate template) {
		Matcher matcher = TEMPLATE_NAME_PATTERN.matcher(template.getFileName());
		return matcher.matches() ? new SelectItemGroup(matcher.group(1)) : new SelectItemGroup(template.getFileName());
	}

	private void putReportItemsTo(SelectItemGroup templateGroup, ReportModelTemplate template) {
        ResourceBundle reportBundle = LocaleUtils.getBundle("ReportBundle", getClass());

        templateGroup.setSelectItems(getReportTypesBy(template.getMimeType()).stream()
				.map(rof -> new SelectItem(new ReportItem(template, rof), reportBundle.getString("box.report.export.to") + " " + rof.name())).toArray(SelectItem[]::new));
	}


	public StreamedContent export() {
		return exportFnc.apply(selectedReport);
	}

	public void setSelectedReport(ReportItem selectedReport) {
		if (!Objects.equals(selectedReport, this.selectedReport)) {
			this.selectedReport = selectedReport;
		}
		if (selectedReport != null) {
			RequestContext.getCurrentInstance().execute("clickBtn();");
		}
	}
}