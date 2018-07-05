package ru.argustelecom.box.env.report;

import lombok.Getter;

@Getter
public enum ReportTypeCategory {

	//@formatter:off
	GROUP     ("group", "Группа", "fa fa-folder-open"),
	TYPE      ("type", "Тип", "icon-paper");
	//@formatter:on

	private String keyword;
	private String title;
	private String icon;

	ReportTypeCategory(String keyword, String title, String icon) {
		this.keyword = keyword;
		this.title = title;
		this.icon = icon;
	}

}
