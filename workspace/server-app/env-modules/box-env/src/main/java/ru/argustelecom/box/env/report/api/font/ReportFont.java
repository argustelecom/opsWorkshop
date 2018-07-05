package ru.argustelecom.box.env.report.api.font;

import java.io.Serializable;

public interface ReportFont extends Serializable {

	String getName();

	String getPath();

	String getEncoding();

	boolean isEmbedded();

}
