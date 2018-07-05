package ru.argustelecom.box.env.report;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ru.argustelecom.system.inf.dataloader.LazyTreeNodeLoader;

public class ReportTypeLazyTreeNodeLoader implements LazyTreeNodeLoader<ReportTypeTreeNodeDto> {

	private ReportTypeGroupAppService rtgApp;
	private ReportTypeLazyTreeNodeDtoTranslator translator;

	public ReportTypeLazyTreeNodeLoader(ReportTypeGroupAppService rtgApp, ReportTypeLazyTreeNodeDtoTranslator tr) {
		this.rtgApp = rtgApp;
		this.translator = tr;
	}

	@Override
	public List<ReportTypeTreeNodeDto> loadChildren(ReportTypeTreeNodeDto node) {
		if (node.getCategory() == ReportTypeCategory.TYPE)
			return Collections.emptyList();

		return rtgApp.findReportTypesBy(node.getId()).stream().map(t -> translator.translate(t))
				.collect(Collectors.toList());
	}
}
