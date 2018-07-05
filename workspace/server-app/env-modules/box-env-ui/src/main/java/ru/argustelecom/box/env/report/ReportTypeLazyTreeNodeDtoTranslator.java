package ru.argustelecom.box.env.report;

import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.report.model.ReportTypeGroup;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ReportTypeLazyTreeNodeDtoTranslator {

	public ReportTypeTreeNodeDto translate(ReportType type) {
		ReportTypeTreeNodeDto parent = type.getGroup() != null ? translate(type.getGroup()) : null;
		//@formatter:off
		return ReportTypeTreeNodeDto.builder()
					.id(type.getId())
					.name(type.getObjectName())
					.category(ReportTypeCategory.TYPE)
					.parent(parent)
				.build();
		//@formatter:on
	}

	public ReportTypeTreeNodeDto translate(ReportTypeGroup group) {
		ReportTypeTreeNodeDto parent = group.getParent() != null ? translate(group.getParent()) : null;
		//@formatter:off
		return ReportTypeTreeNodeDto.builder()
					.id(group.getId())
					.name(group.getObjectName())
					.category(ReportTypeCategory.GROUP)
					.parent(parent)
				.build();
		//@formatter:on
	}

	public ReportTypeTreeNodeDto translate(ReportTypeDto type) {
		ReportTypeTreeNodeDto parent = type.getReportTypeGroup() != null ? translate(type.getReportTypeGroup()) : null;
		//@formatter:off
		return ReportTypeTreeNodeDto.builder()
					.id(type.getId())
					.name(type.getObjectName())
					.category(ReportTypeCategory.TYPE)
					.parent(parent)
				.build();
		//@formatter:on
	}

	public ReportTypeTreeNodeDto translate(ReportTypeGroupDto group) {
		ReportTypeTreeNodeDto parent = group.getParent() != null ? translate(group.getParent()) : null;
		//@formatter:off
		return ReportTypeTreeNodeDto.builder()
					.id(group.getId())
					.name(group.getObjectName())
					.category(ReportTypeCategory.GROUP)
					.parent(parent)
				.build();
		//@formatter:on
	}

}
