package ru.argustelecom.box.env.report.api;

import java.util.Arrays;

public enum ReportTemplateFormat {

	//@formatter:off
	DOCX(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
	XLSX(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	HTML(".html", "text/html");
	//@formatter:on

	private String extension;
	private String mimeType;

	private ReportTemplateFormat(String extension, String mimeType) {
		this.extension = extension;
		this.mimeType = mimeType;
	}

	public static ReportTemplateFormat getReportTemplateFormatBy(String mimeType) {
		return Arrays.stream(values()).filter(tt -> tt.mimeType().equals(mimeType)).findFirst().orElse(null);
	}

	public String extension() {
		return extension;
	}

	public String mimeType() {
		return mimeType;
	}

}