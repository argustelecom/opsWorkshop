package ru.argustelecom.box.env.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ReportTypeTreeNodeDto {

	private Long id;
	private String name;
	private ReportTypeCategory category;
	private ReportTypeTreeNodeDto parent;

}
