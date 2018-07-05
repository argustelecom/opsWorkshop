package ru.argustelecom.box.env.report.api;

import java.io.InputStream;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * 
 */
@Getter
@Builder
@ToString
public class ReportPattern {

	@NonNull
	private ReportTemplateFormat templateFormat;

	@NonNull
	private InputStream templateContent;

	@NonNull
	private ReportOutputFormat reportOutputFormat;

	private String name;

}